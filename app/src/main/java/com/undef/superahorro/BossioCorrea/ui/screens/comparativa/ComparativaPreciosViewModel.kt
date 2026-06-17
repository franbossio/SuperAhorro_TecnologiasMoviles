package com.undef.superahorro.BossioCorrea.ui.screens.comparativa

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.BossioCorrea.data.local.SessionManager
import com.undef.superahorro.BossioCorrea.data.repository.CompraRepository
import com.undef.superahorro.BossioCorrea.data.repository.GroqRepository
import com.undef.superahorro.BossioCorrea.data.repository.GrupoProductos
import com.undef.superahorro.BossioCorrea.domain.model.PrecioProducto
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class ComparativaData(
    val mesesDisponibles    : List<String>,        // "yyyy-MM", más reciente primero
    val mesSeleccionado     : String,
    val productosDisponibles: List<String>,        // nombres genéricos (agrupados por IA), orden alfabético
    val productoSeleccionado: String?,
    val comparacion         : List<PrecioProducto>, // entradas del producto seleccionado, ordenadas por precio
    val cargandoIA          : Boolean = false       // true mientras la IA agrupa los productos del mes
)

class ComparativaPreciosViewModel(application: Application) : AndroidViewModel(application) {

    private val repo    = CompraRepository.create(application)
    private val session = SessionManager(application)
    private val groqRepo = GroqRepository()

    private val _uiState = MutableStateFlow<UiState<ComparativaData>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var usuarioId = SessionManager.NO_SESSION
    private var productosDelMes: List<PrecioProducto> = emptyList()

    // Grupos de nombres equivalentes (mismo producto) detectados por la IA
    // para los productos del mes seleccionado.
    private var grupos: List<GrupoProductos> = emptyList()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            usuarioId = session.userId.first()
            if (usuarioId == SessionManager.NO_SESSION) {
                _uiState.value = UiState.Error("Sesión expirada")
                return@launch
            }

            val meses = repo.getMesesConCompras(usuarioId)
            if (meses.isEmpty()) {
                _uiState.value = UiState.Success(
                    ComparativaData(emptyList(), mesActual(), emptyList(), null, emptyList(), cargandoIA = false)
                )
                return@launch
            }

            val mesActual  = mesActual()
            val mesInicial = if (mesActual in meses) mesActual else meses.first()
            cargarMes(meses, mesInicial)
        }
    }

    fun seleccionarMes(mes: String) {
        val actual = (_uiState.value as? UiState.Success)?.data ?: return
        viewModelScope.launch { cargarMes(actual.mesesDisponibles, mes) }
    }

    fun seleccionarProducto(nombre: String) {
        val actual = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.value = UiState.Success(
            actual.copy(productoSeleccionado = nombre, comparacion = comparacionDe(nombre))
        )
    }

    private suspend fun cargarMes(meses: List<String>, mes: String) {
        val previo = (_uiState.value as? UiState.Success)?.data
        _uiState.value = UiState.Success(
            ComparativaData(
                mesesDisponibles     = meses,
                mesSeleccionado      = mes,
                productosDisponibles = previo?.productosDisponibles ?: emptyList(),
                productoSeleccionado = previo?.productoSeleccionado,
                comparacion          = previo?.comparacion ?: emptyList(),
                cargandoIA           = true
            )
        )

        productosDelMes = repo.getProductosDelMes(usuarioId, mes)
        val nombresDistintos = productosDelMes.map { it.nombre.trim() }.distinctBy { it.lowercase() }

        grupos = groqRepo.agruparProductos(nombresDistintos)
        val nombres = grupos
            .map { it.nombre }
            .distinctBy { it.lowercase() }
            .sortedBy { it.lowercase() }

        val productoSel = nombres.firstOrNull()
        _uiState.value = UiState.Success(
            ComparativaData(
                mesesDisponibles     = meses,
                mesSeleccionado      = mes,
                productosDisponibles = nombres,
                productoSeleccionado = productoSel,
                comparacion          = productoSel?.let { comparacionDe(it) } ?: emptyList(),
                cargandoIA           = false
            )
        )
    }


    private fun comparacionDe(nombreCanonico: String): List<PrecioProducto> {
        val itemsDelGrupo = grupos
            .filter { it.nombre.equals(nombreCanonico, ignoreCase = true) }
            .flatMap { it.items }
            .map { it.lowercase() }
            .toSet()

        return productosDelMes
            .filter { it.nombre.trim().lowercase() in itemsDelGrupo }
            .sortedBy { it.precio }
    }

    private fun mesActual(): String =
        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
}
