package com.undef.superahorro.BossioCorrea.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Success(Unit))
    val uiState = _uiState.asStateFlow()

    fun login(email: String, password: String, onExito: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            delay(1200) // Simula llamada a API
            if (email.isNotBlank() && password.length >= 4) {
                _uiState.value = UiState.Success(Unit)
                onExito()
            } else {
                _uiState.value = UiState.Error("Email o contraseña inválidos")
            }
        }
    }
}