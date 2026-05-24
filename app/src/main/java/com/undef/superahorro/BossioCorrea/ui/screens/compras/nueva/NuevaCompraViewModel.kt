package com.undef.superahorro.BossioCorrea.ui.screens.compras.nueva

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.BossioCorrea.data.local.AppDatabase
import com.undef.superahorro.BossioCorrea.data.local.SessionManager
import com.undef.superahorro.BossioCorrea.data.repository.CompraRepository
import com.undef.superahorro.BossioCorrea.domain.model.Producto
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NuevaCompraViewModel(application: Application) : AndroidViewModel(application) {

    private val repo    = CompraRepository(AppDatabase.getInstance(application))
    private val session = SessionManager(application)

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Success(Unit))
    val uiState = _uiState.asStateFlow()

    // Productos que se van agregando antes de guardar la compra
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos = _productos.asStateFlow()

    fun agregarProducto(producto: Producto) {
        _productos.value = _productos.value + producto
    }

    fun eliminarProducto(productoId: Int) {
        _productos.value = _productos.value.filterNot { it.id == productoId }
    }

    fun guardar(
        supermercado : String,
        fecha        : String,
        hora         : String,
        total        : String,
        onExito      : () -> Unit
    ) {
        // Validaciones básicas
        if (supermercado.isBlank()) { _uiState.value = UiState.Error("Seleccioná un supermercado"); return }
        if (fecha.isBlank())        { _uiState.value = UiState.Error("Seleccioná una fecha"); return }
        if (hora.isBlank())         { _uiState.value = UiState.Error("Ingresá la hora"); return }
        val totalDouble = total.replace(",", ".").toDoubleOrNull()
        if (totalDouble == null || totalDouble <= 0) { _uiState.value = UiState.Error("Ingresá un total válido"); return }

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val usuarioId = session.userId.first()
                if (usuarioId == SessionManager.NO_SESSION) {
                    _uiState.value = UiState.Error("Sesión expirada")
                    return@launch
                }

                // Convertir fecha de "dd/MM/yyyy" a "yyyy-MM-dd" para guardar en BD
                val partes = fecha.split("/")
                val fechaBD = "${partes[2]}-${partes[1]}-${partes[0]}"

                repo.guardarCompra(
                    usuarioId    = usuarioId,
                    fecha        = fechaBD,
                    hora         = hora,
                    supermercado = supermercado,
                    total        = totalDouble,
                    productos    = _productos.value
                )
                _uiState.value = UiState.Success(Unit)
                onExito()
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al guardar: ${e.message}")
            }
        }
    }
}