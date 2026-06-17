package com.undef.superahorro.BossioCorrea.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class CompraConProductos(
    @Embedded val compra: CompraEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "compraId"
    )
    val productos: List<ProductoEntity>
)
