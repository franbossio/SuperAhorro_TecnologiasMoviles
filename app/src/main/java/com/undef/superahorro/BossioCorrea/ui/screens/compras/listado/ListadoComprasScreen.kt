package com.undef.superahorro.BossioCorrea.ui.screens.compras.listado

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
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
import com.undef.superahorro.BossioCorrea.ui.theme.*
import java.time.format.DateTimeFormatter

private val CardBorder = Color(0xFFBBCABF).copy(alpha = 0.35f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListadoComprasScreen(
    vm                 : ListadoComprasViewModel = viewModel(),
    onCompraClick      : (Int) -> Unit = {},
    onNuevaCompraClick : () -> Unit = {},
    onBackClick        : () -> Unit = {}
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    var busqueda        by remember { mutableStateOf("") }
    var filtroSel       by remember { mutableStateOf("Todo") }
    var compraAEliminar by remember { mutableStateOf<Compra?>(null) }

    // ── Animación pulsante del FAB ─────────────────────────────────────────
    val pulse = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by pulse.animateFloat(
        initialValue  = 0.20f,
        targetValue   = 0.40f,
        animationSpec = infiniteRepeatable(tween(1100, easing = EaseInOutSine), RepeatMode.Reverse),
        label         = "pulseAlpha"
    )

    // ── Dialog confirmación eliminar ───────────────────────────────────────
    compraAEliminar?.let { compra ->
        AlertDialog(
            onDismissRequest = { compraAEliminar = null },
            icon = {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Icon(
                        Icons.Outlined.Warning,
                        contentDescription = null,
                        tint     = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(12.dp).size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    "¿Eliminar compra?",
                    fontWeight = FontWeight.Bold,
                    textAlign  = TextAlign.Center
                )
            },
            text = {
                Text(
                    "Se eliminará la compra en ${compra.supermercado} del ${
                        compra.fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    }. Esta acción no se puede deshacer.",
                    color     = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    style     = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick  = { vm.eliminar(compra.id); compraAEliminar = null },
                    colors   = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape    = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Eliminar", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick  = { compraAEliminar = null },
                    shape    = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Cancelar") }
            },
            shape          = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    }

    Scaffold(
        topBar         = { StitchTopBar(stringResource(R.string.compra_listado_titulo), onBackClick) },
        containerColor = StitchBackground,
        floatingActionButton = {
            Box {
                // Glow pulsante
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(StitchPrimary.copy(alpha = pulseAlpha))
                        .blur(14.dp)
                )
                FloatingActionButton(
                    onClick        = onNuevaCompraClick,
                    containerColor = StitchPrimary,
                    contentColor   = Color.White,
                    shape          = RoundedCornerShape(18.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nueva compra")
                }
            }
        }
    ) { padding ->

        when (val state = uiState) {
            is UiState.Loading -> Box(
                Modifier.fillMaxSize().padding(padding), Alignment.Center
            ) { CircularProgressIndicator(color = StitchPrimary) }

            is UiState.Error -> Box(
                Modifier.fillMaxSize().padding(padding), Alignment.Center
            ) { Text(stringResource(R.string.error_generico)) }

            is UiState.Success -> {
                val comprasFiltradas = state.data.filter { c ->
                    busqueda.isBlank() ||
                            c.supermercado.contains(busqueda, ignoreCase = true) ||
                            c.productos.any { p -> p.nombre.contains(busqueda, ignoreCase = true) }
                }

                LazyColumn(
                    modifier            = Modifier.fillMaxSize().padding(padding),
                    contentPadding      = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // ════════════════════════════════════════════════════════
                    //  HERO CARD — mismo estilo que HistorialComprasScreen
                    // ════════════════════════════════════════════════════════
                    item {
                        ListadoHeroCard(compras = comprasFiltradas)
                    }

                    // ════════════════════════════════════════════════════════
                    //  BUSCADOR
                    // ════════════════════════════════════════════════════════
                    item {
                        OutlinedTextField(
                            value         = busqueda,
                            onValueChange = { busqueda = it },
                            modifier      = Modifier.fillMaxWidth(),
                            placeholder   = {
                                Text(
                                    "Buscar por supermercado o producto",
                                    color = StitchOutline
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Search, null, tint = StitchOutline)
                            },
                            shape  = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor    = CardBorder,
                                focusedBorderColor      = StitchPrimary,
                                focusedContainerColor   = MaterialTheme.colorScheme.surfaceContainerLow,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
                            ),
                            singleLine = true
                        )
                    }

                    // ════════════════════════════════════════════════════════
                    //  FILTROS CHIP
                    // ════════════════════════════════════════════════════════
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(listOf("Todo", "Últimos 7 días", "Este mes", "Categorías")) { f ->
                                FilterChip(
                                    selected = f == filtroSel,
                                    onClick  = { filtroSel = f },
                                    label    = {
                                        Text(f, style = MaterialTheme.typography.labelSmall)
                                    },
                                    shape  = RoundedCornerShape(20.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = StitchPrimary,
                                        selectedLabelColor     = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // ════════════════════════════════════════════════════════
                    //  HEADER LISTA + hint swipe
                    // ════════════════════════════════════════════════════════
                    if (comprasFiltradas.isNotEmpty()) {
                        item {
                            Row(
                                modifier              = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                LabelCaps("${comprasFiltradas.size} compras encontradas")
                                Row(
                                    verticalAlignment     = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete, null,
                                        tint     = StitchOutline,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        "Deslizá para eliminar",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = StitchOutline
                                    )
                                }
                            }
                        }
                    }

                    // ════════════════════════════════════════════════════════
                    //  LISTA vacía
                    // ════════════════════════════════════════════════════════
                    if (comprasFiltradas.isEmpty()) {
                        item {
                            Box(
                                Modifier.fillMaxWidth().padding(top = 40.dp),
                                Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🛒", fontSize = 48.sp)
                                    Spacer(Modifier.height(12.dp))
                                    Text(
                                        if (busqueda.isNotBlank())
                                            "Sin resultados para \"$busqueda\""
                                        else stringResource(R.string.compra_sin_compras),
                                        color     = StitchOutline,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        items(comprasFiltradas, key = { it.id }) { compra ->
                            SwipeToDeleteCompraCard(
                                compra   = compra,
                                onClick  = { onCompraClick(compra.id) },
                                onDelete = { compraAEliminar = compra }
                            )
                        }
                    }

                    item { Spacer(Modifier.height(100.dp)) }
                }
            }
        }
    }
}

// ─── Hero Card — idéntica al HeroCard del HistorialComprasScreen ──────────────
@Composable
private fun ListadoHeroCard(compras: List<Compra>) {

    // Animación de entrada del número — igual que Historial
    var visible by remember { mutableStateOf(false) }
    val animTotal by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label         = "hero_anim"
    )
    LaunchedEffect(Unit) { visible = true }

    val total    = compras.sumOf { it.total }
    val promedio = if (compras.isEmpty()) 0.0 else total / compras.size
    val superFav = compras
        .groupBy { it.supermercado }
        .maxByOrNull { it.value.size }
        ?.key ?: "—"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                // Gradiente idéntico al del Historial
                Brush.linearGradient(
                    colors = listOf(Color(0xFF003D29), Color(0xFF006C49), Color(0xFF10A870)),
                    start  = Offset(0f, 0f),
                    end    = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
            .padding(24.dp)
    ) {
        // Círculos decorativos — mismo tamaño y posición que Historial
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

            // Label en caps con letterSpacing — igual al Historial
            Text(
                "MIS COMPRAS",
                style         = MaterialTheme.typography.labelSmall,
                color         = Color.White.copy(alpha = 0.65f),
                fontWeight    = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

            Spacer(Modifier.height(4.dp))

            // Monto animado — 42sp ExtraBold, letterSpacing (-1sp), igual Historial
            Text(
                "$ %,.0f".format(total * animTotal),
                fontSize      = 42.sp,
                fontWeight    = FontWeight.ExtraBold,
                color         = Color.White,
                letterSpacing = (-1.0).sp
            )

            Spacer(Modifier.height(12.dp))

            // Divisor sutil — igual que Historial
            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

            Spacer(Modifier.height(12.dp))

            // Stats en fila — mismo patrón HeroStat del Historial
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                HeroStat(label = "COMPRAS",  value = "${compras.size}")
                HeroStat(label = "PROMEDIO", value = "$ %,.0f".format(promedio))
                HeroStat(label = "FAVORITO", value = superFav.take(8))
            }
        }
    }
}

// ─── Stat helper — idéntico al HeroStat del Historial ────────────────────────
@Composable
private fun HeroStat(label: String, value: String) {
    Column {
        Text(
            label,
            style         = MaterialTheme.typography.labelSmall,
            color         = Color.White.copy(0.55f),
            letterSpacing = 0.8.sp
        )
        Text(
            value,
            fontWeight = FontWeight.Bold,
            fontSize   = 16.sp,
            color      = Color.White
        )
    }
}

// ─── Swipe-to-delete wrapper ──────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteCompraCard(
    compra   : Compra,
    onClick  : () -> Unit,
    onDelete : () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) onDelete()
            false
        },
        positionalThreshold = { it * 0.40f }
    )

    SwipeToDismissBox(
        state                       = dismissState,
        modifier                    = Modifier.fillMaxWidth(),
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent           = { DeleteBackground(dismissState) },
        content                     = { CompraCard(compra = compra, onClick = onClick) }
    )
}

