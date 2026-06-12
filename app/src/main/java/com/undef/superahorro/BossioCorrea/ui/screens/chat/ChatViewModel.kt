package com.undef.superahorro.BossioCorrea.ui.screens.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.BossioCorrea.data.local.AppDatabase
import com.undef.superahorro.BossioCorrea.data.local.SessionManager
import com.undef.superahorro.BossioCorrea.data.repository.ChatResult
import com.undef.superahorro.BossioCorrea.data.repository.CompraRepository
import com.undef.superahorro.BossioCorrea.data.repository.GroqRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

data class MensajeChat(
    val texto     : String,
    val esUsuario : Boolean,
    val esError   : Boolean = false
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val repo     = CompraRepository(AppDatabase.getInstance(application))
    private val session  = SessionManager(application)
    private val groqRepo = GroqRepository()

    private val _mensajes = MutableStateFlow<List<MensajeChat>>(emptyList())
    val mensajes = _mensajes.asStateFlow()

    private val _escribiendo = MutableStateFlow(false)
    val escribiendo = _escribiendo.asStateFlow()

    fun enviar(pregunta: String) {
        val texto = pregunta.trim()
        if (texto.isBlank() || _escribiendo.value) return

        _mensajes.value = _mensajes.value + MensajeChat(texto, esUsuario = true)

        viewModelScope.launch {
            _escribiendo.value = true

            val contexto = armarContextoCompras()
            // Mensajes anteriores (sin la pregunta recién agregada) para mantener el hilo
            val previos = _mensajes.value.dropLast(1)
                .filterNot { it.esError }
                .takeLast(10)
                .map { (if (it.esUsuario) "user" else "assistant") to it.texto }

            val mensaje = when (val resultado = groqRepo.consultarHistorial(texto, contexto, previos)) {
                is ChatResult.Exito -> MensajeChat(resultado.respuesta, esUsuario = false)
                is ChatResult.Error -> MensajeChat(resultado.mensaje, esUsuario = false, esError = true)
            }
            _mensajes.value = _mensajes.value + mensaje
            _escribiendo.value = false
        }
    }

    /** Arma un resumen en texto plano del historial de compras para dárselo de contexto a la IA. */
    private suspend fun armarContextoCompras(): String {
        val userId = session.userId.first()
        if (userId == SessionManager.NO_SESSION) return "El usuario no tiene compras registradas."

        val compras = repo.getComprasFlow(userId).first()
        if (compras.isEmpty()) return "El usuario no tiene compras registradas."

        val fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return buildString {
            appendLine("El usuario tiene ${compras.size} compras registradas (de más reciente a más antigua):")
            compras.take(40).forEach { c ->
                appendLine("- ${c.fecha.format(fmt)} ${c.hora} | ${c.supermercado} | Total: $${"%.2f".format(c.total)}")
                c.productos.forEach { p ->
                    appendLine("    * ${p.nombre} x${p.cantidad} = $${"%.2f".format(p.subtotal)}")
                }
            }
        }
    }
}
