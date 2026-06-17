package com.undef.superahorro.BossioCorrea.ui.screens.compras.listado

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.BossioCorrea.data.local.SessionManager
import com.undef.superahorro.BossioCorrea.data.repository.CompraRepository
import com.undef.superahorro.BossioCorrea.domain.model.Compra
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ListadoComprasViewModel(application: Application) : AndroidViewModel(application) {

    private val repo    = CompraRepository.create(application)
    private val session = SessionManager(application)

    private val _uiState = MutableStateFlow<UiState<List<Compra>>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var observarJob: Job? = null

    init { cargar() }

    fun cargar() {
        observarJob?.cancel()
        observarJob = viewModelScope.launch {
            val usuarioId = session.userId.first()
            if (usuarioId == SessionManager.NO_SESSION) {
                _uiState.value = UiState.Error("Sesión expirada")
                return@launch
            }
            repo.getComprasFlow(usuarioId).collect { compras ->
                _uiState.value = UiState.Success(compras)
            }
        }
    }

    fun eliminar(compraId: String) {
        viewModelScope.launch { repo.eliminarCompra(compraId) }
    }
}
