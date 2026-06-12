package com.undef.superahorro.BossioCorrea.ui.screens.estadisticas

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.superahorro.BossioCorrea.R
import com.undef.superahorro.BossioCorrea.ui.components.LabelCaps
import com.undef.superahorro.BossioCorrea.ui.components.MainBottomBar
import com.undef.superahorro.BossioCorrea.ui.components.StitchTopBar
import com.undef.superahorro.BossioCorrea.ui.navigation.Routes
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import com.undef.superahorro.BossioCorrea.ui.theme.SuperAhorroTheme

// Medallas para el podio de productos
private val MEDALLAS = listOf("🥇", "🥈", "🥉", "4°", "5°")

// Colores para el podio (verde degradado)
private val PODIO_COLORES = listOf(
    Color(0xFF026259),
    Color(0xFF18867C),
    Color(0xFF34ADA2),
    Color(0xFF50C2B7),
    Color(0xFF69D3C8)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadisticasScreen(
    vm          : EstadisticasViewModel = viewModel(),
    onBackClick : () -> Unit = {},
    onComparativaClick : () -> Unit = {},
    currentRoute : String = Routes.ESTADISTICAS,
    onTabClick   : (String) -> Unit = {}
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { vm.cargar() }

    val periodos = listOf(
        stringResource(R.string.estadisticas_periodo_7_dias),
        stringResource(R.string.estadisticas_periodo_30_dias),
        stringResource(R.string.estadisticas_periodo_3_meses),
        stringResource(R.string.estadisticas_periodo_1_ano)
    )
    var periodoSel by remember { mutableStateOf(periodos[1]) }

    Scaffold(
        topBar = { StitchTopBar(stringResource(R.string.estadisticas_titulo), onBackClick) },
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { MainBottomBar(currentRoute = currentRoute, onTabClick = onTabClick) }
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
                            Text(
                                stringResource(R.string.estadisticas_titulo),
                                fontSize = 36.sp, fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                letterSpacing = (-0.72).sp
                            )
                            Text(
                                stringResource(R.string.estadisticas_subtitulo),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
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

                    // ── HERO: Total gastado ────────────────────────────────
                    item {
                        TotalHeroCard(
                            total           = data.totalGastado,
                            cantidadCompras = data.cantidadCompras,
                            promedio        = data.promedio
                        )
                    }

                    // ── Evolución mensual (barras animadas) ────────────────
                    item {
                        EvolucionMensualCard(gastosPorMes = data.gastosPorMes)
                    }

                    // ── Por supermercado ───────────────────────────────────
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(16.dp),
                            color    = MaterialTheme.colorScheme.surfaceContainerLow,
                            shadowElevation = 1.dp,
                            border   = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBBCABF).copy(alpha = 0.3f))
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                LabelCaps(stringResource(R.string.estadisticas_por_supermercado))
                                data.gastosPorSuper.forEach { (nombre, pct) ->
                                    SuperBar(nombre, pct)
                                }
                            }
                        }
                    }

                    // ── Productos más comprados (rediseñado) ───────────────
                    item {
                        TopProductosCard(data.topProductos)
                    }

                    // ── Comparativa de precios ──────────────────────────────
                    item {
                        ComparativaPreciosEntryCard(onClick = onComparativaClick)
                    }

                    item { Spacer(Modifier.height(32.dp)) }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// HERO CARD — Total gastado
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TotalHeroCard(total: Double, cantidadCompras: Int, promedio: Double) {
    // Animación de entrada del número
    var visible by remember { mutableStateOf(false) }
    val animatedTotal by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label         = "total_anim"
    )
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF009688), Color(0xFF00BCD4), Color(0xBE009688)),
                    start  = Offset(0f, 0f),
                    end    = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
            .padding(24.dp)
    ) {
        // Círculos decorativos de fondo
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
                stringResource(R.string.estadisticas_total_gastado).uppercase(),
                style      = MaterialTheme.typography.labelSmall,
                color      = Color.White.copy(alpha = 0.65f),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "$ %,.0f".format(total * animatedTotal),
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
                HeroStat(stringResource(R.string.estadisticas_cantidad_compras).uppercase(), "$cantidadCompras")
                HeroStat(stringResource(R.string.estadisticas_promedio).uppercase(), "$ %,.0f".format(promedio))
                HeroStat(stringResource(R.string.estadisticas_tendencia).uppercase(), "+12%")
            }
        }
    }
}

