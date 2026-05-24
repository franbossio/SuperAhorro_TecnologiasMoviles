package com.undef.superahorro.BossioCorrea.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UsuarioDao {

    /**
     * Inserta un nuevo usuario. Si el email ya existe lanza excepción
     * (gracias al índice unique en UsuarioEntity) que capturamos en el repo.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(usuario: UsuarioEntity): Long

    /**
     * Busca un usuario por email y contraseña. Devuelve null si no coincide.
     * Se usa para el login.
     */
    @Query("SELECT * FROM usuarios WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): UsuarioEntity?

    /**
     * Comprueba si un email ya está registrado.
     */
    @Query("SELECT COUNT(*) FROM usuarios WHERE email = :email")
    suspend fun existeEmail(email: String): Int

    /**
     * Devuelve el usuario por ID (para cargar el perfil).
     */
    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): UsuarioEntity?
}