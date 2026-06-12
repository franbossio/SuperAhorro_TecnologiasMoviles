package com.undef.superahorro.BossioCorrea.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.undef.superahorro.BossioCorrea.data.local.SessionManager
import com.undef.superahorro.BossioCorrea.domain.model.Usuario
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

/**
 * Resultado sellado para las operaciones de autenticación.
 * Lo usamos en vez de UiState porque estas operaciones no tienen Loading;
 * el Loading lo maneja el ViewModel antes de llamar al repo.
 */
sealed class AuthResult {
    data class Exito(val usuario: Usuario) : AuthResult()
    data class Error(val mensaje: String) : AuthResult()
}

/**
 * Autenticación contra Firebase Auth (email/contraseña) y perfil del
 * usuario en Firestore (colección "usuarios", un documento por UID).
 * La contraseña nunca se guarda en la base: la maneja Firebase.
 */
class AuthRepository(private val session: SessionManager) {

    private val auth     = FirebaseAuth.getInstance()
    private val usuarios = FirebaseFirestore.getInstance().collection("usuarios")

    /**
     * Registra un nuevo usuario en Firebase Auth y crea su perfil en Firestore.
     */
    suspend fun registrar(
        nombre   : String,
        apellido : String,
        email    : String,
        password : String
    ): AuthResult {
        val emailNorm = email.trim().lowercase()
        return try {
            val resultado = auth.createUserWithEmailAndPassword(emailNorm, password).await()
            val uid = resultado.user?.uid
                ?: return AuthResult.Error("Error al registrar: no se obtuvo el usuario")

            val usuario = Usuario(nombre.trim(), apellido.trim(), emailNorm)
            usuarios.document(uid).set(
                mapOf(
                    "nombre"   to usuario.nombre,
                    "apellido" to usuario.apellido,
                    "email"    to usuario.email
                )
            ).await()

            session.guardarSesion(uid, usuario.email, usuario.nombre)
            AuthResult.Exito(usuario)
        } catch (e: FirebaseAuthUserCollisionException) {
            AuthResult.Error("El email ya está registrado")
        } catch (e: FirebaseAuthWeakPasswordException) {
            AuthResult.Error("La contraseña debe tener al menos 6 caracteres")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            AuthResult.Error("El email no es válido")
        } catch (e: Exception) {
            AuthResult.Error("Error al registrar: ${e.message}")
        }
    }

    /**
     * Valida credenciales contra Firebase Auth.
     * Si coinciden, recupera el perfil de Firestore y guarda la sesión.
     */
    suspend fun login(email: String, password: String): AuthResult {
        val emailNorm = email.trim().lowercase()
        return try {
            val resultado = auth.signInWithEmailAndPassword(emailNorm, password).await()
            val uid = resultado.user?.uid
                ?: return AuthResult.Error("Email o contraseña incorrectos")

            val usuario = getUsuario(uid) ?: Usuario("", "", emailNorm)
            session.guardarSesion(uid, usuario.email, usuario.nombre)
            AuthResult.Exito(usuario)
        } catch (e: FirebaseAuthInvalidUserException) {
            AuthResult.Error("Email o contraseña incorrectos")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            AuthResult.Error("Email o contraseña incorrectos")
        } catch (e: Exception) {
            AuthResult.Error("No se pudo iniciar sesión: ${e.message}")
        }
    }

    /**
     * Login por biometría: la huella/cara ya fue validada por el sistema.
     * Firebase mantiene la sesión iniciada en el dispositivo, así que solo
     * verificamos que siga activa y que el perfil exista en la nube.
     */
    suspend fun loginConBiometria(): AuthResult {
        val uid = session.userId.first()
        if (uid == SessionManager.NO_SESSION || auth.currentUser == null) {
            return AuthResult.Error("Iniciá sesión con email y contraseña primero")
        }
        return try {
            val usuario = getUsuario(uid)
                ?: return AuthResult.Error("No se encontró la cuenta. Iniciá sesión manualmente")
            session.guardarSesion(uid, usuario.email, usuario.nombre)
            AuthResult.Exito(usuario)
        } catch (e: Exception) {
            AuthResult.Error("No se pudo iniciar sesión: ${e.message}")
        }
    }

    /** Envía un email real con el link para restablecer la contraseña. */
    suspend fun recuperarPassword(email: String): AuthResult {
        return try {
            auth.sendPasswordResetEmail(email.trim().lowercase()).await()
            AuthResult.Exito(Usuario("", "", email.trim().lowercase()))
        } catch (e: FirebaseAuthInvalidUserException) {
            AuthResult.Error("No existe una cuenta con ese email")
        } catch (e: Exception) {
            AuthResult.Error("No se pudo enviar el email: ${e.message}")
        }
    }

    /** Lee el perfil del usuario desde Firestore (null si no existe). */
    suspend fun getUsuario(uid: String): Usuario? {
        val doc = usuarios.document(uid).get().await()
        if (!doc.exists()) return null
        return Usuario(
            nombre   = doc.getString("nombre")   ?: "",
            apellido = doc.getString("apellido") ?: "",
            email    = doc.getString("email")    ?: ""
        )
    }

    /**
     * Actualiza el perfil en Firestore. El email de login en Firebase Auth
     * no cambia (requeriría reautenticación); solo se actualiza el perfil.
     */
    suspend fun actualizarUsuario(uid: String, nombre: String, apellido: String, email: String) {
        usuarios.document(uid).update(
            mapOf(
                "nombre"   to nombre.trim(),
                "apellido" to apellido.trim(),
                "email"    to email.trim().lowercase()
            )
        ).await()
        session.guardarSesion(uid, email.trim().lowercase(), nombre.trim())
    }

    /** Cierra la sesión en Firebase y borra los datos del DataStore */
    suspend fun cerrarSesion() {
        auth.signOut()
        session.cerrarSesion()
    }
}
