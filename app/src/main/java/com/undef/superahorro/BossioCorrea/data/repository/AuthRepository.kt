package com.undef.superahorro.BossioCorrea.data.repository

import com.undef.superahorro.BossioCorrea.data.local.AppDatabase
import com.undef.superahorro.BossioCorrea.data.local.SessionManager
import com.undef.superahorro.BossioCorrea.data.local.UsuarioEntity

/**
 * Resultado sellado para las operaciones de autenticación.
 * Lo usamos en vez de UiState porque estas operaciones no tienen Loading;
 * el Loading lo maneja el ViewModel antes de llamar al repo.
 */
sealed class AuthResult {
    data class Exito(val usuario: UsuarioEntity) : AuthResult()
    data class Error(val mensaje: String) : AuthResult()
}

class AuthRepository(
    private val db      : AppDatabase,
    private val session : SessionManager
) {
    private val dao get() = db.usuarioDao()

    /**
     * Registra un nuevo usuario.
     * Verifica que el email no esté en uso antes de insertar.
     */
    suspend fun registrar(
        nombre   : String,
        apellido : String,
        email    : String,
        password : String
    ): AuthResult {
        if (dao.existeEmail(email) > 0) {
            return AuthResult.Error("El email ya está registrado")
        }
        return try {
            val id = dao.insertar(
                UsuarioEntity(
                    nombre   = nombre.trim(),
                    apellido = apellido.trim(),
                    email    = email.trim().lowercase(),
                    password = password
                )
            )
            val usuario = dao.getById(id.toInt())!!
            session.guardarSesion(usuario.id, usuario.email, usuario.nombre)
            AuthResult.Exito(usuario)
        } catch (e: Exception) {
            AuthResult.Error("Error al registrar: ${e.message}")
        }
    }

    /**
     * Valida credenciales contra la base de datos.
     * Si coinciden, guarda la sesión en DataStore.
     */
    suspend fun login(email: String, password: String): AuthResult {
        val usuario = dao.login(email.trim().lowercase(), password)
        return if (usuario != null) {
            session.guardarSesion(usuario.id, usuario.email, usuario.nombre)
            AuthResult.Exito(usuario)
        } else {
            AuthResult.Error("Email o contraseña incorrectos")
        }
    }

    /** Cierra la sesión borrando los datos del DataStore */
    suspend fun cerrarSesion() = session.cerrarSesion()
}