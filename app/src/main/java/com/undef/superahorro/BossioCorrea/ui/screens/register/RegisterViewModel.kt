package com.undef.superahorro.BossioCorrea.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Success(Unit))
    val uiState = _uiState.asStateFlow()

    fun registrar(nombre: String, email: String, password: String, confirmar: String, onExito: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            delay(1000)
            when {
                nombre.isBlank()         -> _uiState.value = UiState.Error("Ingresá tu nombre")
                email.isBlank()          -> _uiState.value = UiState.Error("Ingresá tu email")
                password.length < 6      -> _uiState.value = UiState.Error("La contraseña debe tener al menos 6 caracteres")
                password != confirmar    -> _uiState.value = UiState.Error("Las contraseñas no coinciden")
                else -> { _uiState.value = UiState.Success(Unit); onExito() }
            }
        }
    }
}