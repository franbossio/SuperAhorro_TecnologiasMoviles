package com.undef.superahorro.BossioCorrea.ui.screens.compras.historial

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

// Nombres cortos de los meses en español
private val MESES = listOf(
    "Ene", "Feb", "Mar", "Abr", "May", "Jun",
    "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialComprasScreen(
    vm            : HistorialComprasViewModel = viewModel(),
    onCompraClick : (Int) -> Unit = {},
    onBackClick   : () -> Unit = {}
) {
    val uiState  by vm.uiState.collectAsStateWithLifecycle()
    val mesSel   by vm.mesSel.collectAsStateWithLifecycle()
    val anioSel  by vm.anioSel.collectAsStateWithLifecycle()

    // Diálogo de confirmación de eliminación
    var compraAEliminar by remember { mutableStateOf<Compra?>(null) }

    compraAEliminar?.let { compra ->
        AlertDialog(
            onDismissRequest = { compraAEliminar = null },
            title = {
                Text(
                    stringResource(R.string.historial_eliminar_titulo),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    stringResource(R.string.historial_eliminar_mensaje, compra.supermercado)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.eliminarCompra(compra.id)
                        compraAEliminar = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.eliminar_confirmar), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { compraAEliminar = null }) {
                    Text(stringResource(R.string.cancelar), color = MaterialTheme.colorScheme.outline)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

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
                    modifier       = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // ── Header ──────────────────────────────────────────────
                    item {
                        Column {
                            Text(
                                "Historial",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                letterSpacing = (-0.72).sp
                            )
                            Text(
                                "Todos tus registros de compras",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    // ── Selector de año ─────────────────────────────────────
                    item {
                        AnioSelector(
                            anioSel    = anioSel,
                            anios      = vm.aniosDisponibles().ifEmpty { listOf(anioSel) },
                            onAnterior = {
                                val anios = vm.aniosDisponibles()
                                val idx   = anios.indexOf(anioSel)
                                if (idx > 0) vm.seleccionarAnio(anios[idx - 1])
                            },
                            onSiguiente = {
                                val anios = vm.aniosDisponibles()
                                val idx   = anios.indexOf(anioSel)
                                if (idx < anios.lastIndex) vm.seleccionarAnio(anios[idx + 1])
                            }
                        )
                    }

                    // ── Chips de meses ──────────────────────────────────────
                    item {
                        MesesRow(
                            mesSel        = mesSel,
                            onMesSelected = { vm.seleccionarMes(it) }
                        )
                    }

                    // ── Summary cards ───────────────────────────────────────
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatMiniCard(
                                modifier = Modifier.weight(1f),
                                label    = "ACUMULADO",
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
                                    if (compras.isEmpty()) 0.0
                                    else compras.sumOf { it.total } / compras.size
                                )
                            )
                        }
                    }

                    // ── Lista vacía ─────────────────────────────────────────
                    if (compras.isEmpty()) {
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape    = RoundedCornerShape(16.dp),
                                color    = MaterialTheme.colorScheme.surfaceVariant.copy(0.4f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "😶",
                                        fontSize  = 36.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        stringResource(R.string.historial_sin_compras),
                                        style     = MaterialTheme.typography.bodyMedium,
                                        color     = MaterialTheme.colorScheme.outline,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    // ── Lista agrupada por mes con swipe-to-delete ──────────
                    val agrupado = compras.groupBy {
                        it.fecha.format(
                            DateTimeFormatter.ofPattern("MMMM yyyy", java.util.Locale("es", "AR"))
                        )
                    }
                    agrupado.forEach { (mes, lista) ->
                        item {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                mes.replaceFirstChar { it.uppercase() },
                                style      = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color      = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                        items(lista, key = { it.id }) { compra ->
                            CompraSwipeable(
                                compra     = compra,
                                onEliminar = { compraAEliminar = compra },
                                onClick    = { onCompraClick(compra.id) }
                            )
                        }
                    }

                    item { Spacer(Modifier.height(32.dp)) }
                }
            }
        }
    }
}

// ── Selector de año con flechas ──────────────────────────────────────────────

@Composable
private fun AnioSelector(
    anioSel    : Int,
    anios      : List<Int>,
    onAnterior : () -> Unit,
    onSiguiente: () -> Unit
) {
    val hayAnterior  = anios.indexOf(anioSel) > 0
    val haySiguiente = anios.indexOf(anioSel) < anios.lastIndex

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        color    = MaterialTheme.colorScheme.primary.copy(alpha = 0.07f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onAnterior,
                enabled = hayAnterior
            ) {
                Icon(
                    Icons.Default.ChevronLeft,
                    contentDescription = "Año anterior",
                    tint = if (hayAnterior) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline.copy(0.3f)
                )
            }

            Text(
                text       = "$anioSel",
                fontSize   = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = MaterialTheme.colorScheme.primary,
                letterSpacing = (-0.4).sp
            )

            IconButton(
                onClick = onSiguiente,
                enabled = haySiguiente
            ) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Año siguiente",
                    tint = if (haySiguiente) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline.copy(0.3f)
                )
            }
        }
    }
}

