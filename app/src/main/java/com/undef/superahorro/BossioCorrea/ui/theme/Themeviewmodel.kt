package com.undef.superahorro.BossioCorrea.ui.theme

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeViewModel : ViewModel() {

    // El modo oscuro elegido manualmente (solo importa si useSystemTheme = false)
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode = _isDarkMode.asStateFlow()

    // Cuando es true, el MainActivity usa isSystemInDarkTheme() del dispositivo
    private val _useSystemTheme = MutableStateFlow(true)  // por defecto respeta el sistema
    val useSystemTheme = _useSystemTheme.asStateFlow()

    /** Llamado desde SettingsScreen cuando el usuario elige Claro u Oscuro manualmente */
    fun setDarkMode(enabled: Boolean) {
        _useSystemTheme.value = false
        _isDarkMode.value     = enabled
    }

    /** Llamado desde SettingsScreen cuando el usuario elige "Sistema" */
    fun setSystemTheme() {
        _useSystemTheme.value = true
    }
}