package com.undef.superahorro.BossioCorrea.data.mock

import com.undef.superahorro.BossioCorrea.domain.model.Compra
import com.undef.superahorro.BossioCorrea.domain.model.Producto
import com.undef.superahorro.BossioCorrea.domain.model.Usuario
import java.time.LocalDate
import java.time.LocalTime

val usuarioMock = Usuario(
    nombre   = "Juan",
    apellido = "García",
    email    = "juan.garcia@gmail.com"
)

val supermercadosMock = listOf(
    "Carrefour", "Día", "Jumbo", "Coto", "Walmart",
    "Vea", "La Anónima", "Disco", "Changomas", "Metro"
)

val comprasMock = listOf(
    Compra(
        id             = 1,
        fecha          = LocalDate.of(2026, 4, 28),
        hora           = LocalTime.of(10, 30),
        supermercado   = "Carrefour",
        total          = 18540.50,
        productos = listOf(
            Producto(1, "7790895001234", "Leche La Serenísima",  "Entera 1L",  2, 1350.00),
            Producto(2, "7798082390012", "Pan Lactal Bimbo",     "500g",       1, 1890.00),
            Producto(3, "7790070044010", "Arroz Gallo",          "1 kg",       3, 1200.00),
            Producto(4, "7790040001234", "Galletitas Oreo",      "138g",       2,  980.00),
            Producto(5, "7790895005678", "Yogur Danone",         "Frutilla x4",1, 2300.00),
        )
    ),
    Compra(
        id             = 2,
        fecha          = LocalDate.of(2026, 4, 20),
        hora           = LocalTime.of(15, 0),
        supermercado   = "Día",
        total          = 9320.00,
        productos = listOf(
            Producto(6,  "7790040001234", "Yerba Taragüi",   "500g",  2, 2100.00),
            Producto(7,  "7790040001235", "Fideos Marolio",  "500g",  4,  780.00),
        )
    ),
    Compra(
        id             = 3,
        fecha          = LocalDate.of(2026, 3, 15),
        hora           = LocalTime.of(11, 45),
        supermercado   = "Jumbo",
        total          = 32800.00,
        productos = listOf(
            Producto(8,  "7798082390100", "Aceite Natura",   "1.5L",  2, 4200.00),
            Producto(9,  "7790895001300", "Azúcar Ledesma",  "1 kg",  2, 1100.00),
            Producto(10, "7790040001300", "Café Nescafé",    "170g",  1, 3500.00),
        )
    ),
    Compra(
        id             = 4,
        fecha          = LocalDate.of(2026, 2, 10),
        hora           = LocalTime.of(9, 15),
        supermercado   = "Coto",
        total          = 12500.00,
        productos = listOf(
            Producto(11, "7790895002000", "Skip Polvo",       "3 kg",  1, 6800.00),
            Producto(12, "7790040002000", "Papel Elite",      "x4",    2, 2850.00),
        )
    ),
)