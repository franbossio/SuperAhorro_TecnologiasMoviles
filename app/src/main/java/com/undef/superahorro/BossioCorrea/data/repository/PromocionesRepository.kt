package com.undef.superahorro.BossioCorrea.data.repository

import com.undef.superahorro.BossioCorrea.data.remote.RetrofitClient
import com.undef.superahorro.BossioCorrea.data.remote.VtexProducto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

private data class FuenteVtex(val supermercado: String, val baseUrl: String)

class PromocionesRepository {

    private val fuentes = listOf(
        FuenteVtex("Carrefour",  "https://www.carrefour.com.ar"),
        FuenteVtex("Chango Más", "https://www.masonline.com.ar"),
    )

    suspend fun buscarPromocionesArgentina(): PromocionesResult = withContext(Dispatchers.IO) {
        val promociones = mutableListOf<Promocion>()

        for (fuente in fuentes) {
            val servicio = RetrofitClient.vtex(fuente.baseUrl)
            try {
                // 2 páginas de 50 productos → 100 productos por supermercado
                for (pagina in 0 until 2) {
                    val from = pagina * 50
                    val to   = from + 49
                    val productos = servicio.buscarProductos(from, to)
                    productos.forEach { producto ->
                        parsearPromocion(producto, fuente.supermercado)?.let(promociones::add)
                    }
                }
            } catch (_: Exception) {
                // Si falla un supermercado, continuar con el resto
            }
        }

        if (promociones.isEmpty()) {
            PromocionesResult.Error("No se pudieron obtener promociones en este momento.")
        } else {
            PromocionesResult.Exito(promociones)
        }
    }

    private fun parsearPromocion(producto: VtexProducto, supermercado: String): Promocion? {
        val offer = producto.items
            ?.firstOrNull()
            ?.sellers?.firstOrNull()
            ?.commertialOffer ?: return null

        if (offer.listPrice <= offer.price) return null

        return Promocion(
            producto           = producto.productName,
            precio             = offer.price,
            precioSinDescuento = offer.listPrice,
            moneda             = "ARS",
            supermercado       = supermercado,
            ciudad             = "Córdoba",
            pais               = "Argentina"
        )
    }
}
