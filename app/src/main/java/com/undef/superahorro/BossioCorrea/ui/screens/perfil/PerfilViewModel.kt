package com.undef.superahorro.BossioCorrea.ui.screens.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PerfilData(val nombre: String, val apellido: String, val email: String)

class PerfilViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<PerfilData>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            delay(400)
            _uiState.value = UiState.Success(
                PerfilData("Juan", "Pérez", "juan.perez@email.com")
            )
        }
    }

    fun guardar(nombre: String, apellido: String, email: String, onDone: () -> Unit) {
        viewModelScope.launch {
            delay(500)
            _uiState.value = UiState.Success(PerfilData(nombre, apellido, email))
            onDone()
        }
    }
}
