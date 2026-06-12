package com.undef.superahorro.BossioCorrea.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.undef.superahorro.BossioCorrea.ui.screens.comparativa.ComparativaPreciosScreen
import com.undef.superahorro.BossioCorrea.ui.screens.chat.ChatScreen
import com.undef.superahorro.BossioCorrea.ui.screens.compras.detalle.DetalleCompraScreen
import com.undef.superahorro.BossioCorrea.ui.screens.compras.historial.HistorialComprasScreen
import com.undef.superahorro.BossioCorrea.ui.screens.compras.listado.ListadoComprasScreen
import com.undef.superahorro.BossioCorrea.ui.screens.compras.nueva.NuevaCompraScreen
import com.undef.superahorro.BossioCorrea.ui.screens.estadisticas.EstadisticasScreen
import com.undef.superahorro.BossioCorrea.ui.screens.home.HomeScreen
import com.undef.superahorro.BossioCorrea.ui.screens.login.LoginScreen
import com.undef.superahorro.BossioCorrea.ui.screens.olvidopassword.OlvidoPasswordScreen
import com.undef.superahorro.BossioCorrea.ui.screens.perfil.PerfilScreen
import com.undef.superahorro.BossioCorrea.ui.screens.promociones.PromocionesScreen
import com.undef.superahorro.BossioCorrea.ui.screens.productos.nuevo.NuevoProductoScreen
import com.undef.superahorro.BossioCorrea.ui.screens.register.RegisterScreen
import com.undef.superahorro.BossioCorrea.ui.screens.settings.SettingsScreen
import com.undef.superahorro.BossioCorrea.ui.screens.splash.SplashScreen
import com.undef.superahorro.BossioCorrea.ui.theme.LanguageViewModel
import com.undef.superahorro.BossioCorrea.ui.theme.ThemeViewModel

@Composable
fun NavGraph(themeViewModel: ThemeViewModel, languageViewModel: LanguageViewModel) {
    val navController = rememberNavController()

    // Navega entre las pestañas de la barra inferior evitando duplicar pantallas en el stack
    val goToTab: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(
        navController    = navController,
        startDestination = Routes.SPLASH
    ) {

        // ── Splash ──────────────────────────────────────────────────────────
        composable(Routes.SPLASH) {
            SplashScreen(
                onIngresarClick    = { navController.navigate(Routes.LOGIN) },
                onCrearCuentaClick = { navController.navigate(Routes.REGISTER) }
            )
        }

        // ── Auth ────────────────────────────────────────────────────────────
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginExitoso        = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onRegistrarseClick    = { navController.navigate(Routes.REGISTER) },
                onBackClick           = { navController.popBackStack() },
                onOlvidoPasswordClick = { navController.navigate(Routes.OLVIDO_PASSWORD) }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegistroExitoso = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onLoginClick  = { navController.popBackStack() },
                onBackClick   = { navController.popBackStack() }
            )
        }

        composable(Routes.OLVIDO_PASSWORD) {
            OlvidoPasswordScreen(
                onBackClick   = { navController.popBackStack() },
                onVolverLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.OLVIDO_PASSWORD) { inclusive = true }
                    }
                }
            )
        }

        // ── Home ────────────────────────────────────────────────────────────
        composable(Routes.HOME) {
            HomeScreen(
                onNuevaCompraClick  = { navController.navigate(Routes.NUEVA_COMPRA) },
                onListadoClick      = { navController.navigate(Routes.LISTADO_COMPRAS) },
                onEstadisticasClick = { navController.navigate(Routes.ESTADISTICAS) },
                onPromocionesClick  = { navController.navigate(Routes.PROMOCIONES) },
                onPerfilClick       = { navController.navigate(Routes.PERFIL) },
                onSettingsClick     = { navController.navigate(Routes.SETTINGS) },
                onChatClick         = { navController.navigate(Routes.CHAT) },
                onCerrarSesionClick = {
                    navController.navigate(Routes.SPLASH) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                currentRoute = Routes.HOME,
                onTabClick   = goToTab
            )
        }

        // ── Compras ─────────────────────────────────────────────────────────
        composable(Routes.NUEVA_COMPRA) {
            NuevaCompraScreen(
                onGuardarClick = { navController.popBackStack() },
                onBackClick    = { navController.popBackStack() }
            )
        }

        composable(Routes.LISTADO_COMPRAS) {
            ListadoComprasScreen(
                onCompraClick      = { id -> navController.navigate(Routes.detalleCompra(id)) },
                onNuevaCompraClick = { navController.navigate(Routes.NUEVA_COMPRA) },
                onBackClick        = { navController.popBackStack() },
                currentRoute       = Routes.LISTADO_COMPRAS,
                onTabClick         = goToTab
            )
        }

        composable(
            route     = Routes.DETALLE_COMPRA,
            arguments = listOf(navArgument("compraId") { type = NavType.StringType })
        ) { back ->
            val compraId = back.arguments?.getString("compraId") ?: ""
            DetalleCompraScreen(
                compraId               = compraId,
                onAgregarProductoClick = { navController.navigate(Routes.NUEVO_PRODUCTO) },
                onBackClick            = { navController.popBackStack() }
            )
        }

        composable(Routes.HISTORIAL_COMPRAS) {
            HistorialComprasScreen(
                onCompraClick = { id -> navController.navigate(Routes.detalleCompra(id)) },
                onBackClick   = { navController.popBackStack() },
                currentRoute  = Routes.HISTORIAL_COMPRAS,
                onTabClick    = goToTab
            )
        }

        // ── Productos ────────────────────────────────────────────────────────
        composable(Routes.NUEVO_PRODUCTO) {
            NuevoProductoScreen(
                onGuardarClick = { navController.popBackStack() },
                onBackClick    = { navController.popBackStack() }
            )
        }

        // ── Estadísticas ─────────────────────────────────────────────────────
        composable(Routes.ESTADISTICAS) {
            EstadisticasScreen(
                onBackClick       = { navController.popBackStack() },
                onComparativaClick = { navController.navigate(Routes.COMPARATIVA_PRECIOS) },
                currentRoute      = Routes.ESTADISTICAS,
                onTabClick        = goToTab
            )
        }

        // ── Comparativa de precios ────────────────────────────────────────────
        composable(Routes.COMPARATIVA_PRECIOS) {
            ComparativaPreciosScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // ── Promociones ───────────────────────────────────────────────────────
        composable(Routes.PROMOCIONES) {
            PromocionesScreen(
                onBackClick  = { navController.popBackStack() },
                currentRoute = Routes.PROMOCIONES,
                onTabClick   = goToTab
            )
        }

        // ── Perfil ────────────────────────────────────────────────────────────
        composable(Routes.PERFIL) {
            PerfilScreen(
                onGuardarClick      = { navController.popBackStack() },
                onCerrarSesionClick = {
                    navController.navigate(Routes.SPLASH) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // ── Chat (asistente IA sobre el historial) ────────────────────────────
        composable(Routes.CHAT) {
            ChatScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // ── Settings ──────────────────────────────────────────────────────────
        composable(Routes.SETTINGS) {
            SettingsScreen(
                themeViewModel    = themeViewModel,
                languageViewModel = languageViewModel,
                onBackClick       = { navController.popBackStack() }
            )
        }
    }
}