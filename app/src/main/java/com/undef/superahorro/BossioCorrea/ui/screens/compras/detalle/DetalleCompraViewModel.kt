package com.undef.superahorro.BossioCorrea.ui.screens.compras.detalle

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.BossioCorrea.data.repository.CompraRepository
import com.undef.superahorro.BossioCorrea.domain.model.Compra
import com.undef.superahorro.BossioCorrea.domain.model.Producto
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetalleCompraViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = CompraRepository()

    private val _uiState = MutableStateFlow<UiState<Compra>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun cargar(compraId: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val compra = try { repo.getCompraById(compraId) } catch (_: Exception) { null }
            _uiState.value = if (compra != null) UiState.Success(compra)
            else UiState.Error("Compra no encontrada")
        }
    }

    fun eliminarProducto(productoId: String) {
        viewModelScope.launch {
            val compraId = (_uiState.value as? UiState.Success)?.data?.id ?: return@launch
            repo.eliminarProducto(compraId, productoId)
            // Actualizar state local para que la UI reaccione sin recargar de la nube
            _uiState.update { current ->
                if (current !is UiState.Success) return@update current
                UiState.Success(
                    current.data.copy(
                        productos = current.data.productos.filterNot { it.id == productoId }
                    )
                )
            }
        }
    }

    fun agregarProducto(producto: Producto) {
        viewModelScope.launch {
            val compraId = (_uiState.value as? UiState.Success)?.data?.id ?: return@launch
            repo.agregarProducto(compraId, producto)
            // Recargar para obtener la compra actualizada desde Firestore
            cargar(compraId)
        }
    }
}
