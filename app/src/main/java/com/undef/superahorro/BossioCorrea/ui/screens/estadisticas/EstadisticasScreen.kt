package com.undef.superahorro.BossioCorrea.ui.screens.estadisticas

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.superahorro.BossioCorrea.R
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import com.undef.superahorro.BossioCorrea.ui.theme.SuperAhorroTheme

// Colores fijos para el gráfico de supermercados
private val barColors = listOf(
    Color(0xFF2E7D32), Color(0xFF4CAF50), Color(0xFF81C784),
    Color(0xFFA5D6A7), Color(0xFFC8E6C9)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadisticasScreen(
    vm          : EstadisticasViewModel = viewModel(),
    onBackClick : () -> Unit = {}
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.estadisticas_titulo)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.volver))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor            = MaterialTheme.colorScheme.primary,
                    titleContentColor         = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->

        when (val state = uiState) {
            is UiState.Loading -> Box(
                modifier         = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            is UiState.Error -> Box(
                modifier         = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { Text(stringResource(R.string.error_generico)) }

            is UiState.Success -> {
                val data = state.data
                val maxMensual = data.gastosMensuales.maxOfOrNull { it.second } ?: 1.0
                val maxSuper   = data.gastosPorSuper.firstOrNull()?.second ?: 1.0

                LazyColumn(
                    modifier            = Modifier.fillMaxSize().padding(padding),
                    contentPadding      = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // ── KPI Cards ─────────────────────────────────────────
                    item {
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            KpiCard(
                                modifier = Modifier.weight(1f),
                                label    = stringResource(R.string.estadisticas_total_gastado),
                                value    = "$ %,.0f".format(data.totalGastado),
                                icon     = Icons.Default.TrendingUp,
                                color    = MaterialTheme.colorScheme.primaryContainer,
                                onColor  = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            KpiCard(
                                modifier = Modifier.weight(1f),
                                label    = stringResource(R.string.estadisticas_cantidad_compras),
                                value    = "${data.cantidadCompras}",
                                icon     = Icons.Default.ShoppingCart,
                                color    = MaterialTheme.colorScheme.secondaryContainer,
                                onColor  = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    item {
                        KpiCard(
                            modifier = Modifier.fillMaxWidth(),
                            label    = stringResource(R.string.estadisticas_promedio),
                            value    = "$ %,.0f por compra".format(data.promedio),
                            icon     = Icons.Default.BarChart,
                            color    = MaterialTheme.colorScheme.tertiaryContainer,
                            onColor  = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }

                    // ── Evolución mensual ─────────────────────────────────
                    item {
                        SectionCard(title = "Evolución del gasto mensual") {
                            Row(
                                modifier              = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment     = Alignment.Bottom
                            ) {
                                data.gastosMensuales.forEach { (mes, valor) ->
                                    val fraccion = if (maxMensual > 0) (valor / maxMensual).toFloat() else 0f
                                    val animated by animateFloatAsState(
                                        targetValue  = fraccion,
                                        animationSpec = tween(800),
                                        label        = "bar_$mes"
                                    )
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Bottom,
                                        modifier            = Modifier.weight(1f)
                                    ) {
                                        if (valor > 0) {
                                            Text(
                                                text  = "$ %,.0f".format(valor),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 8.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Box(
                                            modifier = Modifier
                                                .width(28.dp)
                                                .fillMaxHeight(animated.coerceAtLeast(0.03f))
                                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                                .background(
                                                    if (valor > 0) MaterialTheme.colorScheme.primary
                                                    else MaterialTheme.colorScheme.surfaceVariant
                                                )
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text  = mes,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ── Gasto por supermercado ────────────────────────────
                    item {
                        SectionCard(title = stringResource(R.string.estadisticas_por_supermercado)) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                data.gastosPorSuper.forEachIndexed { idx, (nombre, valor) ->
                                    val fraccion = if (maxSuper > 0) (valor / maxSuper).toFloat() else 0f
                                    val animated by animateFloatAsState(
                                        targetValue  = fraccion,
                                        animationSpec = tween(900),
                                        label        = "super_$nombre"
                                    )
                                    Column {
                                        Row(
                                            modifier              = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment     = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(10.dp)
                                                        .clip(CircleShape)
                                                        .background(barColors[idx % barColors.size])
                                                )
                                                Text(nombre, style = MaterialTheme.typography.bodyMedium)
                                            }
                                            Text(
                                                text  = "$ %,.0f".format(valor),
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        LinearProgressIndicator(
                                            progress      = { animated },
                                            modifier      = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp)),
                                            color         = barColors[idx % barColors.size],
                                            trackColor    = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ── Productos más comprados ───────────────────────────
                    item {
                        SectionCard(title = stringResource(R.string.estadisticas_mas_comprados)) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                data.productosMasComprados.forEachIndexed { idx, (nombre, cant) ->
                                    Row(
                                        modifier              = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment     = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment     = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Box(
                                                modifier         = Modifier
                                                    .size(28.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text       = "${idx + 1}",
                                                    style      = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color      = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            }
                                            Text(nombre, style = MaterialTheme.typography.bodyMedium)
                                        }
                                        Card(
                                            shape  = RoundedCornerShape(8.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                                contentColor   = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        ) {
                                            Text(
                                                text     = "x$cant",
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                style    = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun KpiCard(
    modifier : Modifier = Modifier,
    label    : String,
    value    : String,
    icon     : androidx.compose.ui.graphics.vector.ImageVector,
    color    : Color,
    onColor  : Color
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = color, contentColor = onColor),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier            = Modifier.padding(16.dp),
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(28.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelMedium)
                Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
private fun SectionCard(
    title   : String,
    content : @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor   = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text       = title,
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EstadisticasPreview() {
    SuperAhorroTheme { EstadisticasScreen() }
}
