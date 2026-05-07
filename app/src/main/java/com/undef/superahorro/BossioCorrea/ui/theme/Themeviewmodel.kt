package com.undef.superahorro.BossioCorrea.ui.theme

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel de ciclo de vida de la aplicación que persiste el modo oscuro
 * mientras la app esté en memoria. En la Segunda Entrega se puede reemplazar
 * el respaldo en memoria por DataStore para que el valor sobreviva reinicios.
 */
class ThemeViewModel : ViewModel() {

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode = _isDarkMode.asStateFlow()

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }
}