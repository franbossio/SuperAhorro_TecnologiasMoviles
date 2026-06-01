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
TAREA: Extraer todos los productos del ticket. Responder SOLO con JSON válido. Sin texto extra. Sin bloques de código markdown.

════════════════════════════════════════
FORMATOS DE TICKET ARGENTINO
════════════════════════════════════════

Existen distintos formatos, identificar cuál corresponde al ticket:

FORMATO A (almacenes, kioscos):
  1,0000 (00) x 4500,0000      ← línea de cantidad/precio unitario → IGNORAR COMPLETAMENTE
  COCA COLA RETORNABLE 4500,00 ← producto real (nombre + precio total)

FORMATO B (Carrefour, Jumbo, Coto, Disco):
  COCA COLA 1.5L               ← nombre del producto
  1        $4.500,00  $4.500,00 ← cant, precio unit, precio total → tomar el último número

FORMATO C (línea única):
  1 COCA COLA 1.5L   4500,00   ← cantidad + nombre + precio total en una sola línea

REGLA GENERAL: El precio del producto es siempre el ÚLTIMO número de la línea del producto.

════════════════════════════════════════
ZONA DE PRODUCTOS
════════════════════════════════════════

INICIO: Después del encabezado (nombre del negocio, CUIT, dirección, fecha/hora).
FIN: La primera línea que diga TOTAL, IMPORTE TOTAL, SUBTOTAL, SUMA TOTAL o similar.

NUNCA incluir líneas después del TOTAL. Ese número grande junto a TOTAL es el total del ticket, NO es un producto.

════════════════════════════════════════
DESCUENTOS — MUY IMPORTANTE
════════════════════════════════════════

Los descuentos aparecen DESPUÉS del producto al que aplican, generalmente con valor negativo.
Ejemplos de cómo aparecen en el ticket:
  "Descuento JAMON COCIDO       -318,65"
  "Dto. 2da unidad              -500,00"
  "DESC. JUBILADOS              -200,00"
  "Bonificacion 10%             -150,00"
  "AHORRO TARJETA               -300,00"
  "Promo 2x1                    -800,00"

QUÉ HACER con cada descuento:
  1. Identificar el producto anterior al que aplica ese descuento
  2. precio final del producto = precio original - monto del descuento
  3. En el campo "descripcion" escribir exactamente: "Descuento aplicado: ${'$'}MONTO"
  4. Si hay varios descuentos para el mismo producto, aplicar todos y listar cada uno en descripcion
  5. NO crear una entrada separada para el descuento en la lista de productos

EJEMPLO CORRECTO:
  Ticket muestra:
    JAMON COCIDO FRIG    2000,00
    Desc. JAMON          -318,65

  JSON correcto:
    { "nombre": "JAMON COCIDO FRIG", "descripcion": "Descuento aplicado: $318.65", "cantidad": 1, "precio": 1681.35 }

════════════════════════════════════════
IGNORAR COMPLETAMENTE
════════════════════════════════════════

- Todo lo después del TOTAL: Efectivo, Tarjeta, Vuelto, Suma de pagos, Cajero, IVA, CUIT, Cod. Barra
- Encabezado: nombre del negocio, dirección, teléfono, CUIT, Responsable Inscripto
- Líneas de cantidad pura: "1,0000 (00) x 4500,0000"
- Leyendas legales: Transparencia Fiscal, Régimen de, Reg. N°, Vend, Rev

════════════════════════════════════════
FORMATO DE RESPUESTA — SOLO ESTE JSON
════════════════════════════════════════

{
  "supermercado": "nombre del negocio",
  "fecha": "dd/MM/yyyy",
  "hora": "HH:mm",
  "total": "4500.00",
  "productos": [
    {
      "nombre": "NOMBRE COMPLETO DEL PRODUCTO TAL COMO APARECE EN EL TICKET",
      "descripcion": "Descuento aplicado: $318.65",
      "cantidad": 1,
      "precio": 1681.35
    }
  ]
}

REGLAS DEL JSON:
- Usar punto decimal (1500.00), no coma
- precio = precio FINAL ya con el descuento descontado
- Si no hay descuento: descripcion: ""
- cantidad: número entero mayor o igual a 1
- No inventar ni modificar nombres de productos, copiar exactamente lo que dice el ticket
            """.trimIndent()

            val requestBody = JSONObject().apply {
                put("model", "meta-llama/llama-4-scout-17b-16e-instruct")
                put("max_tokens", 3000)
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
            val conDescuentos = aplicarDescuentosSueltos(filtrados)
            GroqResult.Exito(ticket.copy(productos = conDescuentos))

        } catch (e: Exception) {
            GroqResult.Error("Error al analizar el ticket: ${e.message}")
        }
    }

    private fun aplicarDescuentosSueltos(productos: List<Producto>): List<Producto> {
        val patronDescuento = Regex(
            """^(desc|dto\.?|descuento|bonif|ahorr|promo|rebaj|oferta)""",
            RegexOption.IGNORE_CASE
        )
        val resultado = mutableListOf<Producto>()

        for (producto in productos) {
            val esDescuento = patronDescuento.containsMatchIn(producto.nombre.trim()) || producto.precio < 0
            if (esDescuento && resultado.isNotEmpty()) {
                val monto = Math.abs(producto.precio)
                if (monto > 0) {
                    val ultimo = resultado.last()
                    val precioNuevo = (ultimo.precio - monto).coerceAtLeast(0.0)
                    val notaDescuento = "Descuento aplicado: $${"%.2f".format(monto)}"
                    val descripcionNueva = listOf(ultimo.descripcion, notaDescuento)
                        .filter { it.isNotBlank() }.joinToString(". ")
                    resultado[resultado.size - 1] = ultimo.copy(precio = precioNuevo, descripcion = descripcionNueva)
                }
            } else if (!esDescuento) {
                resultado.add(producto)
            }
        }
        return resultado
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
        val scaled = scaleBitmap(bitmap, maxDimension = 1920)
        val output = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, 90, output)
        return Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)
    }

    private fun scaleBitmap(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val w = bitmap.width; val h = bitmap.height
        if (w <= maxDimension && h <= maxDimension) return bitmap
        val ratio = minOf(maxDimension.toFloat() / w, maxDimension.toFloat() / h)
        return Bitmap.createScaledBitmap(bitmap, (w * ratio).toInt(), (h * ratio).toInt(), true)
    }
}