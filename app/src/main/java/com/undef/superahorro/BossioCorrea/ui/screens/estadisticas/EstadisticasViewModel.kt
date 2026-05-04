package com.undef.superahorro.BossioCorrea.ui.screens.estadisticas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.BossioCorrea.data.mock.comprasMock
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EstadisticasData(
    val totalGastado: Double,
    val cantidadCompras: Int,
    val promedio: Double,
    val gastosPorSuper: List<Pair<String, Double>>,
    val productosMasComprados: List<Pair<String, Int>>,
    val gastosMensuales: List<Pair<String, Double>>
)

class EstadisticasViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<EstadisticasData>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            delay(600)

            val total    = comprasMock.sumOf { it.total }
            val cantidad = comprasMock.size
            val promedio = if (cantidad > 0) total / cantidad else 0.0

            val porSuper = comprasMock
                .groupBy { it.supermercado }
                .map { (super_, compras) -> super_ to compras.sumOf { it.total } }
                .sortedByDescending { it.second }

            val productosContados = comprasMock
                .flatMap { it.productos }
                .groupBy { it.nombre }
                .map { (nombre, prods) -> nombre to prods.sumOf { it.cantidad } }
                .sortedByDescending { it.second }
                .take(5)

            val mensuales = listOf(
                "Ene" to 42000.0,
                "Feb" to 12500.0,
                "Mar" to 32800.0,
                "Abr" to 27860.5,
                "May" to 0.0
            )

            _uiState.value = UiState.Success(
                EstadisticasData(
                    totalGastado          = total,
                    cantidadCompras       = cantidad,
                    promedio              = promedio,
                    gastosPorSuper        = porSuper,
                    productosMasComprados = productosContados,
                    gastosMensuales       = mensuales
                )
            )
        }
    }
}
