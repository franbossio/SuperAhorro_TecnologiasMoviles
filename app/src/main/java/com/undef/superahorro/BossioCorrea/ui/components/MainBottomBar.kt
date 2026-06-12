package com.undef.superahorro.BossioCorrea.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.undef.superahorro.BossioCorrea.R
import com.undef.superahorro.BossioCorrea.ui.navigation.Routes

private data class BottomNavItem(val route: String, val icon: ImageVector, val labelRes: Int)

private val BOTTOM_NAV_ITEMS = listOf(
    BottomNavItem(Routes.HOME, Icons.Default.Home, R.string.nav_inicio),
    BottomNavItem(Routes.LISTADO_COMPRAS, Icons.AutoMirrored.Filled.ListAlt, R.string.home_mis_compras),
    BottomNavItem(Routes.HISTORIAL_COMPRAS, Icons.Default.CalendarMonth, R.string.home_historial),
    BottomNavItem(Routes.ESTADISTICAS, Icons.Default.BarChart, R.string.home_estadisticas),
    BottomNavItem(Routes.PROMOCIONES, Icons.Default.LocalOffer, R.string.home_promociones)
)

@Composable
fun MainBottomBar(
    currentRoute : String,
    onTabClick   : (String) -> Unit
) {
    Surface(
        shape           = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        shadowElevation = 12.dp,
        color           = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            BOTTOM_NAV_ITEMS.forEach { item ->
                val selected = currentRoute == item.route
                NavigationBarItem(
                    selected = selected,
                    onClick  = { if (!selected) onTabClick(item.route) },
                    icon = {
                        Icon(
                            imageVector        = item.icon,
                            contentDescription = null,
                            modifier           = Modifier.size(if (selected) 24.dp else 22.dp)
                        )
                    },
                    label = {
                        Text(
                            text       = stringResource(item.labelRes),
                            fontSize   = 10.sp,
                            lineHeight = 12.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            maxLines   = 1,
                            softWrap   = false,
                            overflow   = TextOverflow.Ellipsis
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor   = MaterialTheme.colorScheme.primary,
                        selectedTextColor   = MaterialTheme.colorScheme.primary,
                        indicatorColor      = MaterialTheme.colorScheme.primaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.outline,
                        unselectedTextColor = MaterialTheme.colorScheme.outline
                    )
                )
            }
        }
    }
}
