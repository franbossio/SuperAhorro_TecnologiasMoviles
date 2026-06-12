package com.undef.superahorro.BossioCorrea.ui.screens.compras.nueva

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.BossioCorrea.data.local.SessionManager
import com.undef.superahorro.BossioCorrea.data.repository.CompraRepository
import com.undef.superahorro.BossioCorrea.data.repository.GroqRepository
import com.undef.superahorro.BossioCorrea.data.repository.GroqResult
import com.undef.superahorro.BossioCorrea.data.repository.GrupoProductos
import com.undef.superahorro.BossioCorrea.domain.model.Producto
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class NuevaCompraViewModel(application: Application) : AndroidViewModel(application) {

    private val repo      = CompraRepository()
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

    // Comparativa de precios: por cada producto del carrito, lista de precios
    // pagados este mes (en esta y otras compras), ordenada de menor a mayor.
    private val _comparaciones = MutableStateFlow<Map<String, List<ItemComparacionPrecio>>>(emptyMap())
    val comparaciones = _comparaciones.asStateFlow()

    // true mientras se consulta a la IA para identificar productos equivalentes
    private val _agrupandoComparativa = MutableStateFlow(false)
    val agrupandoComparativa = _agrupandoComparativa.asStateFlow()

    private var comparacionesJob: Job? = null

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
                        onResultado(t.supermercado, fechaIaValida(t.fecha), t.hora, t.total, t.productos)
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

    // Devuelve la fecha de la IA si es plausible (año actual o el anterior),
    // o "" si no se pudo parsear o el año es claramente erróneo. Con "" el
    // formulario no autocompleta y el usuario debe elegir la fecha a mano.
    private fun fechaIaValida(fecha: String): String {
        val partes = fecha.trim().split("/")
        if (partes.size != 3) return ""
        return try {
            val dia  = partes[0].padStart(2, '0')
            val mes  = partes[1].padStart(2, '0')
            val anio = partes[2].let { if (it.length == 2) "20$it" else it }
            val parsed = LocalDate.parse("$anio-$mes-$dia")
            val anioActual = LocalDate.now().year
            if (parsed.year in (anioActual - 1)..anioActual) fecha else ""
        } catch (_: Exception) { "" }
    }

    // ── Productos ─────────────────────────────────────────────────────────────

    private suspend fun copiarImagenInterna(uri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val input = context.contentResolver.openInputStream(uri) ?: return@withContext null
            val file  = File(context.filesDir, "ticket_${System.currentTimeMillis()}.jpg")
            file.outputStream().use { out -> input.use { it.copyTo(out) } }
            file.absolutePath
        } catch (e: Exception) { null }
    }

    fun agregarProducto(producto: Producto) {
        _productos.value = _productos.value + producto
    }

    fun eliminarProducto(productoId: String) {
        _productos.value = _productos.value.filterNot { it.id == productoId }
    }

    // ── Comparativa de precios ───────────────────────────────────────────────

    /**
     * Recalcula la comparativa de precios para los productos del carrito,
     * cruzándolos con las compras del usuario en el mismo mes que [fecha]
     * (formato "dd/MM/yyyy"). Sólo se incluyen productos con precios
     * registrados en más de un supermercado ese mes.
     *
     * Para detectar el mismo producto aunque esté escrito distinto entre
     * compras (abreviaturas, mayúsculas, unidades, etc.) se usa la IA de
     * Groq para agrupar nombres equivalentes antes de comparar.
     */
    fun actualizarComparaciones(fecha: String, supermercado: String) {
        comparacionesJob?.cancel()
        comparacionesJob = viewModelScope.launch {
            val usuarioId = session.userId.first()
            if (usuarioId == SessionManager.NO_SESSION || _productos.value.isEmpty()) {
                _comparaciones.value = emptyMap()
                return@launch
            }

            // Pequeño debounce para no disparar una consulta a la IA por cada
            // cambio rápido (agregar producto, tipear fecha, etc.)
            delay(500)

            val anioMes  = anioMesDeFecha(fecha)
            val historico = repo.getProductosDelMes(usuarioId, anioMes)

            val productosCarrito = _productos.value
                .distinctBy { it.nombre.trim().lowercase() }
                .filter { it.nombre.isNotBlank() }

            val todosLosNombres = (productosCarrito.map { it.nombre.trim() } + historico.map { it.nombre.trim() })
                .distinctBy { it.lowercase() }

            _agrupandoComparativa.value = true
            val grupos = groqRepo.agruparProductos(todosLosNombres)
            _agrupandoComparativa.value = false

            val resultado = linkedMapOf<String, List<ItemComparacionPrecio>>()
            productosCarrito.forEach { producto ->
                val nombre = producto.nombre.trim()

                val grupo = grupos.find { g -> g.items.any { it.equals(nombre, ignoreCase = true) } }
                    ?: GrupoProductos(nombre, listOf(nombre))
                val itemsDelGrupo = grupo.items.map { it.lowercase() }.toSet()

                val items = mutableListOf<ItemComparacionPrecio>()
                if (supermercado.isNotBlank()) {
                    items += ItemComparacionPrecio(supermercado, producto.precio, esActual = true, nombreProducto = nombre)
                }
                historico.filter { it.nombre.trim().lowercase() in itemsDelGrupo }
                    .forEach { items += ItemComparacionPrecio(it.supermercado, it.precio, esActual = false, nombreProducto = it.nombre.trim()) }

                val supersDistintos = items.map { it.supermercado.trim().lowercase() }.distinct()
                if (items.size > 1 && supersDistintos.size > 1) {
                    resultado[nombre] = items.sortedBy { it.precio }
                }
            }
            _comparaciones.value = resultado
        }
    }


    // Convierte una fecha "dd/MM/yyyy" (o "dd/MM/yy") a "yyyy-MM".
    // Si está vacía o no se puede parsear, usa el mes actual.
    private fun anioMesDeFecha(fecha: String): String {
        val partes = fecha.trim().split("/")
        if (partes.size == 3) {
            val anio = partes[2].let { if (it.length == 2) "20$it" else it }
            val mes  = partes[1].padStart(2, '0')
            if (anio.length == 4 && anio.all { it.isDigit() } && mes.all { it.isDigit() }) {
                return "$anio-$mes"
            }
        }
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
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
                val anio    = partes[2].let { if (it.length == 2) "20$it" else it }
                val mes     = partes[1].padStart(2, '0')
                val dia     = partes[0].padStart(2, '0')
                val fechaBD = "$anio-$mes-$dia"

                // Normaliza la hora a "HH:mm" para garantizar parseo correcto al leer
                val horaBD = try {
                    LocalTime.parse(hora.trim(), DateTimeFormatter.ofPattern("H:mm"))
                        .format(DateTimeFormatter.ofPattern("HH:mm"))
                } catch (_: Exception) { hora.trim() }

                val ticketPath = _ticketUri.value?.let { copiarImagenInterna(it) }

                repo.guardarCompra(
                    usuarioId    = usuarioId,
                    fecha        = fechaBD,
                    hora         = horaBD,
                    supermercado = supermercado,
                    total        = totalDouble,
                    productos    = _productos.value,
                    ticketUri    = ticketPath
                )
                _uiState.value = UiState.Success(Unit)
                onExito()
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al guardar: ${e.message}")
            }
        }
    }
}

// Una entrada de la comparativa de precios: precio pagado por un producto
// en un supermercado durante el mes seleccionado. [esActual] indica que
// corresponde al precio recién ingresado en esta compra (todavía sin guardar).
// [nombreProducto] es el nombre tal como figura en esa compra puntual: puede
// diferir del nombre del producto del carrito si la IA los identificó como
// el mismo producto pese a estar escritos distinto.
data class ItemComparacionPrecio(
    val supermercado: String,
    val precio: Double,
    val esActual: Boolean,
    val nombreProducto: String
)