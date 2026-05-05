package com.undef.superahorro.BossioCorrea.ui.screens.compras.nueva

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NuevaCompraViewModel : ViewModel() {
    private val _guardado = MutableStateFlow(false)
    val guardado = _guardado.asStateFlow()

    fun guardar(supermercado: String, fecha: String, hora: String, total: String) {
        // En la segunda entrega se persistirá en Room
        _guardado.value = true
    }
}