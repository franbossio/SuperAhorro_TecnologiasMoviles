package com.undef.superahorro.BossioCorrea.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorro.BossioCorrea.data.local.AppDatabase
import com.undef.superahorro.BossioCorrea.data.local.SessionManager
import com.undef.superahorro.BossioCorrea.data.repository.CompraRepository
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

    private val repo    = CompraRepository(AppDatabase.getInstance(application))
    private val session = SessionManager(application)
    private val db      = AppDatabase.getInstance(application)

    private val _uiState = MutableStateFlow<UiState<HomeData>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var observarJob: Job? = null

    init { cargar() }

    fun cargar() {
        observarJob?.cancel()
        observarJob = viewModelScope.launch {
            val userId = session.userId.first()
            if (userId == SessionManager.NO_SESSION) {
                _uiState.value = UiState.Error("Sesión expirada")
                return@launch
            }
            val fmtFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")

            repo.getComprasFlow(userId).collect { compras ->
                val usuario  = db.usuarioDao().getById(userId)
                val nombre   = usuario?.nombre   ?: ""
                val apellido = usuario?.apellido ?: ""
                val email    = usuario?.email    ?: ""

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
