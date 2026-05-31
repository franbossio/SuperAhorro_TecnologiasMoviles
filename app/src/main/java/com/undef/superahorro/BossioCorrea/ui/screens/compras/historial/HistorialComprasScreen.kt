package com.undef.superahorro.BossioCorrea.ui.screens.compras.historial

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialComprasScreen(
    vm            : HistorialComprasViewModel = viewModel(),
    onCompraClick : (Int) -> Unit = {},
    onBackClick   : () -> Unit = {}
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val mesSel  by vm.mesSel.collectAsStateWithLifecycle()
    val anioSel by vm.anioSel.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { vm.cargar() }

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
                    stringResource(R.string.historial_eliminar_mensaje, compra.supermercado),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick  = { vm.eliminarCompra(compra.id); compraAEliminar = null },
                    colors   = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape    = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.eliminar_confirmar), fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick  = { compraAEliminar = null },
                    shape    = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) { Text(stringResource(R.string.cancelar)) }
            },
            shape          = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    }

    Scaffold(
        topBar         = { StitchTopBar(stringResource(R.string.compra_historial_titulo), onBackClick) },
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
                    modifier            = Modifier.fillMaxSize().padding(padding),
                    contentPadding      = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // ── Hero card — mismo estilo que EstadisticasScreen ───
                    item { HeroCard(compras) }

                    // ── Selector de año ───────────────────────────────────
                    item {
                        AnioSelector(
                            anioSel     = anioSel,
                            anios       = vm.aniosDisponibles().ifEmpty { listOf(anioSel) },
                            onAnterior  = {
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

                    // ── Chips de meses — igual que filtros de Estadísticas ─
                    item {
                        MesesRow(mesSel = mesSel, onMesSelected = { vm.seleccionarMes(it) })
                    }

                    // ── Vacío ─────────────────────────────────────────────
                    if (compras.isEmpty()) {
                        item {
                            Surface(
                                modifier        = Modifier.fillMaxWidth(),
                                shape           = RoundedCornerShape(16.dp),
                                color           = MaterialTheme.colorScheme.surfaceContainerLow,
                                shadowElevation = 1.dp,
                                border          = CardDefaults.outlinedCardBorder().copy(
                                    brush = SolidColor(Color(0xFFBBCABF).copy(alpha = 0.3f))
                                )
                            ) {
                                Column(
                                    modifier            = Modifier.fillMaxWidth().padding(40.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("🛍", fontSize = 40.sp)
                                    Spacer(Modifier.height(10.dp))
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

                    // ── Lista agrupada por mes ────────────────────────────
                    val agrupado = compras.groupBy {
                        it.fecha.format(
                            DateTimeFormatter.ofPattern("MMMM yyyy", java.util.Locale.getDefault())
                        )
                    }

                    agrupado.entries.forEach { (mes, lista) ->

                        // Encabezado de grupo — nombre del mes + total del mes
                        item {
                            Spacer(Modifier.height(4.dp))
                            Row(
                                modifier              = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                Text(
                                    mes.replaceFirstChar { it.uppercase() },
                                    style      = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color      = MaterialTheme.colorScheme.primary
                                )
                                // Total del mes como chip verde igual que PriceChip
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                                ) {
                                    Text(
                                        "$ %,.0f".format(lista.sumOf { it.total }),
                                        modifier   = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        style      = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color      = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }

                        items(lista, key = { it.id }) { compra ->
                            HistorialSwipeable(
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

// ── Hero card — mismo gradiente verde que EstadisticasScreen ─────────────────

@Composable
private fun HeroCard(compras: List<Compra>) {
    var visible by remember { mutableStateOf(false) }
    val animTotal by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label         = "hero_anim"
    )
    LaunchedEffect(Unit) { visible = true }

    val total   = compras.sumOf { it.total }
    val promedio = if (compras.isEmpty()) 0.0 else total / compras.size

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF003D29), Color(0xFF006C49), Color(0xFF10A870)),
                    start  = Offset(0f, 0f),
                    end    = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
            .padding(24.dp)
    ) {
        // Círculos decorativos — igual que EstadisticasScreen
        Box(
            Modifier
                .size(180.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-40).dp)
                .background(Color.White.copy(alpha = 0.05f), CircleShape)
        )
        Box(
            Modifier
                .size(100.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-30).dp, y = 30.dp)
                .background(Color.White.copy(alpha = 0.04f), CircleShape)
        )

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                stringResource(R.string.compra_historial_titulo).uppercase(),
                style         = MaterialTheme.typography.labelSmall,
                color         = Color.White.copy(alpha = 0.65f),
                fontWeight    = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "$ %,.0f".format(total * animTotal),
                fontSize      = 42.sp,
                fontWeight    = FontWeight.ExtraBold,
                color         = Color.White,
                letterSpacing = (-1.0).sp
            )
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
            Spacer(Modifier.height(12.dp))
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                HeroStat(stringResource(R.string.home_compras).uppercase(),  "${compras.size}")
                HeroStat(stringResource(R.string.home_prom).uppercase(), "$ %,.0f".format(promedio))
            }
        }
    }
}

@Composable
private fun HeroStat(label: String, value: String) {
    Column {
        Text(
            label,
            style         = MaterialTheme.typography.labelSmall,
            color         = Color.White.copy(0.55f),
            letterSpacing = 0.8.sp
        )
        Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
    }
}

// ── Selector de año ───────────────────────────────────────────────────────────

@Composable
private fun AnioSelector(
    anioSel     : Int,
    anios       : List<Int>,
    onAnterior  : () -> Unit,
    onSiguiente : () -> Unit
) {
    val hayAnterior  = anios.indexOf(anioSel) > 0
    val haySiguiente = anios.indexOf(anioSel) < anios.lastIndex

    Surface(
        modifier        = Modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(14.dp),
        color           = MaterialTheme.colorScheme.surfaceContainerLow,
        shadowElevation = 1.dp,
        border          = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(Color(0xFFBBCABF).copy(alpha = 0.3f))
        )
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onAnterior, enabled = hayAnterior) {
                Icon(
                    Icons.Default.ChevronLeft,
                    contentDescription = "Año anterior",
                    tint = if (hayAnterior) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant
                )
            }
            Text(
                "$anioSel",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color      = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = onSiguiente, enabled = haySiguiente) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Año siguiente",
                    tint = if (haySiguiente) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

// ── Chips de meses — mismo estilo FilterChip que EstadisticasScreen ───────────

@Composable
private fun MesesRow(
    mesSel        : Int?,
    onMesSelected : (Int?) -> Unit
) {
    val currentLocale = java.util.Locale.getDefault()
    val meses = remember(currentLocale) {
        (1..12).map { mesNum ->
            java.time.LocalDate.of(2000, mesNum, 1)
                .format(DateTimeFormatter.ofPattern("MMM", currentLocale))
        }
    }
    val rowState = rememberLazyListState()
    LaunchedEffect(mesSel) {
        if (mesSel != null) rowState.animateScrollToItem((mesSel - 1).coerceAtLeast(0))
    }

    LazyRow(
        state                 = rowState,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding        = PaddingValues(horizontal = 2.dp)
    ) {
        item {
            FilterChip(
                selected = mesSel == null,
                onClick  = { onMesSelected(null) },
                label    = { Text(stringResource(R.string.historial_filtro_todos), style = MaterialTheme.typography.labelSmall) },
                shape    = RoundedCornerShape(20.dp),
                colors   = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor     = Color.White
                )
            )
        }
        itemsIndexed(meses) { idx, nombre ->
            val mesNum = idx + 1
            FilterChip(
                selected = mesSel == mesNum,
                onClick  = { onMesSelected(if (mesSel == mesNum) null else mesNum) },
                label    = { Text(nombre, style = MaterialTheme.typography.labelSmall) },
                shape    = RoundedCornerShape(20.dp),
                colors   = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor     = Color.White
                )
            )
        }
    }
}

// ── Swipe-to-delete — igual que ListadoComprasScreen ─────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistorialSwipeable(
    compra     : Compra,
    onEliminar : () -> Unit,
    onClick    : () -> Unit
) {
    val swipeState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) { onEliminar(); false } else false
        },
        positionalThreshold = { it * 0.40f }
    )

    SwipeToDismissBox(
        state                       = swipeState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent           = { SwipeBackground(swipeState) },
        content                     = { HistorialRow(compra = compra, onClick = onClick) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeBackground(state: SwipeToDismissBoxState) {
    val activo = state.dismissDirection == SwipeToDismissBoxValue.EndToStart
    val bg by animateColorAsState(
        targetValue   = if (activo) MaterialTheme.colorScheme.errorContainer
        else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0f),
        animationSpec = tween(200),
        label         = "swipeBg"
    )
    val iconScale by animateFloatAsState(
        targetValue = if (activo) 1.15f else 0.85f,
        label       = "swipeScale"
    )
    Box(
        modifier         = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .padding(end = 20.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier            = Modifier.scale(iconScale)
        ) {
            Icon(
                Icons.Default.Delete, null,
                tint     = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                stringResource(R.string.eliminar_confirmar),
                color      = MaterialTheme.colorScheme.error,
                style      = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ── Card de compra — mismo patrón que CompraCard en ListadoComprasScreen ──────

@Composable
private fun HistorialRow(compra: Compra, onClick: () -> Unit) {
    val fmtFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val fmtDia   = DateTimeFormatter.ofPattern("dd")
    val fmtMes   = DateTimeFormatter.ofPattern("MMM", java.util.Locale.getDefault())

    Surface(
        modifier        = Modifier.fillMaxWidth().clickable { onClick() },
        shape           = RoundedCornerShape(14.dp),
        color           = MaterialTheme.colorScheme.surfaceContainerLow,
        shadowElevation = 1.dp,
        border          = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(Color(0xFFBBCABF).copy(alpha = 0.3f))
        )
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Bloque fecha — destacado con primary al 10%
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
            ) {
                Column(
                    modifier            = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        compra.fecha.format(fmtDia),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize   = 20.sp,
                        color      = MaterialTheme.colorScheme.primary,
                        lineHeight = 20.sp
                    )
                    Text(
                        compra.fecha.format(fmtMes).uppercase(),
                        fontWeight    = FontWeight.Bold,
                        fontSize      = 10.sp,
                        color         = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        letterSpacing = 0.8.sp
                    )
                }
            }

            // Info central
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    compra.supermercado,
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.primary
                )
                Text(
                    "${compra.hora}  ·  ${stringResource(R.string.compra_card_productos, compra.productos.size)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            // Total — mismo chip verde que ProductoRow
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
            ) {
                Text(
                    "$ %,.0f".format(compra.total),
                    modifier   = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HistorialPreview() { SuperAhorroTheme { HistorialComprasScreen() } }