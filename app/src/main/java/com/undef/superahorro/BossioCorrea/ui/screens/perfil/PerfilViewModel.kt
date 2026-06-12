package com.undef.superahorro.BossioCorrea.ui.screens.perfil

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.BossioCorrea.data.local.SessionManager
import com.undef.superahorro.BossioCorrea.data.repository.AuthRepository
import com.undef.superahorro.BossioCorrea.data.repository.CompraRepository
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class PerfilData(
    val nombre            : String,
    val apellido          : String,
    val email             : String,
    val cantidadCompras   : Int    = 0,
    val cantidadProductos : Int    = 0,
    val totalGastado      : Double = 0.0
)

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val session  = SessionManager(application)
    private val authRepo = AuthRepository(session)
    private val repo     = CompraRepository()

    private val _uiState = MutableStateFlow<UiState<PerfilData>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init { observar() }

    private fun observar() = viewModelScope.launch {
        val userId = session.userId.first()
        if (userId == SessionManager.NO_SESSION) {
            _uiState.value = UiState.Error("Sesión expirada")
            return@launch
        }
        val usuario = try { authRepo.getUsuario(userId) } catch (_: Exception) { null }
        if (usuario == null) {
            _uiState.value = UiState.Error("No se pudo cargar el perfil")
            return@launch
        }
        repo.getComprasFlow(userId).collect { compras ->
            // Si el usuario editó el perfil, conservamos lo que muestra la UI
            val actual = (_uiState.value as? UiState.Success)?.data
            _uiState.value = UiState.Success(
                PerfilData(
                    nombre            = actual?.nombre   ?: usuario.nombre,
                    apellido          = actual?.apellido ?: usuario.apellido,
                    email             = actual?.email    ?: usuario.email,
                    cantidadCompras   = compras.size,
                    cantidadProductos = compras.sumOf { it.productos.size },
                    totalGastado      = compras.sumOf { it.total }
                )
            )
        }
    }

    fun guardar(nombre: String, apellido: String, email: String, onDone: () -> Unit) {
        viewModelScope.launch {
            val userId = session.userId.first()
            if (userId != SessionManager.NO_SESSION) {
                try {
                    authRepo.actualizarUsuario(userId, nombre, apellido, email)
                    val current = _uiState.value
                    if (current is UiState.Success) {
                        _uiState.value = UiState.Success(
                            current.data.copy(
                                nombre   = nombre.trim(),
                                apellido = apellido.trim(),
                                email    = email.trim().lowercase()
                            )
                        )
                    }
                } catch (_: Exception) {
                    // si falla la actualización en la nube, la UI queda como estaba
                }
            }
            onDone()
        }
    }
}
