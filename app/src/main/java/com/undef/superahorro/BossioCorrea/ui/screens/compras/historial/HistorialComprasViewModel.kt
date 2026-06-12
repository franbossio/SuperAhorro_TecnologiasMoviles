package com.undef.superahorro.BossioCorrea.ui.screens.compras.historial

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
import java.time.LocalDate

class HistorialComprasViewModel(application: Application) : AndroidViewModel(application) {

    private val repo    = CompraRepository()
    private val session = SessionManager(application)

    private val _todasLasCompras = MutableStateFlow<List<Compra>>(emptyList())

    private val _uiState = MutableStateFlow<UiState<List<Compra>>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _mesSel = MutableStateFlow<Int?>(null)
    val mesSel = _mesSel.asStateFlow()

    private val _anioSel = MutableStateFlow(LocalDate.now().year)
    val anioSel = _anioSel.asStateFlow()

    private var primeraEmision = true
    private var observarJob: Job? = null

    init { cargar() }

    fun cargar() {
        observarJob?.cancel()
        primeraEmision = true
        observarJob = viewModelScope.launch {
            val userId = session.userId.first()
            if (userId == SessionManager.NO_SESSION) {
                _uiState.value = UiState.Error("Sesión expirada")
                return@launch
            }
            repo.getComprasFlow(userId).collect { compras ->
                val sorted = compras.sortedByDescending { it.fecha }
                _todasLasCompras.value = sorted
                if (primeraEmision) {
                    _anioSel.value = sorted.maxOfOrNull { it.fecha.year } ?: LocalDate.now().year
                    primeraEmision = false
                }
                aplicarFiltros()
            }
        }
    }

    fun seleccionarMes(mes: Int?) {
        _mesSel.value = mes
        aplicarFiltros()
    }

    fun seleccionarAnio(anio: Int) {
        _anioSel.value = anio
        _mesSel.value  = null
        aplicarFiltros()
    }

    fun eliminarCompra(compraId: String) {
        viewModelScope.launch { repo.eliminarCompra(compraId) }
    }

    private fun aplicarFiltros() {
        val mes  = _mesSel.value
        val anio = _anioSel.value
        val resultado = _todasLasCompras.value.filter { c ->
            c.fecha.year == anio && (mes == null || c.fecha.monthValue == mes)
        }
        _uiState.value = UiState.Success(resultado)
    }

    fun aniosDisponibles(): List<Int> =
        _todasLasCompras.value.map { it.fecha.year }.distinct().sorted()
}
