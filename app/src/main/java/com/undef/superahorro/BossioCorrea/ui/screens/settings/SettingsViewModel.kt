package com.undef.superahorro.BossioCorrea.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.undef.superahorro.BossioCorrea.data.local.SessionManager
import com.undef.superahorro.BossioCorrea.data.repository.CompraRepository
import com.undef.superahorro.BossioCorrea.domain.model.Compra
import kotlinx.coroutines.flow.first

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repo    = CompraRepository()
    private val session = SessionManager(application)

    /** Trae el historial de compras actual del usuario logueado (vacío si no hay sesión). */
    suspend fun obtenerCompras(): List<Compra> {
        val userId = session.userId.first()
        if (userId == SessionManager.NO_SESSION) return emptyList()
        return repo.getComprasFlow(userId).first()
    }
}
