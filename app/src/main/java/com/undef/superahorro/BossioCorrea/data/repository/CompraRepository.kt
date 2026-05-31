package com.undef.superahorro.BossioCorrea.data.repository

import com.undef.superahorro.BossioCorrea.data.local.AppDatabase
import com.undef.superahorro.BossioCorrea.data.local.CompraEntity
import com.undef.superahorro.BossioCorrea.data.local.ProductoEntity
import com.undef.superahorro.BossioCorrea.domain.model.Compra
import com.undef.superahorro.BossioCorrea.domain.model.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class CompraRepository(private val db: AppDatabase) {

    private val compraDao   = db.compraDao()
    private val productoDao = db.productoDao()

    // ── Observar compras del usuario ──────────────────────────────────────────
    //
    // mapLatest cancela la transformación anterior cuando llega un nuevo valor.
    // El catch solo captura IOException, no CancellationException, para que
    // la cancelación de corrutinas se propague correctamente.

    fun getComprasFlow(usuarioId: Int): Flow<List<Compra>> =
        compraDao.getComprasDeUsuario(usuarioId).mapLatest { entities ->
            entities.mapNotNull { entity ->
                val productos = productoDao.getProductosDeCompra(entity.id)
                entity.toDomain(productos)
            }
        }

    // ── Guardar compra con productos ──────────────────────────────────────────

    suspend fun guardarCompra(
        usuarioId    : Int,
        fecha        : String,
        hora         : String,
        supermercado : String,
        total        : Double,
        productos    : List<Producto>,
        ticketUri    : String? = null
    ): Int {
        val compraId = compraDao.insertar(
            CompraEntity(
                usuarioId      = usuarioId,
                fecha          = fecha,
                hora           = hora,
                supermercado   = supermercado,
                total          = total,
                ticketImageUri = ticketUri
            )
        ).toInt()

        if (productos.isNotEmpty()) {
            productoDao.insertarTodos(productos.map { it.toEntity(compraId) })
        }
        return compraId
    }

    suspend fun agregarProducto(compraId: Int, producto: Producto) {
        productoDao.insertar(producto.toEntity(compraId))
    }

    suspend fun eliminarCompra(compraId: Int) { compraDao.eliminar(compraId) }

    suspend fun eliminarProducto(productoId: Int) { productoDao.eliminar(productoId) }

    suspend fun getCompraById(compraId: Int): Compra? {
        val entity    = compraDao.getById(compraId) ?: return null
        val productos = productoDao.getProductosDeCompra(compraId)
        return entity.toDomain(productos)
    }
}

// ── Mappers ───────────────────────────────────────────────────────────────────

private val FMT_HORA_LENIENTE = DateTimeFormatter.ofPattern("H:mm")

// Normaliza cualquier variante "yy-M-d" / "yyyy-M-d" → "yyyy-MM-dd" y parsea.
private fun parseFecha(raw: String): LocalDate {
    val partes = raw.trim().split("-")
    if (partes.size == 3) {
        val anio = partes[0].let { if (it.length == 2) "20$it" else it }
        val mes  = partes[1].padStart(2, '0')
        val dia  = partes[2].padStart(2, '0')
        return LocalDate.parse("$anio-$mes-$dia")
    }
    return LocalDate.parse(raw)   // último recurso: dejar que falle con mensaje claro
}

private fun CompraEntity.toDomain(productos: List<ProductoEntity>): Compra =
    Compra(
        id             = id,
        fecha          = parseFecha(fecha),
        hora           = try { LocalTime.parse(hora) }
                         catch (_: Exception) { LocalTime.parse(hora, FMT_HORA_LENIENTE) },
        supermercado   = supermercado,
        total          = total,
        productos      = productos.map { it.toDomain() },
        ticketImageUri = ticketImageUri
    )

private fun ProductoEntity.toDomain(): Producto =
    Producto(
        id          = id,
        codigo      = codigo,
        nombre      = nombre,
        descripcion = descripcion,
        cantidad    = cantidad,
        precio      = precio
    )

private fun Producto.toEntity(compraId: Int): ProductoEntity =
    ProductoEntity(
        compraId    = compraId,
        codigo      = codigo,
        nombre      = nombre,
        descripcion = descripcion,
        cantidad    = cantidad,
        precio      = precio
    )
