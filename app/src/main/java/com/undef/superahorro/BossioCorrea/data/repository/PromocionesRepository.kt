package com.undef.superahorro.BossioCorrea.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
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

/**
 * Consulta promociones (precios con descuento) reportadas en supermercados de
 * Argentina a través de la API pública de Open Prices (Open Food Facts).
 * Documentación: https://prices.openfoodfacts.org/api/docs
 *
 * La API no permite filtrar precios por país en una sola llamada, así que la
 * consulta se hace en dos pasos: primero se buscan las sucursales de
 * supermercados de Argentina y luego se piden los precios con descuento
 * reportados en esas sucursales (siempre en pesos argentinos).
 */
class PromocionesRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun buscarPromocionesArgentina(): PromocionesResult = withContext(Dispatchers.IO) {
        try {
            val sucursales = obtenerSucursalesArgentinas()
            if (sucursales.isEmpty()) return@withContext PromocionesResult.Exito(emptyList())

            val url = "https://prices.openfoodfacts.org/api/v1/prices".toHttpUrl().newBuilder()
                .addQueryParameter("location_id__in", sucursales.joinToString(","))
                .addQueryParameter("price_is_discounted", "true")
                .addQueryParameter("order_by", "-created")
                .addQueryParameter("size", "30")
                .build()

            // Filtramos por ARS: la base es global y a veces alguna sucursal
            // mal etiquetada como supermercado reporta precios en otra moneda.
            val promociones = obtenerItems(url).map(::parsearPromocion).filter { it.moneda == "ARS" }
            PromocionesResult.Exito(promociones)
        } catch (e: Exception) {
            PromocionesResult.Error("Error al consultar promociones: ${e.message}")
        }
    }

    private fun obtenerSucursalesArgentinas(): List<Int> {
        val url = "https://prices.openfoodfacts.org/api/v1/locations".toHttpUrl().newBuilder()
            .addQueryParameter("osm_address_country__like", "Argentina")
            .addQueryParameter("osm_tag_value", "supermarket")
            .addQueryParameter("size", "100")
            .build()

        return obtenerItems(url).map { it.getInt("id") }
    }

    private fun obtenerItems(url: HttpUrl): List<JSONObject> {
        val request  = Request.Builder().url(url).get().build()
        val response = client.newCall(request).execute()
        val body     = response.body?.string()
        if (!response.isSuccessful || body == null) return emptyList()

        val items = JSONObject(body).optJSONArray("items") ?: return emptyList()
        return (0 until items.length()).map { items.getJSONObject(it) }
    }

    private fun parsearPromocion(item: JSONObject): Promocion {
        val location       = item.optJSONObject("location")
        val product        = item.optJSONObject("product")
        val precioOriginal = item.optDouble("price_without_discount")

        return Promocion(
            producto           = textoOrNull(item, "product_name")
                                    ?: product?.let { textoOrNull(it, "product_name") }
                                    ?: "Producto sin nombre",
            precio             = item.optDouble("price", 0.0),
            precioSinDescuento = precioOriginal.takeUnless { it.isNaN() },
            moneda             = item.optString("currency"),
            supermercado       = location?.let { textoOrNull(it, "osm_name") } ?: "Supermercado",
            ciudad             = location?.let { textoOrNull(it, "osm_address_city") },
            pais               = location?.let { textoOrNull(it, "osm_address_country") }
        )
    }

    /** org.json representa los valores `null` del JSON como la cadena literal "null". */
    private fun textoOrNull(json: JSONObject, key: String): String? =
        json.optString(key).takeIf { it.isNotBlank() && it != "null" }
}
