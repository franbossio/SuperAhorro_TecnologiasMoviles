package com.undef.superahorro.BossioCorrea.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.BossioCorrea.data.mock.comprasMock
import com.undef.superahorro.BossioCorrea.data.mock.usuarioMock
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

data class HomeData(
    val saludo           : String,
    val gastoMes         : Double,
    val cantidadCompras  : Int,
    val ultimoSuper      : String,
    val ultimaFecha      : String,
    val ultimoTotal      : Double,
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<HomeData>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            delay(700)
            val ultima    = comprasMock.first()
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            _uiState.value = UiState.Success(
                HomeData(
                    saludo          = "¡Hola, ${usuarioMock.nombre}!",
                    gastoMes        = comprasMock.sumOf { it.total },
                    cantidadCompras = comprasMock.size,
                    ultimoSuper     = ultima.supermercado,
                    ultimaFecha     = ultima.fecha.format(formatter),
                    ultimoTotal     = ultima.total,
                )
            )
        }
    }
}