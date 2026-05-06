package com.undef.superahorro.BossioCorrea.ui.screens.compras.historial

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.superahorro.BossioCorrea.R
import com.undef.superahorro.BossioCorrea.domain.model.Compra
import com.undef.superahorro.BossioCorrea.ui.components.LabelCaps
import com.undef.superahorro.BossioCorrea.ui.components.StitchTopBar
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import com.undef.superahorro.BossioCorrea.ui.theme.SuperAhorroTheme
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialComprasScreen(
    vm            : HistorialComprasViewModel = viewModel(),
    onCompraClick : (Int) -> Unit = {},
    onBackClick   : () -> Unit = {}
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val periodos = listOf("Todos", "Ene", "Feb", "Mar", "Abr", "May", "Jun",
                          "Jul", "Ago", "Sep", "Oct", "Nov", "Dic")
    var periodoSel by remember { mutableStateOf("Todos") }

    Scaffold(
        topBar = { StitchTopBar(stringResource(R.string.compra_historial_titulo), onBackClick) },
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
                val compras = state.data
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // ── Header ─────────────────────────────────────────────
                    item {
                        Column {
                            Text("Historial", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                letterSpacing = (-0.72).sp)
                            Text("Todos tus registros de compras",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline)
                        }
                    }

                    // ── Filtro meses ───────────────────────────────────────
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

                    // ── Summary ────────────────────────────────────────────
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatMiniCard(
                                modifier = Modifier.weight(1f),
                                label    = "TOTAL ACUMULADO",
                                value    = "$ %,.0f".format(compras.sumOf { it.total })
                            )
                            StatMiniCard(
                                modifier = Modifier.weight(1f),
                                label    = "COMPRAS",
                                value    = "${compras.size}"
                            )
                            StatMiniCard(
                                modifier = Modifier.weight(1f),
                                label    = "PROMEDIO",
                                value    = "$ %,.0f".format(
                                    if (compras.isEmpty()) 0.0 else compras.sumOf { it.total } / compras.size
                                )
                            )
                        }
                    }

                    // ── Lista agrupada ─────────────────────────────────────
                    val agrupado = compras.groupBy {
                        it.fecha.format(DateTimeFormatter.ofPattern("MMMM yyyy",
                            java.util.Locale("es", "AR")))
                    }
                    agrupado.forEach { (mes, lista) ->
                        item {
                            Spacer(Modifier.height(4.dp))
                            Text(mes.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(8.dp))
                        }
                        items(lista) { c -> HistorialRow(c) { onCompraClick(c.id) } }
                    }

                    item { Spacer(Modifier.height(32.dp)) }
                }
            }
        }
    }
}

@Composable
private fun StatMiniCard(modifier: Modifier, label: String, value: String) {
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
            Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun HistorialRow(compra: Compra, onClick: () -> Unit) {
    val fmt = DateTimeFormatter.ofPattern("dd MMM", java.util.Locale("es", "AR"))
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape    = RoundedCornerShape(12.dp),
        color    = Color(0xFFFFFFFF),
        shadowElevation = 1.dp,
        border   = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBBCABF).copy(alpha = 0.25f))
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Fecha chip
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                ) {
                    Text(compra.fecha.format(fmt).uppercase(),
                        modifier   = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        style      = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.primary)
                }
                Column {
                    Text(compra.supermercado, style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color      = MaterialTheme.colorScheme.onSurface)
                    Text("${compra.productos.size} ${compra.hora}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline)
                }
            }
            Text("$ %,.0f".format(compra.total),
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.primary,
                style      = MaterialTheme.typography.titleMedium)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HistorialPreview() { SuperAhorroTheme { HistorialComprasScreen() } }
