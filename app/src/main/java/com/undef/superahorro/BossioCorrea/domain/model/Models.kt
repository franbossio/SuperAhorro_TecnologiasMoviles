package com.undef.superahorro.BossioCorrea.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class Compra(
    val id: String,
    val fecha: LocalDate,
    val hora: LocalTime,
    val supermercado: String,
    val total: Double,
    val productos: List<Producto> = emptyList(),
    val ticketImageUri: String? = null
)

data class Producto(
    val id: String,
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val cantidad: Int,
    val precio: Double
) {
    val subtotal: Double get() = precio * cantidad
}

data class Usuario(
    val nombre: String,
    val apellido: String,
    val email: String
)

// Precio de un producto en una compra puntual, usado para comparar
// precios del mismo producto entre supermercados dentro de un mismo mes.
data class PrecioProducto(
    val nombre: String,
    val supermercado: String,
    val precio: Double,
    val fecha: LocalDate
)