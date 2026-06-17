package com.undef.superahorro.BossioCorrea.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "compras")
data class CompraEntity(
    @PrimaryKey val id: String,
    val usuarioId: String,
    val fecha: String,
    val hora: String,
    val supermercado: String,
    val total: Double,
    val ticketImageUri: String?
)
