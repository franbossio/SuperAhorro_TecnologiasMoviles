package com.undef.superahorro.BossioCorrea.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensión que crea el DataStore una sola vez en el Context
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

/**
 * Gestiona la sesión del usuario logueado usando DataStore.
 * Guarda el UID de Firebase y el email del usuario para que al
 * reiniciar la app no haya que loguearse de nuevo.
 */
class SessionManager(private val context: Context) {

    companion object {
        private val KEY_USER_ID    = stringPreferencesKey("user_uid")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        private val KEY_USER_NAME  = stringPreferencesKey("user_name")

        // Valor centinela: no hay sesión activa
        const val NO_SESSION = ""
    }

    /** Flow que emite el UID del usuario logueado ("" si no hay sesión) */
    val userId: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_ID] ?: NO_SESSION
    }

    /** Flow que emite el email del usuario logueado */
    val userEmail: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_EMAIL] ?: ""
    }

    /** Flow que emite el nombre del usuario logueado */
    val userName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_NAME] ?: ""
    }

    /** Guarda la sesión al loguearse */
    suspend fun guardarSesion(userId: String, email: String, nombre: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_ID]    = userId
            prefs[KEY_USER_EMAIL] = email
            prefs[KEY_USER_NAME]  = nombre
        }
    }

    /** Borra la sesión al cerrar sesión */
    suspend fun cerrarSesion() {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_ID]    = NO_SESSION
            prefs[KEY_USER_EMAIL] = ""
            prefs[KEY_USER_NAME]  = ""
        }
    }
}
