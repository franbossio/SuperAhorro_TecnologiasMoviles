package com.undef.superahorro.BossioCorrea.ui.screens.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.BossioCorrea.data.local.AppDatabase
import com.undef.superahorro.BossioCorrea.data.local.SessionManager
import com.undef.superahorro.BossioCorrea.data.repository.AuthRepository
import com.undef.superahorro.BossioCorrea.data.repository.AuthResult
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = AuthRepository(
        db      = AppDatabase.getInstance(application),
        session = SessionManager(application)
    )

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Success(Unit))
    val uiState = _uiState.asStateFlow()

    fun loginConBiometria(onExito: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val resultado = repo.loginConBiometria()) {
                is AuthResult.Exito -> { _uiState.value = UiState.Success(Unit); onExito() }
                is AuthResult.Error -> { _uiState.value = UiState.Error(resultado.mensaje) }
            }
        }
    }

    fun login(email: String, password: String, onExito: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = UiState.Error("Completá todos los campos")
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading

            when (val resultado = repo.login(email, password)) {
                is AuthResult.Exito -> {
                    _uiState.value = UiState.Success(Unit)
                    onExito()
                }
                is AuthResult.Error -> {
                    _uiState.value = UiState.Error(resultado.mensaje)
                }
            }
        }
    }
}