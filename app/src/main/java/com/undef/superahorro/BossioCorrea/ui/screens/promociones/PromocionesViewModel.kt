package com.undef.superahorro.BossioCorrea.ui.screens.promociones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.BossioCorrea.data.repository.Promocion
import com.undef.superahorro.BossioCorrea.data.repository.PromocionesRepository
import com.undef.superahorro.BossioCorrea.data.repository.PromocionesResult
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PromocionesViewModel : ViewModel() {

    private val repo = PromocionesRepository()

    private val _uiState = MutableStateFlow<UiState<List<Promocion>>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = when (val resultado = repo.buscarPromocionesArgentina()) {
                is PromocionesResult.Exito -> UiState.Success(resultado.promociones)
                is PromocionesResult.Error -> UiState.Error(resultado.mensaje)
            }
        }
    }
}
