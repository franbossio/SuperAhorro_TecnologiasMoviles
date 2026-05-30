package com.undef.superahorro.BossioCorrea.ui.screens.compras.detalle

import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import java.io.File
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Store
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.superahorro.BossioCorrea.R
import com.undef.superahorro.BossioCorrea.domain.model.Compra
import com.undef.superahorro.BossioCorrea.domain.model.Producto
import com.undef.superahorro.BossioCorrea.ui.components.LabelCaps
import com.undef.superahorro.BossioCorrea.ui.components.StitchTopBar
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import com.undef.superahorro.BossioCorrea.ui.theme.SuperAhorroTheme
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleCompraScreen(
    compraId               : Int = 1,
    vm                     : DetalleCompraViewModel = viewModel(),
    onAgregarProductoClick : () -> Unit = {},
    onBackClick            : () -> Unit = {}
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(compraId) { vm.cargar(compraId) }

    val context = LocalContext.current
    val compra  = (uiState as? UiState.Success)?.data

    var productoAEliminar by remember { mutableStateOf<Producto?>(null) }

    productoAEliminar?.let { producto ->
        AlertDialog(
            onDismissRequest = { productoAEliminar = null },
            title = {
                Text(stringResource(R.string.eliminar_producto_titulo), fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    stringResource(R.string.eliminar_producto_mensaje, producto.nombre),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick  = { vm.eliminarProducto(producto.id); productoAEliminar = null },
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
                    onClick  = { productoAEliminar = null },
                    shape    = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) { Text(stringResource(R.string.cancelar)) }
            },
            shape          = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    }

    Scaffold(
        topBar = {
            StitchTopBar(
                title       = stringResource(R.string.compra_detalle_titulo),
                onBackClick = onBackClick,
                actions     = {
                    IconButton(
                        onClick  = {
                            compra?.let { c ->
                                val fmt   = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                val texto = buildString {
                                    appendLine("🛒 Compra en ${c.supermercado}")
                                    appendLine("📅 ${c.fecha.format(fmt)} · ${c.hora}")
                                    appendLine()
                                    c.productos.forEach { p ->
                                        appendLine("• ${p.nombre}  ×${p.cantidad}  ${"$%,.2f".format(p.subtotal)}")
                                    }
                                    appendLine()
                                    appendLine("Total: ${"$%,.2f".format(c.total)}")
                                    appendLine()
                                    appendLine("Enviado desde Super Ahorro 💚")
                                }
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, texto)
                                }
                                context.startActivity(
                                    Intent.createChooser(intent, "Compartir compra")
                                )
                            }
                        },
                        enabled = compra != null
                    ) {
                        Icon(Icons.Default.Share, null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = onAgregarProductoClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor   = Color.White,
                shape          = RoundedCornerShape(16.dp)
            ) { Icon(Icons.Default.Add, null) }
        },
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
                val compra = state.data
                LazyColumn(
                    modifier            = Modifier.fillMaxSize().padding(padding),
                    contentPadding      = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // ── Hero card — gradiente verde igual que Estadísticas ─
                    item { HeroCard(compra) }

                    // ── Imagen del ticket (si existe) ─────────────────────
                    if (compra.ticketImageUri != null) {
                        item { TicketImageCard(compra.ticketImageUri) }
                    }

                    // ── Encabezado sección productos ──────────────────────
                    item {
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Text(
                                "Productos (${compra.productos.size})",
                                style      = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color      = MaterialTheme.colorScheme.primary
                            )
                            if (compra.productos.isNotEmpty()) {
                                Text(
                                    stringResource(R.string.swipe_para_eliminar),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }

                    // ── Lista de productos con swipe ──────────────────────
                    items(compra.productos, key = { it.id }) { producto ->
                        ProductoSwipeable(
                            producto   = producto,
                            index      = compra.productos.indexOf(producto) + 1,
                            onEliminar = { productoAEliminar = producto }
                        )
                    }

                    // ── Fila de total ─────────────────────────────────────
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(12.dp),
                            color    = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                        ) {
                            Row(
                                modifier              = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Total",
                                    style      = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color      = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    "$ %,.2f".format(compra.total),
                                    style      = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color      = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

// ── Hero card — mismo gradiente que EstadisticasScreen y HistorialComprasScreen

@Composable
private fun HeroCard(compra: Compra) {
    val fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // Animación del total igual que en EstadisticasScreen
    var visible by remember { mutableStateOf(false) }
    val animTotal by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label         = "hero_anim"
    )
    LaunchedEffect(Unit) { visible = true }

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

            // Supermercado + badge COMPLETADO
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "ESTABLECIMIENTO",
                        style         = MaterialTheme.typography.labelSmall,
                        color         = Color.White.copy(alpha = 0.65f),
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        compra.supermercado,
                        fontSize      = 26.sp,
                        fontWeight    = FontWeight.ExtraBold,
                        color         = Color.White,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        "${compra.fecha.format(fmt)}  ·  ${compra.hora}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.65f)
                    )
                }
                // Badge COMPLETADO — semitransparente sobre el gradiente
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.15f)
                ) {
                    Text(
                        "COMPLETADO",
                        modifier      = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style         = MaterialTheme.typography.labelSmall,
                        color         = Color.White,
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
            Spacer(Modifier.height(16.dp))

            // Total animado — igual que EstadisticasScreen
            Text(
                "TOTAL ABONADO",
                style         = MaterialTheme.typography.labelSmall,
                color         = Color.White.copy(alpha = 0.65f),
                fontWeight    = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "$ %,.2f".format(compra.total * animTotal),
                fontSize      = 42.sp,
                fontWeight    = FontWeight.ExtraBold,
                color         = Color.White,
                letterSpacing = (-1.0).sp
            )

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
            Spacer(Modifier.height(16.dp))

            // Stats row — igual que HeroStat en EstadisticasScreen
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                HeroStat("PRODUCTOS",  "${compra.productos.size}")
                HeroStat("PROMEDIO",
                    "$ %,.0f".format(
                        if (compra.productos.isEmpty()) 0.0
                        else compra.total / compra.productos.size
                    )
                )
                HeroStat("IVA EST.",   "$ %,.0f".format(compra.total * 0.21))
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

// ── Swipe-to-delete — mismo patrón que ListadoComprasScreen ──────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductoSwipeable(
    producto   : Producto,
    index      : Int,
    onEliminar : () -> Unit
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
        backgroundContent           = { ProductoSwipeBg(swipeState) },
        content                     = { ProductoRow(producto, index) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductoSwipeBg(state: SwipeToDismissBoxState) {
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
            .clip(RoundedCornerShape(12.dp))
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
                "Eliminar",
                color      = MaterialTheme.colorScheme.error,
                style      = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ── Fila de producto — más llamativa, respetando paleta Stitch ────────────────

@Composable
private fun ProductoRow(p: Producto, index: Int) {
    Surface(
        modifier        = Modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(12.dp),
        color           = MaterialTheme.colorScheme.surfaceContainerLow,
        shadowElevation = 1.dp,
        border          = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(Color(0xFFBBCABF).copy(alpha = 0.3f))
        )
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Número de ítem — círculo con fondo primary/10
            Surface(
                shape  = CircleShape,
                color  = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        "$index",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize   = 14.sp,
                        color      = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Info central
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    p.nombre,
                    style      = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onSurface,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Chip código
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            "# ${p.codigo}",
                            modifier   = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style      = MaterialTheme.typography.labelSmall,
                            color      = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    // Chip cantidad
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                    ) {
                        Text(
                            "×${p.cantidad}",
                            modifier   = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style      = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color      = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                if (p.descripcion.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        p.descripcion,
                        style    = MaterialTheme.typography.bodySmall,
                        color    = MaterialTheme.colorScheme.outline,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Subtotal — chip verde igual que resto de la app
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
            ) {
                Text(
                    "$ %,.2f".format(p.subtotal),
                    modifier   = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    style      = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun TicketImageCard(imagePath: String) {
    var expandido by remember { mutableStateOf(false) }

    Surface(
        modifier        = Modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(16.dp),
        color           = MaterialTheme.colorScheme.surfaceContainerLow,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier            = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                "TICKET",
                style         = MaterialTheme.typography.labelSmall,
                fontWeight    = FontWeight.Bold,
                color         = MaterialTheme.colorScheme.outline,
                letterSpacing = 1.5.sp
            )
            AsyncImage(
                model              = File(imagePath),
                contentDescription = "Ticket de compra",
                modifier           = Modifier
                    .fillMaxWidth()
                    .height(if (expandido) 600.dp else 220.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { expandido = !expandido },
                contentScale       = if (expandido) ContentScale.Fit else ContentScale.Crop
            )
            Text(
                if (expandido) "Tocá para comprimir" else "Tocá para ver completo",
                style    = MaterialTheme.typography.labelSmall,
                color    = MaterialTheme.colorScheme.outline,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DetallePreview() { SuperAhorroTheme { DetalleCompraScreen() } }