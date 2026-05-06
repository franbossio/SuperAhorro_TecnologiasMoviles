package com.undef.superahorro.BossioCorrea.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.BossioCorrea.data.mock.comprasMock
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeData(
    val saludo          : String,
    val gastoMes        : Double,
    val cantidadCompras : Int,
    val ultimoSuper     : String,
    val ultimaFecha     : String,
    val ultimoTotal     : Double,
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<HomeData>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init { cargar() }

    private fun cargar() = viewModelScope.launch {
        _uiState.value = UiState.Loading
        delay(600)
        val ultima = comprasMock.maxByOrNull { it.fecha }
        _uiState.value = UiState.Success(
            HomeData(
                saludo          = "Juan 👋",
                gastoMes        = comprasMock.sumOf { it.total },
                cantidadCompras = comprasMock.size,
                ultimoSuper     = ultima?.supermercado ?: "-",
                ultimaFecha     = ultima?.fecha?.toString() ?: "-",
                ultimoTotal     = ultima?.total ?: 0.0
            )
        )
    }
}
