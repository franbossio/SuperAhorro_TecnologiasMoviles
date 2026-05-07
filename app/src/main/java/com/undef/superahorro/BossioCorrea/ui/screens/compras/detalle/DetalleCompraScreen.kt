package com.undef.superahorro.BossioCorrea.ui.screens.compras.detalle

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
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

    // Diálogo de confirmación de eliminación
    var productoAEliminar by remember { mutableStateOf<Producto?>(null) }

    productoAEliminar?.let { producto ->
        AlertDialog(
            onDismissRequest = { productoAEliminar = null },
            title = {
                Text(
                    stringResource(R.string.eliminar_producto_titulo),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(stringResource(R.string.eliminar_producto_mensaje, producto.nombre))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.eliminarProducto(producto.id)
                        productoAEliminar = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.eliminar_confirmar), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { productoAEliminar = null }) {
                    Text(stringResource(R.string.cancelar))
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        topBar = {
            StitchTopBar(
                title = stringResource(R.string.compra_detalle_titulo),
                onBackClick = onBackClick,
                actions = {
                    IconButton(onClick = {}) {
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
                val fmt    = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                LazyColumn(
                    modifier       = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // ── Hero card con info principal ───────────────────────
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(16.dp),
                            color    = Color(0xFFFFFFFF),
                            shadowElevation = 1.dp,
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBBCABF).copy(alpha = 0.3f))
                            )
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.Top) {
                                    Column {
                                        LabelCaps("Establecimiento")
                                        Spacer(Modifier.height(4.dp))
                                        Text(compra.supermercado,
                                            fontSize = 24.sp, fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            letterSpacing = (-0.24).sp)
                                        Text("${compra.fecha.format(fmt)}  ·  ${compra.hora}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.outline)
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                                    ) {
                                        Text("COMPLETADO",
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            style    = MaterialTheme.typography.labelSmall,
                                            color    = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(Modifier.height(16.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(0.3f))
                                Spacer(Modifier.height(16.dp))
                                LabelCaps("TOTAL ABONADO")
                                Text("$ %,.2f".format(compra.total),
                                    fontSize = 28.sp, fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    letterSpacing = (-0.56).sp)
                            }
                        }
                    }

                    // ── Productos header ──────────────────────────────────
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Productos (${compra.productos.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary)
                            // Hint de swipe para orientar al usuario
                            if (compra.productos.isNotEmpty()) {
                                Text(
                                    stringResource(R.string.swipe_para_eliminar),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }

                    // ── Lista de productos con swipe-to-delete ────────────
                    items(
                        items = compra.productos,
                        key   = { it.id }
                    ) { producto ->
                        ProductoSwipeable(
                            producto   = producto,
                            onEliminar = { productoAEliminar = producto }
                        )
                    }

                    // ── Total resumen ─────────────────────────────────────
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(12.dp),
                            color    = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total", style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold)
                                Text("$ %,.2f".format(compra.total),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

// ── Swipe-to-delete wrapper ───────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductoSwipeable(
    producto   : Producto,
    onEliminar : () -> Unit
) {
    val swipeState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onEliminar()
                // Retornamos false para que la card NO desaparezca
                // hasta que el usuario confirme en el diálogo
                false
            } else false
        },
        positionalThreshold = { it * 0.4f }   // 40 % del ancho para activar
    )

    SwipeToDismissBox(
        state             = swipeState,
        enableDismissFromStartToEnd = false,   // Solo izquierda → derecha queda desactivado
        enableDismissFromEndToStart = true,    // Swipe de derecha a izquierda habilitado
        backgroundContent = { SwipeBackground(swipeState) },
        content           = { ProductoRow(producto) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeBackground(state: SwipeToDismissBoxState) {
    val isActive = state.dismissDirection == SwipeToDismissBoxValue.EndToStart

    val bgColor by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.errorContainer
        else Color(0xFFFFEBEB),
        animationSpec = tween(200),
        label = "swipe_bg"
    )
    val iconColor by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.onErrorContainer
        else MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
        animationSpec = tween(200),
        label = "swipe_icon"
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
                contentDescription = "Eliminar producto",
                tint               = iconColor,
                modifier           = Modifier.size(24.dp)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text  = "Eliminar",
                color = iconColor,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ── Fila de producto (sin cambios visuales respecto al original) ──────────────

@Composable
private fun ProductoRow(p: Producto) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(12.dp),
        color    = Color(0xFFFFFFFF),
        shadowElevation = 1.dp,
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBBCABF).copy(alpha = 0.25f))
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(p.nombre, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text("Cód: ${p.codigo}  ·  x${p.cantidad}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline)
                if (p.descripcion.isNotBlank()) {
                    Text(p.descripcion,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f))
                }
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
            ) {
                Text("$ %,.2f".format(p.subtotal),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style    = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color    = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DetallePreview() { SuperAhorroTheme { DetalleCompraScreen() } }