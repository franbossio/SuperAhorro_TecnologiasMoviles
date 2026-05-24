package com.undef.superahorro.BossioCorrea.data.repository

import com.undef.superahorro.BossioCorrea.data.local.AppDatabase
import com.undef.superahorro.BossioCorrea.data.local.CompraEntity
import com.undef.superahorro.BossioCorrea.data.local.ProductoEntity
import com.undef.superahorro.BossioCorrea.domain.model.Compra
import com.undef.superahorro.BossioCorrea.domain.model.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime

class CompraRepository(private val db: AppDatabase) {

    private val compraDao   = db.compraDao()
    private val productoDao = db.productoDao()

    // ── Observar compras del usuario (Flow — se actualiza automáticamente) ────

    fun getComprasFlow(usuarioId: Int): Flow<List<Compra>> =
        compraDao.getComprasDeUsuario(usuarioId).map { entities ->
            entities.map { entity ->
                val productos = productoDao.getProductosDeCompra(entity.id)
                entity.toDomain(productos)
            }
        }

    // ── Guardar una compra con sus productos ──────────────────────────────────

    suspend fun guardarCompra(
        usuarioId    : Int,
        fecha        : String,
        hora         : String,
        supermercado : String,
        total        : Double,
        productos    : List<Producto>
    ): Int {
        val compraId = compraDao.insertar(
            CompraEntity(
                usuarioId    = usuarioId,
                fecha        = fecha,
                hora         = hora,
                supermercado = supermercado,
                total        = total
            )
        ).toInt()

        if (productos.isNotEmpty()) {
            productoDao.insertarTodos(
                productos.map { it.toEntity(compraId) }
            )
        }
        return compraId
    }

    // ── Agregar un producto a una compra ya existente ─────────────────────────

    suspend fun agregarProducto(compraId: Int, producto: Producto) {
        productoDao.insertar(producto.toEntity(compraId))
    }

    // ── Eliminar compra (los productos se borran en cascada) ──────────────────

    suspend fun eliminarCompra(compraId: Int) {
        compraDao.eliminar(compraId)
    }

    // ── Eliminar un producto puntual ──────────────────────────────────────────

    suspend fun eliminarProducto(productoId: Int) {
        productoDao.eliminar(productoId)
    }

    // ── Obtener una compra puntual con sus productos ──────────────────────────

    suspend fun getCompraById(compraId: Int): Compra? {
        val entity    = compraDao.getById(compraId) ?: return null
        val productos = productoDao.getProductosDeCompra(compraId)
        return entity.toDomain(productos)
    }
}

// ── Mappers ───────────────────────────────────────────────────────────────────

private fun CompraEntity.toDomain(productos: List<ProductoEntity>): Compra =
    Compra(
        id           = id,
        fecha        = LocalDate.parse(fecha),
        hora         = LocalTime.parse(hora),
        supermercado = supermercado,
        total        = total,
        productos    = productos.map { it.toDomain() },
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