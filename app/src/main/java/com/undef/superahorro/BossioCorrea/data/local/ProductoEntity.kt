package com.undef.superahorro.BossioCorrea.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "productos",
    foreignKeys = [ForeignKey(
        entity = CompraEntity::class,
        parentColumns = ["id"],
        childColumns = ["compraId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("compraId")]
)
data class ProductoEntity(
    @PrimaryKey val id: String,
    val compraId: String,
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val cantidad: Int,
    val precio: Double
)
