package com.undef.superahorro.BossioCorrea.ui.screens.estadisticas

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

data class EstadisticasData(
    val totalGastado    : Double,
    val cantidadCompras : Int,
    val promedio        : Double,
    val gastosPorSuper  : List<Pair<String, Float>>,
    val topProductos    : List<Pair<String, Int>>
)

class EstadisticasViewModel(application: Application) : AndroidViewModel(application) {

    private val repo    = CompraRepository(AppDatabase.getInstance(application))
    private val session = SessionManager(application)

    private val _uiState = MutableStateFlow<UiState<EstadisticasData>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init { observar() }

    private fun observar() = viewModelScope.launch {
        val userId = session.userId.first()
        if (userId == SessionManager.NO_SESSION) {
            _uiState.value = UiState.Error("Sesión expirada")
            return@launch
        }
        repo.getComprasFlow(userId).collect { compras ->
            val total = compras.sumOf { it.total }
            val porSuper = if (total > 0) {
                compras.groupBy { it.supermercado }
                    .map { (s, list) -> s to (list.sumOf { it.total } / total).toFloat() }
                    .sortedByDescending { it.second }
            } else emptyList()
            val topProds = compras
                .flatMap { it.productos }
                .groupBy { it.nombre }
                .map { (n, list) -> n to list.sumOf { it.cantidad } }
                .sortedByDescending { it.second }
                .take(5)
            _uiState.value = UiState.Success(
                EstadisticasData(
                    totalGastado    = total,
                    cantidadCompras = compras.size,
                    promedio        = if (compras.isEmpty()) 0.0 else total / compras.size,
                    gastosPorSuper  = porSuper,
                    topProductos    = topProds
                )
            )
        }
    }
}