// ─── Fondo rojo al deslizar ───────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteBackground(state: SwipeToDismissBoxState) {
    val progressing = state.dismissDirection == SwipeToDismissBoxValue.EndToStart
    val bgColor by animateColorAsState(
        targetValue   = if (progressing) MaterialTheme.colorScheme.errorContainer
        else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0f),
        animationSpec = tween(200),
        label         = "bg"
    )
    val iconScale by animateFloatAsState(
        targetValue = if (progressing) 1.15f else 0.85f,
        label       = "scale"
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .padding(end = 20.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier            = Modifier.scale(iconScale)
        ) {
            Icon(
                Icons.Default.Delete, "Eliminar",
                tint     = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                "Eliminar",
                color      = MaterialTheme.colorScheme.error,
                style      = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ─── Card de compra — sin cambios de estilo ───────────────────────────────────
@Composable
fun CompraCard(compra: Compra, onClick: () -> Unit = {}) {
    val fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    Surface(
        modifier        = Modifier.fillMaxWidth().clickable { onClick() },
        shape           = RoundedCornerShape(14.dp),
        color           = MaterialTheme.colorScheme.surfaceContainerLow,
        shadowElevation = 1.dp,
        border          = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(CardBorder)
        )
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    compra.supermercado,
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.primary
                )
                Text(
                    compra.fecha.format(fmt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    "${compra.productos.size} producto(s)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
            ) {
                Text(
                    "$ %,.0f".format(compra.total),
                    modifier   = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.primary,
                    style      = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ListadoPreview() {
    SuperAhorroTheme { ListadoComprasScreen() }
}