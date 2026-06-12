package com.undef.superahorro.BossioCorrea.ui.theme

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val android.content.Context.themeDataStore: DataStore<Preferences>
        by preferencesDataStore(name = "theme_prefs")

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = application.themeDataStore

    private val KEY_IS_DARK    = booleanPreferencesKey("is_dark")
    private val KEY_USE_SYSTEM = booleanPreferencesKey("use_system")

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode = _isDarkMode.asStateFlow()

    private val _useSystemTheme = MutableStateFlow(true)
    val useSystemTheme = _useSystemTheme.asStateFlow()

    init {
        viewModelScope.launch {
            val prefs = dataStore.data.first()
            _useSystemTheme.value = prefs[KEY_USE_SYSTEM] ?: true
            _isDarkMode.value     = prefs[KEY_IS_DARK]    ?: false
        }
    }

    fun setDarkMode(enabled: Boolean) {
        _useSystemTheme.value = false
        _isDarkMode.value     = enabled
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[KEY_USE_SYSTEM] = false
                prefs[KEY_IS_DARK]    = enabled
            }
        }
    }

    fun setSystemTheme() {
        _useSystemTheme.value = true
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[KEY_USE_SYSTEM] = true
            }
        }
    }
}
