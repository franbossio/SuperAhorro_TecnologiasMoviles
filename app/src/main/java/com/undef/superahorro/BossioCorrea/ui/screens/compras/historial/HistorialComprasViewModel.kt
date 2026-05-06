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

class HistorialComprasViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<Compra>>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            delay(500)
            _uiState.value = UiState.Success(comprasMock.sortedByDescending { it.fecha })
        }
    }
}
