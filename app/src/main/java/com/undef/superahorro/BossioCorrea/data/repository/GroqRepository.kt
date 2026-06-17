package com.undef.superahorro.BossioCorrea.data.repository

import android.graphics.Bitmap
import android.util.Base64
import com.undef.superahorro.BossioCorrea.BuildConfig
import com.undef.superahorro.BossioCorrea.data.remote.GroqContentPart
import com.undef.superahorro.BossioCorrea.data.remote.GroqImageUrl
import com.undef.superahorro.BossioCorrea.data.remote.GroqMessage
import com.undef.superahorro.BossioCorrea.data.remote.GroqRequest
import com.undef.superahorro.BossioCorrea.data.remote.ResponseFormat
import com.undef.superahorro.BossioCorrea.data.remote.RetrofitClient
import com.undef.superahorro.BossioCorrea.domain.model.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.text.Normalizer

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

data class GrupoProductos(
    val nombre: String,
    val items: List<String>
)

sealed class ChatResult {
    data class Exito(val respuesta: String) : ChatResult()
    data class Error(val mensaje: String)   : ChatResult()
}

class GroqRepository {

    private val apiKey = BuildConfig.GROQ_API_KEY
    private val auth   get() = "Bearer $apiKey"

    suspend fun analizarTicket(bitmap: Bitmap): GroqResult = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext GroqResult.Error("Falta configurar GROQ_API_KEY en local.properties")
        }
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

            val request = GroqRequest(
                model       = "meta-llama/llama-4-scout-17b-16e-instruct",
                maxTokens   = 3000,
                temperature = 0.0,
                messages    = listOf(
                    GroqMessage(
                        role    = "user",
                        content = listOf(
                            GroqContentPart(type = "text", text = prompt),
                            GroqContentPart(
                                type     = "image_url",
                                imageUrl = GroqImageUrl("data:image/jpeg;base64,$base64")
                            )
                        )
                    )
                )
            )

            val respuesta = RetrofitClient.groq.chatCompletions(auth, request)
            val content = respuesta.choices.first().message.content.trim()
            val cleanJson = content
                .removePrefix("```json").removePrefix("```")
                .removeSuffix("```").trim()

            val ticket        = parsearTicket(cleanJson)
            val filtrados     = filtrarProductosSospechosos(ticket.productos)
            val conDescuentos = aplicarDescuentosSueltos(filtrados)
            GroqResult.Exito(ticket.copy(productos = conDescuentos))

        } catch (e: Exception) {
            GroqResult.Error("Error al analizar el ticket: ${e.message}")
        }
    }

    suspend fun agruparProductos(nombres: List<String>): List<GrupoProductos> = withContext(Dispatchers.IO) {
        val distintos = nombres.map { it.trim() }.filter { it.isNotBlank() }.distinctBy { it.lowercase() }
        if (distintos.size < 2) return@withContext distintos.map { GrupoProductos(it, listOf(it)) }

        val gruposBase = distintos
            .groupBy { firmaProducto(it) }
            .values
            .map { items -> GrupoProductos(items.first(), items) }

        if (apiKey.isBlank() || gruposBase.size < 2) return@withContext gruposBase

        try {
            val representantes = gruposBase.map { it.nombre }
            val listado = representantes.mapIndexed { i, n -> "${i + 1}. $n" }.joinToString("\n")

            val prompt = """
Sos un experto en catalogar productos de supermercados argentinos.

TAREA: Te paso una lista de nombres de productos tal como aparecen en tickets de compra reales
(pueden tener mayúsculas/minúsculas distintas, abreviaturas, unidades escritas de formas diferentes,
con o sin tildes, etc.). Agrupá los nombres que correspondan AL MISMO PRODUCTO, aunque estén escritos
de forma distinta.

REGLAS:
- Mismo producto = misma marca, mismo tipo y misma presentación/tamaño (ej: "COCA COLA 1.5L" y
  "Coca-Cola Botella 1,5 Litros" son el mismo producto).
- Productos de distinta marca, sabor, variedad (light, zero, entera, descremada, etc.) o
  tamaño/presentación son productos DIFERENTES y van en grupos separados.
- Si un producto no tiene ningún equivalente en la lista, va solo en su propio grupo.
- Cada nombre de la lista debe aparecer en EXACTAMENTE un grupo, copiado tal cual está en la lista.
- Responder SOLO con JSON válido, sin texto extra ni bloques de código markdown.

LISTA DE PRODUCTOS:
$listado

FORMATO DE RESPUESTA:
{
  "grupos": [
    { "nombre": "nombre genérico y legible del producto", "items": ["nombre tal cual de la lista", "..."] }
  ]
}
            """.trimIndent()

            val request = GroqRequest(
                model          = "llama-3.3-70b-versatile",
                maxTokens      = 2000,
                temperature    = 0.0,
                responseFormat = ResponseFormat("json_object"),
                messages       = listOf(GroqMessage(role = "user", content = prompt))
            )

            val respuesta = RetrofitClient.groq.chatCompletions(auth, request)
            val content = respuesta.choices.first().message.content.trim()
            val cleanJson = content
                .removePrefix("```json").removePrefix("```")
                .removeSuffix("```").trim()

            val gruposIA = parsearGrupos(cleanJson, representantes)

            gruposIA.map { gIA ->
                val repsLower = gIA.items.map { it.lowercase() }.toSet()
                val items = gruposBase.filter { it.nombre.lowercase() in repsLower }.flatMap { it.items }
                GrupoProductos(gIA.nombre, items)
            }

        } catch (e: Exception) {
            gruposBase
        }
    }

    suspend fun consultarHistorial(
        pregunta        : String,
        contextoCompras : String,
        mensajesPrevios : List<Pair<String, String>> = emptyList()
    ): ChatResult = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext ChatResult.Error("Falta configurar GROQ_API_KEY en local.properties")
        }
        try {
            val sistema = """
Sos el asistente de SuperAhorro, una app argentina para registrar compras de supermercado.
Tu única función es responder preguntas del usuario sobre SU historial de compras, que te paso abajo.

REGLAS:
- Respondé en español rioplatense, breve y directo (2 a 5 oraciones, sin markdown).
- Usá solamente los datos del historial. No inventes compras, productos ni precios.
- Los montos son pesos argentinos: formatealos como ${'$'}1.234,56.
- Si la pregunta no se puede responder con el historial (o no tiene compras), decilo amablemente.
- Si te preguntan algo no relacionado con sus compras, recordá que solo respondés sobre el historial.

HISTORIAL DE COMPRAS DEL USUARIO:
$contextoCompras
            """.trimIndent()

            val mensajes = mutableListOf(GroqMessage(role = "system", content = sistema))
            mensajesPrevios.forEach { (rol, contenido) ->
                mensajes += GroqMessage(role = rol, content = contenido)
            }
            mensajes += GroqMessage(role = "user", content = pregunta)

            val request = GroqRequest(
                model       = "meta-llama/llama-4-scout-17b-16e-instruct",
                maxTokens   = 1000,
                temperature = 0.3,
                messages    = mensajes
            )

            val respuesta = RetrofitClient.groq.chatCompletions(auth, request)
            ChatResult.Exito(respuesta.choices.first().message.content.trim())

        } catch (e: Exception) {
            ChatResult.Error("Error al consultar: ${e.message}")
        }
    }

    // ── Lógica de negocio (sin cambios) ──────────────────────────────────────

    private fun firmaProducto(nombre: String): String =
        Normalizer.normalize(nombre.lowercase(), Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
            .replace(Regex("[^a-z0-9]+"), " ")
            .trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .sorted()
            .joinToString(" ")

    private fun parsearGrupos(json: String, originales: List<String>): List<GrupoProductos> {
        val obj       = JSONObject(json)
        val gruposArr = obj.optJSONArray("grupos") ?: JSONArray()

        val grupos = mutableListOf<GrupoProductos>()
        val usados = mutableSetOf<String>()

        for (i in 0 until gruposArr.length()) {
            val g        = gruposArr.optJSONObject(i) ?: continue
            val nombre   = g.optString("nombre").trim()
            val itemsArr = g.optJSONArray("items") ?: JSONArray()

            val items = (0 until itemsArr.length())
                .mapNotNull { itemsArr.optString(it)?.trim() }
                .filter { item -> item.isNotBlank() && originales.any { it.equals(item, ignoreCase = true) } }

            if (items.isEmpty()) continue
            grupos.add(GrupoProductos(nombre.ifBlank { items.first() }, items))
            items.forEach { usados.add(it.lowercase()) }
        }

        originales.forEach { original ->
            if (original.lowercase() !in usados) {
                grupos.add(GrupoProductos(original, listOf(original)))
            }
        }
        return grupos
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
                id          = java.util.UUID.randomUUID().toString(),
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
