package com.undef.superahorro.BossioCorrea.ui.screens.compras.detalle

import android.content.Intent
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.superahorro.BossioCorrea.R
import com.undef.superahorro.BossioCorrea.domain.model.Producto
import com.undef.superahorro.BossioCorrea.ui.components.StitchTopBar
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import com.undef.superahorro.BossioCorrea.ui.theme.SuperAhorroTheme
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleCompraScreen(
    compraId               : String = "",
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
            title = { Text(stringResource(R.string.eliminar_producto_titulo), fontWeight = FontWeight.Bold) },
            text  = {
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
                        onClick = {
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
                                context.startActivity(Intent.createChooser(intent, "Compartir compra"))
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
                val c = state.data
                LazyColumn(
                    modifier            = Modifier.fillMaxSize().padding(padding),
                    contentPadding      = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { HeroCard(c) }

                    if (c.ticketImageUri != null) {
                        item { TicketImageCard(c.ticketImageUri) }
                    }

                    item {
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Text(
                                "Productos (${c.productos.size})",
                                style      = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color      = MaterialTheme.colorScheme.primary
                            )
                            if (c.productos.isNotEmpty()) {
                                Text(
                                    stringResource(R.string.swipe_para_eliminar),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }

                    items(c.productos, key = { it.id }) { producto ->
                        ProductoSwipeable(
                            producto   = producto,
                            index      = c.productos.indexOf(producto) + 1,
                            onEliminar = { productoAEliminar = producto }
                        )
                    }

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
                                    "$ %,.2f".format(c.total),
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DetallePreview() { SuperAhorroTheme { DetalleCompraScreen() } }