// ── Chips de meses ────────────────────────────────────────────────────────────

@Composable
private fun MesesRow(
    mesSel        : Int?,
    onMesSelected : (Int?) -> Unit
) {
    val rowState = rememberLazyListState()

    // Auto-scroll al chip activo al montarse
    LaunchedEffect(mesSel) {
        if (mesSel != null) rowState.animateScrollToItem((mesSel - 1).coerceAtLeast(0))
    }

    LazyRow(
        state               = rowState,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding      = PaddingValues(horizontal = 2.dp)
    ) {
        // Chip "Todos"
        item {
            FilterChip(
                selected = mesSel == null,
                onClick  = { onMesSelected(null) },
                label    = {
                    Text(
                        "Todos",
                        style      = MaterialTheme.typography.labelMedium,
                        fontWeight = if (mesSel == null) FontWeight.Bold else FontWeight.Normal
                    )
                },
                shape  = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor     = Color.White
                )
            )
        }

        // Chips de cada mes (1-12)
        itemsIndexed(MESES) { idx, nombre ->
            val mesNum = idx + 1
            FilterChip(
                selected = mesSel == mesNum,
                onClick  = {
                    onMesSelected(if (mesSel == mesNum) null else mesNum)
                },
                label    = {
                    Text(
                        nombre,
                        style      = MaterialTheme.typography.labelMedium,
                        fontWeight = if (mesSel == mesNum) FontWeight.Bold else FontWeight.Normal
                    )
                },
                shape  = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor     = Color.White
                )
            )
        }
    }
}

// ── Swipe-to-delete wrapper ───────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompraSwipeable(
    compra     : Compra,
    onEliminar : () -> Unit,
    onClick    : () -> Unit
) {
    val swipeState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onEliminar()
                false   // No desaparece hasta que el usuario confirme en el diálogo
            } else false
        },
        positionalThreshold = { it * 0.38f }
    )

    SwipeToDismissBox(
        state                       = swipeState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent           = { SwipeBackground(swipeState) },
        content                     = { HistorialRow(compra, onClick) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeBackground(state: SwipeToDismissBoxState) {
    val isActive = state.dismissDirection == SwipeToDismissBoxValue.EndToStart

    val bgColor by animateColorAsState(
        targetValue   = if (isActive) MaterialTheme.colorScheme.errorContainer else Color(0xFFFFEBEB),
        animationSpec = tween(200),
        label         = "swipe_bg"
    )
    val iconColor by animateColorAsState(
        targetValue   = if (isActive) MaterialTheme.colorScheme.onErrorContainer
        else MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
        animationSpec = tween(200),
        label         = "swipe_icon"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(end = 20.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector        = Icons.Default.Delete,
                contentDescription = "Eliminar compra",
                tint               = iconColor,
                modifier           = Modifier.size(24.dp)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                "Eliminar",
                color      = iconColor,
                style      = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ── Stat card ─────────────────────────────────────────────────────────────────

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

// ── Fila de compra ────────────────────────────────────────────────────────────

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
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment      = Alignment.CenterVertically,
                horizontalArrangement  = Arrangement.spacedBy(12.dp)
            ) {
                // Chip fecha
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                ) {
                    Text(
                        compra.fecha.format(fmt).uppercase(),
                        modifier   = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        style      = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.primary
                    )
                }
                Column {
                    Text(
                        compra.supermercado,
                        style      = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color      = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "${compra.productos.size} producto(s)  ·  ${compra.hora}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            Text(
                "$ %,.0f".format(compra.total),
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.primary,
                style      = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HistorialPreview() { SuperAhorroTheme { HistorialComprasScreen() } }