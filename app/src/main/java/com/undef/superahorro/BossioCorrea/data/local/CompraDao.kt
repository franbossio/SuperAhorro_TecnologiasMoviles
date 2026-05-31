package com.undef.superahorro.BossioCorrea.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

// ── DAO Compras ───────────────────────────────────────────────────────────────

@Dao
interface CompraDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(compra: CompraEntity): Long

    @Query("DELETE FROM compras WHERE id = :id")
    suspend fun eliminar(id: Int)

    @Transaction
    @Query("SELECT * FROM compras WHERE usuarioId = :usuarioId ORDER BY fecha DESC, hora DESC")
    fun getComprasDeUsuario(usuarioId: Int): Flow<List<CompraEntity>>

    @Query("SELECT * FROM compras WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): CompraEntity?
}

// ── DAO Productos ─────────────────────────────────────────────────────────────

@Dao
interface ProductoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(producto: ProductoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(productos: List<ProductoEntity>)

    @Query("DELETE FROM productos WHERE id = :id")
    suspend fun eliminar(id: Int)

    @Query("SELECT * FROM productos WHERE compraId = :compraId")
    suspend fun getProductosDeCompra(compraId: Int): List<ProductoEntity>

    @Query("SELECT * FROM productos WHERE compraId = :compraId")
    fun getProductosDeCompraFlow(compraId: Int): Flow<List<ProductoEntity>>
}
