package com.undef.superahorro.BossioCorrea.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tabla de usuarios en la base de datos local.
 * El email es único para evitar registros duplicados.
 */
@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["email"], unique = true)]
)
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val apellido: String,
    val email: String,
    val password: String     // en producción debería hashearse; para el TP es suficiente así
)