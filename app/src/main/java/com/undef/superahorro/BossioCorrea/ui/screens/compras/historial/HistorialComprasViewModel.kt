package com.undef.superahorro.BossioCorrea.ui.screens.compras.historial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.BossioCorrea.data.mock.comprasMock
import com.undef.superahorro.BossioCorrea.domain.model.Compra
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.Locale

class HistorialComprasViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<Map<String, List<Compra>>>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            delay(500)
            val fmt = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "AR"))
            val agrupado = comprasMock
                .sortedByDescending { it.fecha }
                .groupBy { it.fecha.format(fmt).replaceFirstChar { c -> c.uppercase() } }
            _uiState.value = UiState.Success(agrupado)
        }
    }
}