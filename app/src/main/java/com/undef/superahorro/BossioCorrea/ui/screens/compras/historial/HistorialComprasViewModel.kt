package com.undef.superahorro.BossioCorrea.ui.screens.compras.historial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.BossioCorrea.data.mock.comprasMock
import com.undef.superahorro.BossioCorrea.domain.model.Compra
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HistorialComprasViewModel : ViewModel() {

    // Lista completa (fuente de verdad)
    private val _todasLasCompras = MutableStateFlow<List<Compra>>(emptyList())

    // Estado que ve la UI (puede estar cargando, en error, o ser la lista filtrada)
    private val _uiState = MutableStateFlow<UiState<List<Compra>>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    // Filtros activos
    private val _mesSel = MutableStateFlow<Int?>(null)   // null = "Todos"
    val mesSel = _mesSel.asStateFlow()

    private val _anioSel = MutableStateFlow<Int>(2026)
    val anioSel = _anioSel.asStateFlow()

    init {
        viewModelScope.launch {
            delay(500)
            val datos = comprasMock.sortedByDescending { it.fecha }
            _todasLasCompras.value = datos
            // Inicializar el año al más reciente que haya en los datos
            _anioSel.value = datos.maxOfOrNull { it.fecha.year } ?: 2026
            aplicarFiltros()
        }
    }

    fun seleccionarMes(mes: Int?) {
        _mesSel.value = mes
        aplicarFiltros()
    }

    fun seleccionarAnio(anio: Int) {
        _anioSel.value = anio
        _mesSel.value  = null          // al cambiar de año reseteamos el mes
        aplicarFiltros()
    }

    fun eliminarCompra(compraId: Int) {
        _todasLasCompras.update { it.filterNot { c -> c.id == compraId } }
        aplicarFiltros()
    }

    private fun aplicarFiltros() {
        val mes  = _mesSel.value
        val anio = _anioSel.value
        val resultado = _todasLasCompras.value.filter { c ->
            c.fecha.year == anio && (mes == null || c.fecha.monthValue == mes)
        }
        _uiState.value = UiState.Success(resultado)
    }

    /** Años disponibles en los datos para mostrar en el selector */
    fun aniosDisponibles(): List<Int> =
        _todasLasCompras.value.map { it.fecha.year }.distinct().sorted()
}