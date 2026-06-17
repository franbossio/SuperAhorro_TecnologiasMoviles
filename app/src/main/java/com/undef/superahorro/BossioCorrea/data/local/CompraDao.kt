package com.undef.superahorro.BossioCorrea.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CompraDao {

    @Transaction
    @Query("SELECT * FROM compras WHERE usuarioId = :usuarioId ORDER BY fecha DESC, hora DESC")
    fun observarCompras(usuarioId: String): Flow<List<CompraConProductos>>

    @Transaction
    @Query("SELECT * FROM compras WHERE id = :compraId")
    suspend fun getCompraConProductos(compraId: String): CompraConProductos?

    @Transaction
    @Query("SELECT * FROM compras WHERE usuarioId = :usuarioId")
    suspend fun getComprasSnapshot(usuarioId: String): List<CompraConProductos>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCompra(compra: CompraEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProductos(productos: List<ProductoEntity>)

    @Query("DELETE FROM compras WHERE id = :compraId")
    suspend fun eliminarCompraPorId(compraId: String)

    @Query("DELETE FROM productos WHERE compraId = :compraId AND id = :productoId")
    suspend fun eliminarProducto(compraId: String, productoId: String)

    @Query("DELETE FROM compras WHERE usuarioId = :usuarioId")
    suspend fun limpiarComprasDeUsuario(usuarioId: String)
}
