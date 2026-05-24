package com.undef.superahorro.BossioCorrea.ui.screens.compras.nueva

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.BossioCorrea.data.local.AppDatabase
import com.undef.superahorro.BossioCorrea.data.local.SessionManager
import com.undef.superahorro.BossioCorrea.data.repository.CompraRepository
import com.undef.superahorro.BossioCorrea.data.repository.GroqRepository
import com.undef.superahorro.BossioCorrea.data.repository.GroqResult
import com.undef.superahorro.BossioCorrea.domain.model.Producto
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NuevaCompraViewModel(application: Application) : AndroidViewModel(application) {

    private val repo      = CompraRepository(AppDatabase.getInstance(application))
    private val session   = SessionManager(application)
    private val groqRepo  = GroqRepository()
    private val context   = application.applicationContext

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Success(Unit))
    val uiState = _uiState.asStateFlow()

    // Productos agregados a la compra
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos = _productos.asStateFlow()

    // URI de la imagen del ticket (para mostrarla en la UI)
    private val _ticketUri = MutableStateFlow<Uri?>(null)
    val ticketUri = _ticketUri.asStateFlow()

    // Estado del análisis de IA (separado del UiState principal)
    private val _analizando = MutableStateFlow(false)
    val analizando = _analizando.asStateFlow()

    private val _errorIA = MutableStateFlow<String?>(null)
    val errorIA = _errorIA.asStateFlow()

    // ── Ticket ────────────────────────────────────────────────────────────────

    fun setTicketUri(uri: Uri) {
        _ticketUri.value = uri
    }

    /**
     * Llama a Groq con la imagen del ticket.
     * Si la IA devuelve datos, los retorna via callback para que la Screen
     * los aplique a los campos del formulario.
     */
    fun analizarTicketConIA(
        onResultado: (supermercado: String, fecha: String, hora: String, total: String, productos: List<Producto>) -> Unit
    ) {
        val uri = _ticketUri.value ?: run {
            _errorIA.value = "Primero tomá o seleccioná una foto del ticket"
            return
        }

        viewModelScope.launch {
            _analizando.value = true
            _errorIA.value    = null
            try {
                @Suppress("DEPRECATION")
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)

                when (val resultado = groqRepo.analizarTicket(bitmap)) {
                    is GroqResult.Exito -> {
                        val t = resultado.ticket
                        // Reemplazar la lista de productos con los detectados
                        _productos.value = t.productos
                        onResultado(t.supermercado, t.fecha, t.hora, t.total, t.productos)
                    }
                    is GroqResult.Error -> {
                        _errorIA.value = resultado.mensaje
                    }
                }
            } catch (e: Exception) {
                _errorIA.value = "No se pudo procesar la imagen: ${e.message}"
            } finally {
                _analizando.value = false
            }
        }
    }

    // ── Productos ─────────────────────────────────────────────────────────────

    fun agregarProducto(producto: Producto) {
        _productos.value = _productos.value + producto
    }

    fun eliminarProducto(productoId: Int) {
        _productos.value = _productos.value.filterNot { it.id == productoId }
    }

    // ── Guardar compra ────────────────────────────────────────────────────────

    fun guardar(
        supermercado : String,
        fecha        : String,
        hora         : String,
        total        : String,
        onExito      : () -> Unit
    ) {
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

                val partes  = fecha.split("/")
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