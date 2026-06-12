package com.undef.superahorro.BossioCorrea.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.BossioCorrea.data.local.NotificationsPreferences
import com.undef.superahorro.BossioCorrea.data.local.SessionManager
import com.undef.superahorro.BossioCorrea.data.repository.AuthRepository
import com.undef.superahorro.BossioCorrea.data.repository.CompraRepository
import com.undef.superahorro.BossioCorrea.data.repository.Promocion
import com.undef.superahorro.BossioCorrea.data.repository.PromocionesRepository
import com.undef.superahorro.BossioCorrea.data.repository.PromocionesResult
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class HomeData(
    val saludo          : String,
    val gastoMes        : Double,
    val cantidadCompras : Int,
    val ultimoSuper     : String,
    val ultimaFecha     : String,
    val ultimoTotal     : Double,
    val usuarioNombre   : String,
    val usuarioApellido : String,
    val usuarioEmail    : String,
    val gastosPorDia    : List<Double>
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repo      = CompraRepository()
    private val session   = SessionManager(application)
    private val authRepo  = AuthRepository(SessionManager(application))
    private val promoRepo = PromocionesRepository()
    private val notificationsPrefs = NotificationsPreferences(application)

    private val _uiState = MutableStateFlow<UiState<HomeData>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _notificaciones = MutableStateFlow<List<Promocion>>(emptyList())
    val notificaciones = _notificaciones.asStateFlow()

    private var observarJob: Job? = null
    private var promocionesCache: List<Promocion>? = null

    init {
        cargar()
        cargarNotificaciones()
    }

    /**
     * Trae las mejores promociones vigentes para mostrarlas como notificaciones de "nueva oferta".
     * Si el usuario deshabilitó las notificaciones en Configuración, no se muestran (sin importar
     * si ya se habían cargado), y se vuelven a mostrar apenas las vuelve a habilitar.
     */
    private fun cargarNotificaciones() {
        viewModelScope.launch {
            notificationsPrefs.habilitadas.collect { habilitadas ->
                if (!habilitadas) {
                    _notificaciones.value = emptyList()
                    return@collect
                }

                val promociones = promocionesCache ?: run {
                    val resultado = promoRepo.buscarPromocionesArgentina()
                    if (resultado is PromocionesResult.Exito) {
                        // Ordena por mayor descuento (menor proporción precio/precioSinDescuento) primero.
                        resultado.promociones
                            .sortedBy { promo ->
                                val original = promo.precioSinDescuento
                                if (original != null && original > 0) (promo.precio / original) else 1.0
                            }
                            .take(5)
                            .also { promocionesCache = it }
                    } else emptyList()
                }
                _notificaciones.value = promociones
            }
        }
    }

    fun cargar() {
        observarJob?.cancel()
        observarJob = viewModelScope.launch {
            val userId = session.userId.first()
            if (userId == SessionManager.NO_SESSION) {
                _uiState.value = UiState.Error("Sesión expirada")
                return@launch
            }
            val fmtFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")

            val usuario  = try { authRepo.getUsuario(userId) } catch (_: Exception) { null }
            val nombre   = usuario?.nombre   ?: ""
            val apellido = usuario?.apellido ?: ""
            val email    = usuario?.email    ?: ""

            repo.getComprasFlow(userId).collect { compras ->
                val ahora  = LocalDate.now()
                val delMes = compras.filter {
                    it.fecha.year == ahora.year && it.fecha.monthValue == ahora.monthValue
                }.let { esteM ->
                    if (esteM.isNotEmpty()) esteM
                    else {
                        val mesMasReciente = compras.maxByOrNull { it.fecha }?.fecha
                        if (mesMasReciente != null)
                            compras.filter {
                                it.fecha.year == mesMasReciente.year &&
                                it.fecha.monthValue == mesMasReciente.monthValue
                            }
                        else emptyList()
                    }
                }
                val ultima = compras.maxByOrNull { it.fecha.atTime(it.hora) }

                val lunesDeEstaSemana = ahora.with(DayOfWeek.MONDAY)
                val gastosPorDia = (0..6).map { offset ->
                    val dia = lunesDeEstaSemana.plusDays(offset.toLong())
                    compras.filter { it.fecha == dia }.sumOf { it.total }
                }

                _uiState.value = UiState.Success(
                    HomeData(
                        saludo          = nombre,
                        gastoMes        = delMes.sumOf { it.total },
                        cantidadCompras = delMes.size,
                        ultimoSuper     = ultima?.supermercado ?: "-",
                        ultimaFecha     = ultima?.fecha?.format(fmtFecha) ?: "-",
                        ultimoTotal     = ultima?.total ?: 0.0,
                        usuarioNombre   = nombre,
                        usuarioApellido = apellido,
                        usuarioEmail    = email,
                        gastosPorDia    = gastosPorDia
                    )
                )
            }
        }
    }
}
