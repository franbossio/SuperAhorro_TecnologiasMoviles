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
    val totalGastado    : Double,
    val cantidadCompras : Int,
    val promedio        : Double,
    val gastosPorSuper  : List<Pair<String, Float>>,
    val topProductos    : List<Pair<String, Int>>
)

class EstadisticasViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<EstadisticasData>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init { cargar() }

    private fun cargar() = viewModelScope.launch {
        _uiState.value = UiState.Loading
        delay(600)
        val total = comprasMock.sumOf { it.total }
        val porSuper = comprasMock
            .groupBy { it.supermercado }
            .map { (s, list) -> s to (list.sumOf { it.total } / total).toFloat() }
            .sortedByDescending { it.second }
        val topProds = comprasMock
            .flatMap { it.productos }
            .groupBy { it.nombre }
            .map { (n, list) -> n to list.sumOf { it.cantidad } }
            .sortedByDescending { it.second }
            .take(5)
        _uiState.value = UiState.Success(
            EstadisticasData(
                totalGastado    = total,
                cantidadCompras = comprasMock.size,
                promedio        = if (comprasMock.isEmpty()) 0.0 else total / comprasMock.size,
                gastosPorSuper  = porSuper,
                topProductos    = topProds
            )
        )
    }
}
