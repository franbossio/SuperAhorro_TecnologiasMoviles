package com.undef.superahorro.BossioCorrea.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.BossioCorrea.R
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
                nombre.isBlank()         -> _uiState.value = UiState.Error(R.string.register_email_error)
                email.isBlank()          -> _uiState.value = UiState.Error(R.string.register_email_error)
                password.length < 6      -> _uiState.value = UiState.Error(R.string.register_contraseña_error)
                password != confirmar    -> _uiState.value = UiState.Error(R.string.register_contraseña_diferentes)
                else -> { _uiState.value = UiState.Success(Unit); onExito() }
            }
        }
    }
}