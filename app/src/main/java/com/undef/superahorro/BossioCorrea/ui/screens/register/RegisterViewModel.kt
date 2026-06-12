package com.undef.superahorro.BossioCorrea.ui.screens.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.BossioCorrea.data.local.SessionManager
import com.undef.superahorro.BossioCorrea.data.repository.AuthRepository
import com.undef.superahorro.BossioCorrea.data.repository.AuthResult
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = AuthRepository(SessionManager(application))

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Success(Unit))
    val uiState = _uiState.asStateFlow()

    fun registrar(
        nombre    : String,
        apellido  : String,
        email     : String,
        password  : String,
        confirmar : String,
        onExito   : () -> Unit
    ) {
        // Validaciones locales antes de ir a la BD
        when {
            nombre.isBlank()      -> { _uiState.value = UiState.Error("Ingresá tu nombre"); return }
            apellido.isBlank()    -> { _uiState.value = UiState.Error("Ingresá tu apellido"); return }
            email.isBlank()       -> { _uiState.value = UiState.Error("Ingresá tu email"); return }
            password.length < 6   -> { _uiState.value = UiState.Error("La contraseña debe tener al menos 6 caracteres"); return }
            password != confirmar -> { _uiState.value = UiState.Error("Las contraseñas no coinciden"); return }
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading

            when (val resultado = repo.registrar(nombre, apellido, email, password)) {
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