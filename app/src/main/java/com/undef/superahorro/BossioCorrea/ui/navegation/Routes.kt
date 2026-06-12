package com.undef.superahorro.BossioCorrea.ui.navigation

// Rutas de navegación centralizadas
object Routes {
    const val SPLASH            = "splash"
    const val LOGIN             = "login"
    const val REGISTER          = "register"
    const val OLVIDO_PASSWORD   = "olvido_password"
    const val HOME              = "home"
    const val NUEVA_COMPRA      = "nueva_compra"
    const val NUEVO_PRODUCTO    = "nuevo_producto"
    const val LISTADO_COMPRAS   = "listado_compras"
    const val DETALLE_COMPRA    = "detalle_compra/{compraId}"
    const val HISTORIAL_COMPRAS = "historial_compras"
    const val ESTADISTICAS      = "estadisticas"
    const val PROMOCIONES       = "promociones"
    const val PERFIL            = "perfil"
    const val SETTINGS          = "settings"
    const val CHAT              = "chat"

    fun detalleCompra(id: String) = "detalle_compra/$id"
}