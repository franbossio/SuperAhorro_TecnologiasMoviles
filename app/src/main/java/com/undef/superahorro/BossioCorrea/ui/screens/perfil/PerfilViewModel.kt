package com.undef.superahorro.BossioCorrea.ui.screens.perfil

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.BossioCorrea.data.local.AppDatabase
import com.undef.superahorro.BossioCorrea.data.local.SessionManager
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

    private val db      = AppDatabase.getInstance(application)
    private val session = SessionManager(application)
    private val repo    = CompraRepository(db)

    private val _uiState = MutableStateFlow<UiState<PerfilData>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init { observar() }

    private fun observar() = viewModelScope.launch {
        val userId = session.userId.first()
        if (userId == SessionManager.NO_SESSION) {
            _uiState.value = UiState.Error("Sesión expirada")
            return@launch
        }
        repo.getComprasFlow(userId).collect { compras ->
            val usuario = db.usuarioDao().getById(userId) ?: return@collect
            _uiState.value = UiState.Success(
                PerfilData(
                    nombre            = usuario.nombre,
                    apellido          = usuario.apellido,
                    email             = usuario.email,
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
                db.usuarioDao().actualizar(
                    id       = userId,
                    nombre   = nombre.trim(),
                    apellido = apellido.trim(),
                    email    = email.trim().lowercase()
                )
                session.guardarSesion(userId, email.trim().lowercase(), nombre.trim())
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
            }
            onDone()
        }
    }
}
