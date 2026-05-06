package com.undef.superahorro.BossioCorrea.ui.screens.compras.listado

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
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
fun ListadoComprasScreen(
    vm                : ListadoComprasViewModel = viewModel(),
    onCompraClick     : (Int) -> Unit = {},
    onNuevaCompraClick: () -> Unit = {},
    onBackClick       : () -> Unit = {}
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    var busqueda by remember { mutableStateOf("") }

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
                    // ── Buscador ──────────────────────────────────────────
                    item {
                        OutlinedTextField(
                            value         = busqueda,
                            onValueChange = { busqueda = it },
                            modifier      = Modifier.fillMaxWidth(),
                            placeholder   = { Text("Buscar por supermercado o producto",
                                color = MaterialTheme.colorScheme.outline) },
                            leadingIcon   = { Icon(Icons.Default.Search, null,
                                tint = MaterialTheme.colorScheme.outline) },
                            shape         = RoundedCornerShape(12.dp),
                            colors        = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                focusedBorderColor   = MaterialTheme.colorScheme.primary,
                                focusedContainerColor= Color(0xFFFFFFFF),
                                unfocusedContainerColor = Color(0xFFFFFFFF)
                            ),
                            singleLine    = true
                        )
                    }

                    // ── Filtros chip ──────────────────────────────────────
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val filters = listOf("Todo", "Últimos 7 días", "Este mes", "Categorías")
                            items(filters) { f ->
                                FilterChip(
                                    selected = f == "Todo",
                                    onClick  = {},
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
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(16.dp),
                            color    = Color(0xFFFFFFFF),
                            shadowElevation = 1.dp
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    LabelCaps("GASTO TOTAL MES")
                                    Text(
                                        "$ %,.2f".format(compras.sumOf { it.total }),
                                        fontSize = 24.sp, fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        letterSpacing = (-0.48).sp
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    LabelCaps("COMPRAS")
                                    Text("${compras.size}", fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }

                    // ── Lista ─────────────────────────────────────────────
                    if (compras.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(top = 40.dp), Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🛒", fontSize = 48.sp)
                                    Spacer(Modifier.height(12.dp))
                                    Text(stringResource(R.string.compra_sin_compras),
                                        color = MaterialTheme.colorScheme.outline)
                                }
                            }
                        }
                    } else {
                        items(compras) { c ->
                            CompraCard(compra = c, onClick = { onCompraClick(c.id) })
                        }
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
fun CompraCard(compra: Compra, onClick: () -> Unit = {}) {
    val fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape    = RoundedCornerShape(14.dp),
        color    = Color(0xFFFFFFFF),
        shadowElevation = 1.dp,
        border   = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBBCABF).copy(alpha = 0.3f))
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column {
                Text(compra.supermercado, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(compra.fecha.format(fmt), style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline)
                Text("${compra.productos.size} ${stringResource(R.string.productos)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline)
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
