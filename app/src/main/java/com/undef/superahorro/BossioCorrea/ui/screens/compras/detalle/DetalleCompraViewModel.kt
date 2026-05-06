package com.undef.superahorro.BossioCorrea.ui.screens.compras.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.BossioCorrea.data.mock.comprasMock
import com.undef.superahorro.BossioCorrea.domain.model.Compra
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetalleCompraViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<Compra>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun cargar(id: Int) = viewModelScope.launch {
        _uiState.value = UiState.Loading
        delay(400)
        val compra = comprasMock.firstOrNull { it.id == id }
        _uiState.value = if (compra != null) UiState.Success(compra) else UiState.Error("No encontrada")
    }
}
