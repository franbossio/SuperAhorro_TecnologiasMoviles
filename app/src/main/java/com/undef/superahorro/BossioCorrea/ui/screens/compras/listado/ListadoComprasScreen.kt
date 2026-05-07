package com.undef.superahorro.BossioCorrea.ui.screens.compras.listado

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListadoComprasScreen(
    vm                : ListadoComprasViewModel = viewModel(),
    onCompraClick     : (Int) -> Unit = {},
    onNuevaCompraClick: () -> Unit = {},
    onBackClick       : () -> Unit = {}
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    var busqueda by remember { mutableStateOf("") }
    var filtroSel by remember { mutableStateOf("Todo") }

    // Compra pendiente de confirmación para eliminar
    var compraAEliminar by remember { mutableStateOf<Compra?>(null) }

    // ── Dialog de confirmación ────────────────────────────────────────────────
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
                    onClick = {
                        vm.eliminar(compra.id)
                        compraAEliminar = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Eliminar", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { compraAEliminar = null },
                    shape   = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar")
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color(0xFFFFFFFF)
        )
    }

    Scaffold(
        topBar = { StitchTopBar(stringResource(R.string.compra_listado_titulo), onBackClick) },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = onNuevaCompraClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor   = Color.White,
                shape          = RoundedCornerShape(16.dp)
            ) { Icon(Icons.Default.Add, null) }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        when (val state = uiState) {
            is UiState.Loading -> Box(
                Modifier.fillMaxSize().padding(padding),
                Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }

            is UiState.Error -> Box(
                Modifier.fillMaxSize().padding(padding),
                Alignment.Center
            ) {
                Text(stringResource(R.string.error_generico))
            }

            is UiState.Success -> {
                // Filtrar por búsqueda
                val comprasFiltradas = state.data.filter { c ->
                    busqueda.isBlank() ||
                    c.supermercado.contains(busqueda, ignoreCase = true) ||
                    c.productos.any { p -> p.nombre.contains(busqueda, ignoreCase = true) }
                }

                LazyColumn(
                    modifier       = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // ── Buscador ──────────────────────────────────────────
                    item {
                        OutlinedTextField(
                            value         = busqueda,
                            onValueChange = { busqueda = it },
                            modifier      = Modifier.fillMaxWidth(),
                            placeholder   = {
                                Text(
                                    "Buscar por supermercado o producto",
                                    color = MaterialTheme.colorScheme.outline
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Search, null,
                                    tint = MaterialTheme.colorScheme.outline)
                            },
                            shape  = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor    = MaterialTheme.colorScheme.outlineVariant,
                                focusedBorderColor      = MaterialTheme.colorScheme.primary,
                                focusedContainerColor   = Color(0xFFFFFFFF),
                                unfocusedContainerColor = Color(0xFFFFFFFF)
                            ),
                            singleLine = true
                        )
                    }

                    // ── Filtros chip ──────────────────────────────────────
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val filters = listOf("Todo", "Últimos 7 días", "Este mes", "Categorías")
                            items(filters) { f ->
                                FilterChip(
                                    selected = f == filtroSel,
                                    onClick  = { filtroSel = f },
                                    label    = { Text(f, style = MaterialTheme.typography.labelSmall) },
                                    shape    = RoundedCornerShape(20.dp),
                                    colors   = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor     = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // ── Summary widget ────────────────────────────────────
                    item {
                        Surface(
                            modifier        = Modifier.fillMaxWidth(),
                            shape           = RoundedCornerShape(16.dp),
                            color           = Color(0xFFFFFFFF),
                            shadowElevation = 1.dp
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                Column {
                                    LabelCaps("GASTO TOTAL MES")
                                    Text(
                                        "$ %,.2f".format(comprasFiltradas.sumOf { it.total }),
                                        fontSize      = 24.sp,
                                        fontWeight    = FontWeight.Bold,
                                        color         = MaterialTheme.colorScheme.primary,
                                        letterSpacing = (-0.48).sp
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    LabelCaps("COMPRAS")
                                    Text(
                                        "${comprasFiltradas.size}",
                                        fontSize   = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color      = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    // ── Hint swipe ────────────────────────────────────────
                    if (comprasFiltradas.isNotEmpty()) {
                        item {
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier              = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    null,
                                    tint     = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    "Deslizá hacia la izquierda para eliminar",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }

                    // ── Lista con swipe-to-delete ─────────────────────────
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
                                        if (busqueda.isNotBlank()) "Sin resultados para \"$busqueda\""
                                        else stringResource(R.string.compra_sin_compras),
                                        color     = MaterialTheme.colorScheme.outline,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        items(
                            items = comprasFiltradas,
                            key   = { it.id }
                        ) { compra ->
                            SwipeToDeleteCompraCard(
                                compra    = compra,
                                onClick   = { onCompraClick(compra.id) },
                                onDelete  = { compraAEliminar = compra }
                            )
                        }
                    }

                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

// ── Swipe-to-delete wrapper ────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteCompraCard(
    compra   : Compra,
    onClick  : () -> Unit,
    onDelete : () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()   // abre el dialog; el item no se borra aún
            }
            false            // no confirmar el swipe directamente, lo hace el dialog
        },
        positionalThreshold = { it * 0.40f }   // 40% del ancho para activar
    )

    SwipeToDismissBox(
        state             = dismissState,
        modifier          = Modifier.fillMaxWidth(),
        enableDismissFromStartToEnd = false,   // solo izquierda → derecha no
        enableDismissFromEndToStart = true,    // ← swipe para eliminar
        backgroundContent = { DeleteBackground(dismissState) },
        content          = {
            CompraCard(compra = compra, onClick = onClick)
        }
    )
}

// ── Fondo rojo que aparece al deslizar ────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteBackground(state: SwipeToDismissBoxState) {
    val progressing = state.dismissDirection == SwipeToDismissBoxValue.EndToStart

    val bgColor by animateColorAsState(
        targetValue = if (progressing)
            MaterialTheme.colorScheme.errorContainer
        else
            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0f),
        label = "bg"
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
            modifier = Modifier.scale(iconScale)
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Eliminar",
                tint     = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                "Eliminar",
                color  = MaterialTheme.colorScheme.error,
                style  = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ── Card de compra (sin cambios de estilo) ────────────────────────────────────
@Composable
fun CompraCard(compra: Compra, onClick: () -> Unit = {}) {
    val fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape    = RoundedCornerShape(14.dp),
        color    = Color(0xFFFFFFFF),
        shadowElevation = 1.dp,
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBBCABF).copy(alpha = 0.3f))
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
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
private fun ListadoPreview() { SuperAhorroTheme { ListadoComprasScreen() } }
