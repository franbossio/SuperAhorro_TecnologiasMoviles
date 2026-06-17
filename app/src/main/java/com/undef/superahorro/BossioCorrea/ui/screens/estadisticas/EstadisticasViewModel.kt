package com.undef.superahorro.BossioCorrea.ui.screens.estadisticas

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

enum class PeriodoEstadisticas(val dias: Long) {
    SIETE_DIAS(7),
    TREINTA_DIAS(30),
    TRES_MESES(90),
    UN_ANIO(365)
}

data class EstadisticasData(
    val totalGastado       : Double,
    val cantidadCompras    : Int,
    val promedio           : Double,
    val gastosPorSuper     : List<Pair<String, Float>>,
    val topProductos       : List<Pair<String, Int>>,
    val gastosPorMes       : List<Double>,
    val periodoSeleccionado: PeriodoEstadisticas
)

class EstadisticasViewModel(application: Application) : AndroidViewModel(application) {

    private val repo    = CompraRepository.create(application)
    private val session = SessionManager(application)

    private val _uiState = MutableStateFlow<UiState<EstadisticasData>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var observarJob: Job? = null
    private var todasLasCompras: List<Compra> = emptyList()
    private var periodoActual: PeriodoEstadisticas = PeriodoEstadisticas.TREINTA_DIAS

    init { cargar() }

    fun cargar() {
        observarJob?.cancel()
        observarJob = viewModelScope.launch {
            val userId = session.userId.first()
            if (userId == SessionManager.NO_SESSION) {
                _uiState.value = UiState.Error("Sesión expirada")
                return@launch
            }
            repo.getComprasFlow(userId).collect { compras ->
                todasLasCompras = compras
                actualizarEstado()
            }
        }
    }

    /** Cambia el período de las estadísticas (7 días, 30 días, 3 meses, 1 año) y recalcula. */
    fun seleccionarPeriodo(periodo: PeriodoEstadisticas) {
        periodoActual = periodo
        actualizarEstado()
    }

    private fun actualizarEstado() {
        val limite = LocalDate.now().minusDays(periodoActual.dias)
        val comprasDelPeriodo = todasLasCompras.filter { !it.fecha.isBefore(limite) }

        val total = comprasDelPeriodo.sumOf { it.total }
        val porSuper = if (total > 0) {
            comprasDelPeriodo.groupBy { it.supermercado }
                .map { (s, list) -> s to (list.sumOf { it.total } / total).toFloat() }
                .sortedByDescending { it.second }
        } else emptyList()
        val topProds = comprasDelPeriodo
            .flatMap { it.productos }
            .groupBy { it.nombre }
            .map { (n, list) -> n to list.sumOf { it.cantidad } }
            .sortedByDescending { it.second }
            .take(5)
        val anioActual = LocalDate.now().year
        val gastosPorMes = (1..12).map { mes ->
            todasLasCompras.filter { it.fecha.year == anioActual && it.fecha.monthValue == mes }
                .sumOf { it.total }
        }
        _uiState.value = UiState.Success(
            EstadisticasData(
                totalGastado        = total,
                cantidadCompras     = comprasDelPeriodo.size,
                promedio            = if (comprasDelPeriodo.isEmpty()) 0.0 else total / comprasDelPeriodo.size,
                gastosPorSuper      = porSuper,
                topProductos        = topProds,
                gastosPorMes        = gastosPorMes,
                periodoSeleccionado = periodoActual
            )
        )
    }
}
