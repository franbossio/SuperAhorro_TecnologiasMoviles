package com.undef.superahorro.BossioCorrea.ui.screens.estadisticas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.superahorro.BossioCorrea.R
import com.undef.superahorro.BossioCorrea.ui.components.LabelCaps
import com.undef.superahorro.BossioCorrea.ui.components.StitchTopBar
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import com.undef.superahorro.BossioCorrea.ui.theme.SuperAhorroTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadisticasScreen(
    vm          : EstadisticasViewModel = viewModel(),
    onBackClick : () -> Unit = {}
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val periodos = listOf("7 días", "30 días", "3 meses", "1 año")
    var periodoSel by remember { mutableStateOf("30 días") }

    Scaffold(
        topBar = { StitchTopBar(stringResource(R.string.estadisticas_titulo), onBackClick) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        when (val state = uiState) {
            is UiState.Loading -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            is UiState.Error -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                Text(stringResource(R.string.error_generico))
            }
            is UiState.Success -> {
                val data = state.data
                LazyColumn(
                    modifier       = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // ── Header ─────────────────────────────────────────────
                    item {
                        Column {
                            Text(stringResource(R.string.estadisticas_titulo),
                                fontSize = 36.sp, fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                letterSpacing = (-0.72).sp)
                            Text("Análisis inteligente de tus gastos",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline)
                        }
                    }

                    // ── Filtros período ────────────────────────────────────
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(periodos) { p ->
                                FilterChip(
                                    selected = p == periodoSel,
                                    onClick  = { periodoSel = p },
                                    label    = { Text(p, style = MaterialTheme.typography.labelSmall) },
                                    shape    = RoundedCornerShape(20.dp),
                                    colors   = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor     = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // ── KPI row: 3 mini cards ─────────────────────────────
                    item {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            KpiCard(Modifier.weight(1f),
                                label = stringResource(R.string.estadisticas_total_gastado),
                                value = "$ %,.0f".format(data.totalGastado),
                                trend = "+12%", positive = false)
                            KpiCard(Modifier.weight(1f),
                                label = stringResource(R.string.estadisticas_cantidad_compras),
                                value = "${data.cantidadCompras}",
                                trend = "+3", positive = true)
                            KpiCard(Modifier.weight(1f),
                                label = stringResource(R.string.estadisticas_promedio),
                                value = "$ %,.0f".format(data.promedio),
                                trend = "-5%", positive = true)
                        }
                    }

                    // ── Evolución mensual (barras) ─────────────────────────
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(16.dp),
                            color    = Color(0xFFFFFFFF),
                            shadowElevation = 1.dp,
                            border   = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBBCABF).copy(alpha = 0.3f))
                            )
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                LabelCaps("EVOLUCIÓN MENSUAL")
                                Spacer(Modifier.height(16.dp))
                                val meses = listOf("E","F","M","A","M","J","J","A","S","O","N","D")
                                val vals  = listOf(0.4f,0.55f,0.45f,0.7f,0.6f,0.8f,0.5f,0.65f,0.9f,0.75f,0.85f,1f)
                                Row(
                                    modifier = Modifier.fillMaxWidth().height(120.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    meses.zip(vals).forEach { (m, h) ->
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Bottom
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .fillMaxHeight(h)
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                                    .background(
                                                        Brush.verticalGradient(
                                                            listOf(
                                                                MaterialTheme.colorScheme.primary,
                                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                                            )
                                                        )
                                                    )
                                            )
                                            Spacer(Modifier.height(4.dp))
                                            Text(m, style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.outline)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ── Por supermercado ───────────────────────────────────
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(16.dp),
                            color    = Color(0xFFFFFFFF),
                            shadowElevation = 1.dp,
                            border   = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBBCABF).copy(alpha = 0.3f))
                            )
                        ) {
                            Column(modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                LabelCaps(stringResource(R.string.estadisticas_por_supermercado))
                                data.gastosPorSuper.forEach { (nombre, pct) ->
                                    SuperBar(nombre, pct)
                                }
                            }
                        }
                    }

                    // ── Productos más comprados ────────────────────────────
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(16.dp),
                            color    = Color(0xFFFFFFFF),
                            shadowElevation = 1.dp,
                            border   = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBBCABF).copy(alpha = 0.3f))
                            )
                        ) {
                            Column(modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                LabelCaps(stringResource(R.string.estadisticas_mas_comprados))
                                data.topProductos.forEachIndexed { i, (prod, cant) ->
                                    Row(Modifier.fillMaxWidth(),
                                        Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                        Row(verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Surface(
                                                modifier = Modifier.size(28.dp),
                                                shape    = RoundedCornerShape(8.dp),
                                                color    = MaterialTheme.colorScheme.primary.copy(0.08f)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text("${i+1}", style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary)
                                                }
                                            }
                                            Text(prod, style = MaterialTheme.typography.bodyLarge)
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(20.dp),
                                            color = MaterialTheme.colorScheme.primary.copy(0.08f)
                                        ) {
                                            Text("x$cant",
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item { Spacer(Modifier.height(32.dp)) }
                }
            }
        }
    }
}

@Composable
private fun KpiCard(modifier: Modifier, label: String, value: String, trend: String, positive: Boolean) {
    Surface(
        modifier = modifier,
        shape    = RoundedCornerShape(12.dp),
        color    = Color(0xFFFFFFFF),
        shadowElevation = 1.dp,
        border   = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBBCABF).copy(alpha = 0.3f))
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            LabelCaps(label)
            Spacer(Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface, letterSpacing = (-0.36).sp)
            Spacer(Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (positive) Color(0xFF10B981).copy(0.1f) else Color(0xFFEF4444).copy(0.1f)
            ) {
                Text(trend,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style    = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color    = if (positive) Color(0xFF10B981) else Color(0xFFEF4444))
            }
        }
    }
}

@Composable
private fun SuperBar(nombre: String, porcentaje: Float) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text(nombre, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text("${(porcentaje * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(
            progress = { porcentaje },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color    = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EstadisticasPreview() { SuperAhorroTheme { EstadisticasScreen() } }
