package com.undef.superahorro.BossioCorrea.data.repository

import android.graphics.Bitmap
import android.util.Base64
import com.undef.superahorro.BossioCorrea.domain.model.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

data class TicketAnalizado(
    val supermercado : String,
    val fecha        : String,
    val hora         : String,
    val total        : String,
    val productos    : List<Producto>
)

sealed class GroqResult {
    data class Exito(val ticket: TicketAnalizado) : GroqResult()
    data class Error(val mensaje: String)         : GroqResult()
}

class GroqRepository {

    private val apiKey = "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"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun analizarTicket(bitmap: Bitmap): GroqResult = withContext(Dispatchers.IO) {
        try {
            val base64 = bitmapToBase64(bitmap)

            val prompt = """
Sos un experto en leer tickets de supermercados y almacenes de Argentina.
Tu única tarea: extraer exactamente los productos que aparecen en el ticket. Devolvé un JSON. Sin texto extra. Sin bloques de código.

════════════════════════════════════════════
ESTRUCTURA DE UN TICKET ARGENTINO
════════════════════════════════════════════

Cada producto ocupa DOS líneas:
  LÍNEA 1 (ignorar): "1,0000 (00) x 4500,0000"  → solo indica cantidad × precio unitario. NO ES UN PRODUCTO.
  LÍNEA 2 (el producto): "COCA COLA RETORNABLE    4500,00"  → nombre + precio total de esa línea.

ZONA DE PRODUCTOS: empieza después de la línea "TIQUE" o "Fecha/Hora" y TERMINA exactamente en la línea que dice "TOTAL" o "Subtotal".
TODO lo que está desde "TOTAL" hacia abajo NO es un producto. No lo incluyas.

SEÑAL DE FIN DE PRODUCTOS: la palabra "TOTAL" seguida de un número grande es el límite. Nada después de eso es un producto.

EJEMPLO REAL de este ticket (LI XIAOYAN):
  Productos:
    1,0000 (00) x 4500,0000
    COCA COLA RETORNABLE    4500,00         → producto 1
    1,0000 (00) x 1800,0000
    SODA SIFON SALDAN 2L    1800,00         → producto 2
    1,0000 (00) x 4500,0000
    COCA COLA RETORNABLE    4500,00         → producto 3
    1,0000 (00) x 1200,0000
    ALMACEN                 1200,00         → producto 4
    1,0000 (00) x 1200,0000
    BOLSA RESIDUOS 45*60    1200,00         → producto 5
    1,0000 (00) x 1500,0000
    BOLSA CONSORCIO CREE    1500,00         → producto 6
  TOTAL                     14700,00        → STOP. Fin de productos. No crear más productos después de esta línea.
  RECIBI/MOS                               → ignorar
  EFECTIVO                                 → ignorar
  Efectivo             14700,00            → ignorar (este número NO es un producto)
  Suma de sus pagos    14700,00            → ignorar
  Su Vuelto            0,00                → ignorar

El resultado CORRECTO para este ticket son exactamente 6 productos.
El número 14700,00 es el TOTAL, no un producto. El número que aparece después de EFECTIVO tampoco es un producto.

════════════════════════════════════════════
REGLAS CRÍTICAS
════════════════════════════════════════════

1. CONTÁ las pares de líneas (línea cantidad + línea nombre) que hay ANTES del TOTAL. Esa es la cantidad exacta de productos.
2. NO agregues productos después de ver la palabra TOTAL, Subtotal, RECIBI/MOS, Efectivo, Su Vuelto.
3. El nombre del producto es el texto COMPLETO de la Línea 2 antes del precio. Nunca lo cortes.
4. Si hay descuentos (ej: "Descuento JAMON COCIDO -318,65"), restá el descuento al precio del producto correspondiente y ponelo en descripcion: "Descuento aplicado: $318.65".
5. Precios: el ticket usa coma decimal (1500,00). En el JSON usá punto (1500.00).

════════════════════════════════════════════
IGNORAR COMPLETAMENTE
════════════════════════════════════════════

Todo lo que está DESPUÉS de la línea TOTAL:
RECIBI/MOS, Efectivo, Tarjeta, Su Vuelto, Suma de sus pagos, Cantidad Unidades, Cajero, Productos N°, IVA Contenido, Transparencia Fiscal, REGISTRO, V: 1., Vend, Rev.

Y el encabezado del ticket (nombre del negocio, CUIT, domicilio, IVA responsable, etc.).

════════════════════════════════════════════
FORMATO DE RESPUESTA
════════════════════════════════════════════

{
  "supermercado": "nombre del negocio",
  "fecha": "dd/MM/yyyy",
  "hora": "HH:mm",
  "total": "14700.00",
  "productos": [
    {
      "nombre": "NOMBRE COMPLETO",
      "descripcion": "",
      "cantidad": 1,
      "precio": 4500.00
    }
  ]
}
            """.trimIndent()

            val requestBody = JSONObject().apply {
                put("model", "meta-llama/llama-4-scout-17b-16e-instruct")
                put("max_tokens", 2048)
                put("temperature", 0.0)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", JSONArray().apply {
                            put(JSONObject().apply {
                                put("type", "text")
                                put("text", prompt)
                            })
                            put(JSONObject().apply {
                                put("type", "image_url")
                                put("image_url", JSONObject().apply {
                                    put("url", "data:image/jpeg;base64,$base64")
                                })
                            })
                        })
                    })
                })
            }.toString()

            val request = Request.Builder()
                .url("https://api.groq.com/openai/v1/chat/completions")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(requestBody.toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()
                ?: return@withContext GroqResult.Error("Respuesta vacía de la API")

            if (!response.isSuccessful) {
                return@withContext GroqResult.Error("Error API ${response.code}: $body")
            }

            val content = JSONObject(body)
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
                .trim()

            val cleanJson = content
                .removePrefix("```json").removePrefix("```")
                .removeSuffix("```").trim()

            val ticket    = parsearTicket(cleanJson)
            val filtrados = filtrarProductosSospechosos(ticket.productos)
            GroqResult.Exito(ticket.copy(productos = filtrados))

        } catch (e: Exception) {
            GroqResult.Error("Error al analizar el ticket: ${e.message}")
        }
    }

    private fun filtrarProductosSospechosos(productos: List<Producto>): List<Producto> {
        val patronLineaCantidad = Regex("""^\d+[,.]?\d*\s*(u\s*)?\s*(\(\d+\)\s*)?x\s*\d""", RegexOption.IGNORE_CASE)

        val palabrasExcluidas = listOf(
            "subtotal", "total", "iva contenido", "iva responsable",
            "efectivo", "tarjeta", "débito", "debito", "crédito", "credito",
            "vuelto", "suma de sus pagos", "recibi", "recibimos",
            "consumidor final", "transparencia fiscal", "regimen de",
            "puntos", "lealtad", "fidelidad",
            "c.u.i.t", "cuit", "ing. brutos", "domicilio",
            "inicio de actividades", "registro:", "cajero:", "vend:",
            "cantidad unidades", "mc debit", "mc credit", "tarjeta de debito"
        )

        return productos.filter { p ->
            val nombre      = p.nombre.trim()
            val nombreLower = nombre.lowercase()
            val esLinea     = patronLineaCantidad.containsMatchIn(nombre)
            val esSosp      = palabrasExcluidas.any { nombreLower.contains(it) }
            val esCeroCorto = p.precio == 0.0 && nombre.length < 4
            !esLinea && !esSosp && !esCeroCorto
        }
    }

    private fun parsearTicket(json: String): TicketAnalizado {
        val obj          = JSONObject(json)
        val productosArr = obj.optJSONArray("productos") ?: JSONArray()

        val productos = (0 until productosArr.length()).mapIndexed { i, _ ->
            val p = productosArr.getJSONObject(i)
            Producto(
                id          = System.currentTimeMillis().toInt() + i,
                codigo      = "",
                nombre      = p.optString("nombre", "Producto ${i + 1}"),
                descripcion = p.optString("descripcion", ""),
                cantidad    = p.optInt("cantidad", 1).coerceAtLeast(1),
                precio      = p.optDouble("precio", 0.0)
            )
        }

        return TicketAnalizado(
            supermercado = obj.optString("supermercado", ""),
            fecha        = obj.optString("fecha", ""),
            hora         = obj.optString("hora", ""),
            total        = obj.optString("total", ""),
            productos    = productos
        )
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val scaled = scaleBitmap(bitmap, maxDimension = 1024)
        val output = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, 85, output)
        return Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)
    }

    private fun scaleBitmap(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val w = bitmap.width; val h = bitmap.height
        if (w <= maxDimension && h <= maxDimension) return bitmap
        val ratio = minOf(maxDimension.toFloat() / w, maxDimension.toFloat() / h)
        return Bitmap.createScaledBitmap(bitmap, (w * ratio).toInt(), (h * ratio).toInt(), true)
    }
}