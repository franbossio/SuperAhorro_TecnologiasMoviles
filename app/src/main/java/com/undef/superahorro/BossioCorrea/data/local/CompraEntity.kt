package com.undef.superahorro.BossioCorrea.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ── Tabla compras ─────────────────────────────────────────────────────────────

@Entity(
    tableName = "compras",
    foreignKeys = [ForeignKey(
        entity        = UsuarioEntity::class,
        parentColumns = ["id"],
        childColumns  = ["usuarioId"],
        onDelete      = ForeignKey.CASCADE
    )],
    indices = [Index("usuarioId")]
)
data class CompraEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId      : Int,
    val fecha          : String,   // guardamos como "yyyy-MM-dd"
    val hora           : String,   // guardamos como "HH:mm"
    val supermercado   : String,
    val total          : Double,
    val ticketImageUri : String? = null
)

// ── Tabla productos ───────────────────────────────────────────────────────────

@Entity(
    tableName = "productos",
    foreignKeys = [ForeignKey(
        entity        = CompraEntity::class,
        parentColumns = ["id"],
        childColumns  = ["compraId"],
        onDelete      = ForeignKey.CASCADE   // al borrar una compra, se borran sus productos
    )],
    indices = [Index("compraId")]
)
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val compraId    : Int,
    val codigo      : String,
    val nombre      : String,
    val descripcion : String,
    val cantidad    : Int,
    val precio      : Double
)