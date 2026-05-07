package com.undef.superahorro.BossioCorrea.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.undef.superahorro.BossioCorrea.ui.screens.compras.detalle.DetalleCompraScreen
import com.undef.superahorro.BossioCorrea.ui.screens.compras.historial.HistorialComprasScreen
import com.undef.superahorro.BossioCorrea.ui.screens.compras.listado.ListadoComprasScreen
import com.undef.superahorro.BossioCorrea.ui.screens.compras.nueva.NuevaCompraScreen
import com.undef.superahorro.BossioCorrea.ui.screens.estadisticas.EstadisticasScreen
import com.undef.superahorro.BossioCorrea.ui.screens.home.HomeScreen
import com.undef.superahorro.BossioCorrea.ui.screens.login.LoginScreen
import com.undef.superahorro.BossioCorrea.ui.screens.perfil.PerfilScreen
import com.undef.superahorro.BossioCorrea.ui.screens.productos.nuevo.NuevoProductoScreen
import com.undef.superahorro.BossioCorrea.ui.screens.register.RegisterScreen
import com.undef.superahorro.BossioCorrea.ui.screens.settings.SettingsScreen
import com.undef.superahorro.BossioCorrea.ui.screens.splash.SplashScreen
import com.undef.superahorro.BossioCorrea.ui.theme.ThemeViewModel

@Composable
fun NavGraph(themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()

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
                onLoginExitoso     = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onRegistrarseClick = { navController.navigate(Routes.REGISTER) },
                onBackClick        = { navController.popBackStack() }
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

        // ── Home ────────────────────────────────────────────────────────────
        composable(Routes.HOME) {
            HomeScreen(
                onNuevaCompraClick  = { navController.navigate(Routes.NUEVA_COMPRA) },
                onListadoClick      = { navController.navigate(Routes.LISTADO_COMPRAS) },
                onHistorialClick    = { navController.navigate(Routes.HISTORIAL_COMPRAS) },
                onEstadisticasClick = { navController.navigate(Routes.ESTADISTICAS) },
                onPerfilClick       = { navController.navigate(Routes.PERFIL) },
                onSettingsClick     = { navController.navigate(Routes.SETTINGS) }
            )
        }

        // ── Compras ─────────────────────────────────────────────────────────
        composable(Routes.NUEVA_COMPRA) {
            NuevaCompraScreen(
                onGuardarClick         = { navController.popBackStack() },
                onAgregarProductoClick = { navController.navigate(Routes.NUEVO_PRODUCTO) },
                onBackClick            = { navController.popBackStack() }
            )
        }

        composable(Routes.LISTADO_COMPRAS) {
            ListadoComprasScreen(
                onCompraClick      = { id -> navController.navigate(Routes.detalleCompra(id)) },
                onNuevaCompraClick = { navController.navigate(Routes.NUEVA_COMPRA) },
                onBackClick        = { navController.popBackStack() }
            )
        }

        composable(
            route     = Routes.DETALLE_COMPRA,
            arguments = listOf(navArgument("compraId") { type = NavType.IntType })
        ) { back ->
            val compraId = back.arguments?.getInt("compraId") ?: 1
            DetalleCompraScreen(
                compraId               = compraId,
                onAgregarProductoClick = { navController.navigate(Routes.NUEVO_PRODUCTO) },
                onBackClick            = { navController.popBackStack() }
            )
        }

        composable(Routes.HISTORIAL_COMPRAS) {
            HistorialComprasScreen(
                onCompraClick = { id -> navController.navigate(Routes.detalleCompra(id)) },
                onBackClick   = { navController.popBackStack() }
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
                onBackClick = { navController.popBackStack() }
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

        // ── Settings ──────────────────────────────────────────────────────────
        composable(Routes.SETTINGS) {
            SettingsScreen(
                themeViewModel = themeViewModel,
                onBackClick    = { navController.popBackStack() }
            )
        }
    }
}