@Composable
private fun HeroStat(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(0.55f), letterSpacing = 0.8.sp)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// EVOLUCIÓN MENSUAL — barras animadas
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EvolucionMensualCard(gastosPorMes: List<Double>) {
    val meses = stringArrayResource(R.array.estadisticas_meses_cortos).toList()
    val maxGasto = gastosPorMes.maxOrNull() ?: 0.0
    val vals = if (maxGasto > 0) {
        gastosPorMes.map { (it / maxGasto).toFloat().coerceAtLeast(0.03f) }
    } else {
        List(12) { 0.03f }
    }

    // Cada barra tiene su propio animatable para efecto stagger
    val animatedHeights = vals.mapIndexed { idx, target ->
        val anim = remember { Animatable(0f) }
        LaunchedEffect(target) {
            kotlinx.coroutines.delay(idx * 60L)
            anim.animateTo(
                targetValue   = target,
                animationSpec = tween(600, easing = EaseOutBack)
            )
        }
        anim.value
    }

    val primary = MaterialTheme.colorScheme.primary

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        color    = MaterialTheme.colorScheme.surfaceContainerLow,
        shadowElevation = 1.dp,
        border   = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBBCABF).copy(alpha = 0.3f))
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            LabelCaps(stringResource(R.string.estadisticas_evolucion_mensual).uppercase())
            Spacer(Modifier.height(20.dp))

            val barHeight: Dp = 120.dp

            Row(
                modifier              = Modifier.fillMaxWidth().height(barHeight),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment     = Alignment.Bottom
            ) {
                meses.zip(animatedHeights).forEach { (mes, h) ->
                    Column(
                        modifier            = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        // Barra con gradiente animado
                        val barPx = h
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .drawBehind {
                                    val bh = size.height * barPx
                                    val top = size.height - bh
                                    drawRoundRect(
                                        brush       = Brush.verticalGradient(
                                            colors     = listOf(
                                                Color(0xFF1A5932),
                                                Color(0xFF009688)
                                            ),
                                            startY = top,
                                            endY   = size.height
                                        ),
                                        topLeft     = Offset(0f, top),
                                        size        = Size(size.width, bh),
                                        cornerRadius = CornerRadius(6f, 6f)
                                    )
                                }
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            mes,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TOP PRODUCTOS — podio con barras de progreso horizontales y colores
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TopProductosCard(topProductos: List<Pair<String, Int>>) {
    val maxCant = topProductos.maxOfOrNull { it.second }?.toFloat() ?: 1f

    // Animación de entrada de las barras
    val animatedProgress = topProductos.mapIndexed { idx, _ ->
        val anim = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(idx * 80L)
            anim.animateTo(1f, tween(700, easing = EaseOutCubic))
        }
        anim.value
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        color    = MaterialTheme.colorScheme.surfaceContainerLow,
        shadowElevation = 1.dp,
        border   = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBBCABF).copy(alpha = 0.3f))
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            LabelCaps(stringResource(R.string.estadisticas_mas_comprados))
            Spacer(Modifier.height(16.dp))

            topProductos.forEachIndexed { i, (nombre, cant) ->
                val pct       = cant / maxCant
                val color     = PODIO_COLORES.getOrElse(i) { Color(0xFF006C49) }
                val progreso  = animatedProgress.getOrElse(i) { 1f }
                val medalla   = MEDALLAS.getOrElse(i) { "${i+1}°" }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Medalla / posición
                        Box(
                            modifier         = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(color.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text     = medalla,
                                fontSize = if (i < 3) 16.sp else 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color    = color
                            )
                        }

                        // Nombre del producto
                        Text(
                            text      = nombre,
                            modifier  = Modifier.weight(1f),
                            style     = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color     = MaterialTheme.colorScheme.onSurface,
                            maxLines  = 1,
                            overflow  = TextOverflow.Ellipsis
                        )

                        // Cantidad con chip colorido
                        Box(
                            modifier         = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(color.copy(alpha = 0.12f))
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "×$cant",
                                style      = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color      = color
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Barra de progreso animada con color por posición
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(color.copy(alpha = 0.10f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(pct * progreso)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(color, color.copy(alpha = 0.5f))
                                    )
                                )
                        )
                    }
                }

                if (i < topProductos.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 2.dp),
                        color    = MaterialTheme.colorScheme.outlineVariant.copy(0.25f)
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Comparativa de precios — acceso a la pantalla dedicada
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ComparativaPreciosEntryCard(onClick: () -> Unit) {
    Surface(
        modifier        = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape           = RoundedCornerShape(16.dp),
        color           = MaterialTheme.colorScheme.surfaceContainerLow,
        shadowElevation = 1.dp,
        border          = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBBCABF).copy(alpha = 0.3f))
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(
                    modifier = Modifier.size(42.dp).clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(.35f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CompareArrows, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                }
                Column {
                    Text(stringResource(R.string.estadisticas_comparar_precios), fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface)
                    Text(stringResource(R.string.estadisticas_comparar_precios_desc), style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline)
                }
            }
            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Componentes auxiliares sin cambios
// ─────────────────────────────────────────────────────────────────────────────

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
            progress  = { porcentaje },
            modifier  = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color     = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EstadisticasPreview() { SuperAhorroTheme { EstadisticasScreen() } }