package com.undef.superahorro.BossioCorrea.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class Promocion(
    val producto           : String,
    val precio             : Double,
    val precioSinDescuento : Double?,
    val moneda             : String,
    val supermercado       : String,
    val ciudad             : String?,
    val pais               : String?
)

sealed class PromocionesResult {
    data class Exito(val promociones: List<Promocion>) : PromocionesResult()
    data class Error(val mensaje: String)              : PromocionesResult()
}

/** Catálogo público (VTEX) de un supermercado argentino. */
private data class FuenteVtex(val supermercado: String, val baseUrl: String)

/**
 * Consulta los catálogos públicos (VTEX) de varios supermercados argentinos y
 * arma la lista de promociones a partir de los productos cuyo precio actual
 * ("Price") es menor al precio de lista ("ListPrice"), es decir, descuentos
 * vigentes hoy. Son los catálogos online de cada cadena, válidos también en
 * sus sucursales (incluidas las de Córdoba).
 */
class PromocionesRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val fuentes = listOf(
        FuenteVtex("Carrefour", "https://www.carrefour.com.ar"),
        FuenteVtex("Día",       "https://diaonline.supermercadosdia.com.ar"),
    )

    suspend fun buscarPromocionesArgentina(): PromocionesResult = withContext(Dispatchers.IO) {
        val promociones = mutableListOf<Promocion>()

        for (fuente in fuentes) {
            try {
                // 2 páginas de 50 productos (máximo permitido por request) → 100 productos por supermercado
                for (pagina in 0 until 2) {
                    val from = pagina * 50
                    val to   = from + 49
                    val url = "${fuente.baseUrl}/api/catalog_system/pub/products/search".toHttpUrl().newBuilder()
                        .addQueryParameter("_from", from.toString())
                        .addQueryParameter("_to", to.toString())
                        .build()
                    obtenerProductos(url).forEach { producto ->
                        parsearPromocion(producto, fuente.supermercado)?.let(promociones::add)
                    }
                }
            } catch (e: Exception) {
                // Si falla un supermercado en particular, se continúa con el resto.
            }
        }

        if (promociones.isEmpty()) {
            PromocionesResult.Error("No se pudieron obtener promociones en este momento.")
        } else {
            PromocionesResult.Exito(promociones)
        }
    }

    private fun obtenerProductos(url: HttpUrl): List<JSONObject> {
        val request = Request.Builder()
            .url(url)
            .header("Accept", "application/json")
            .header("User-Agent", "Mozilla/5.0 (Android) SuperAhorroApp")
            .get()
            .build()

        val response = client.newCall(request).execute()
        val body      = response.body?.string()
        if (!response.isSuccessful || body == null) return emptyList()

        val items = JSONArray(body)
        return (0 until items.length()).map { items.getJSONObject(it) }
    }

    /** Devuelve null si el producto no tiene un descuento real (ListPrice > Price). */
    private fun parsearPromocion(producto: JSONObject, supermercado: String): Promocion? {
        val item   = producto.optJSONArray("items")?.optJSONObject(0) ?: return null
        val seller = item.optJSONArray("sellers")?.optJSONObject(0) ?: return null
        val offer  = seller.optJSONObject("commertialOffer") ?: return null

        val precio        = offer.optDouble("Price")
        val precioDeLista = offer.optDouble("ListPrice")
        if (precio.isNaN() || precioDeLista.isNaN() || precioDeLista <= precio) return null

        return Promocion(
            producto           = producto.optString("productName"),
            precio             = precio,
            precioSinDescuento = precioDeLista,
            moneda             = "ARS",
            supermercado       = supermercado,
            ciudad             = "Córdoba",
            pais               = "Argentina"
        )
    }
}
