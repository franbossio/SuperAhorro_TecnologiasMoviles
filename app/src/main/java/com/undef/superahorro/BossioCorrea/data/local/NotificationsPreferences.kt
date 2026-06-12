package com.undef.superahorro.BossioCorrea.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensión que crea el DataStore una sola vez en el Context
private val Context.notificationsDataStore: DataStore<Preferences> by preferencesDataStore(name = "notifications_prefs")

/**
 * Gestiona la preferencia de "recibir notificaciones de promociones" usando DataStore.
 * Cuando está deshabilitada, no deben llegar notificaciones de promociones al Home.
 */
class NotificationsPreferences(private val context: Context) {

    companion object {
        private val KEY_HABILITADAS = booleanPreferencesKey("notificaciones_habilitadas")
    }

    /** Flow que emite si las notificaciones de promociones están habilitadas (true por defecto) */
    val habilitadas: Flow<Boolean> = context.notificationsDataStore.data.map { prefs ->
        prefs[KEY_HABILITADAS] ?: true
    }

    /** Guarda la preferencia de notificaciones */
    suspend fun setHabilitadas(habilitadas: Boolean) {
        context.notificationsDataStore.edit { prefs ->
            prefs[KEY_HABILITADAS] = habilitadas
        }
    }
}
