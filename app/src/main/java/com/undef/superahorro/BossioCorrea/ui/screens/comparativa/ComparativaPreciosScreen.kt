package com.undef.superahorro.BossioCorrea.ui.screens.comparativa

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.superahorro.BossioCorrea.R
import com.undef.superahorro.BossioCorrea.domain.model.PrecioProducto
import com.undef.superahorro.BossioCorrea.ui.components.LabelCaps
import com.undef.superahorro.BossioCorrea.ui.components.StitchTopBar
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import com.undef.superahorro.BossioCorrea.ui.theme.SuperAhorroTheme
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparativaPreciosScreen(
    vm          : ComparativaPreciosViewModel = viewModel(),
    onBackClick : () -> Unit = {}
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { StitchTopBar(stringResource(R.string.comparativa_titulo), onBackClick) },
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
                val data = state.data
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding)
                        .padding(horizontal = 20.dp).verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        stringResource(R.string.comparativa_titulo),
                        fontSize = 24.sp, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        stringResource(R.string.comparativa_subtitulo),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(Modifier.height(20.dp))

                    if (data.mesesDisponibles.isEmpty()) {
                        EmptyState(stringResource(R.string.comparativa_sin_compras))
                    } else {
                        // ── Selector de mes ──────────────────────────────────
                        LabelCaps(stringResource(R.string.comparativa_mes))
                        Spacer(Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(data.mesesDisponibles) { mes ->
                                FilterChip(
                                    selected = mes == data.mesSeleccionado,
                                    onClick  = { vm.seleccionarMes(mes) },
                                    label    = { Text(formatMes(mes)) },
                                    shape    = RoundedCornerShape(20.dp),
                                    colors   = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor     = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        if (data.productosDisponibles.isEmpty()) {
                            EmptyState(stringResource(R.string.comparativa_sin_productos))
                        } else {
                            // ── Selector de producto ─────────────────────────
                            LabelCaps(stringResource(R.string.comparativa_producto))
                            Spacer(Modifier.height(8.dp))
                            ProductoSelector(
                                productos     = data.productosDisponibles,
                                seleccionado  = data.productoSeleccionado,
                                onSeleccionar = { vm.seleccionarProducto(it) }
                            )

                            Spacer(Modifier.height(20.dp))

                            if (data.cargandoIA) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.primary)
                                    Text(stringResource(R.string.comparativa_agrupando), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                                }
                                Spacer(Modifier.height(10.dp))
                            }

                            if (data.comparacion.size <= 1) {
                                EmptyState(stringResource(R.string.comparativa_sin_otros_supers))
                            } else {
                                val precioMin = data.comparacion.minOf { it.precio }
                                data.comparacion.forEach { item ->
                                    ComparacionRow(item, precioMin, data.productoSeleccionado)
                                    Spacer(Modifier.height(10.dp))
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductoSelector(
    productos     : List<String>,
    seleccionado  : String?,
    onSeleccionar : (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value         = seleccionado ?: "",
            onValueChange = {},
            readOnly      = true,
            modifier      = Modifier.fillMaxWidth().menuAnchor(),
            trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            shape         = RoundedCornerShape(12.dp),
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            productos.forEach { nombre ->
                DropdownMenuItem(
                    text    = { Text(nombre) },
                    onClick = { onSeleccionar(nombre); expanded = false }
                )
            }
        }
    }
}

@Composable
private fun ComparacionRow(item: PrecioProducto, precioMin: Double, nombreSeleccionado: String?) {
    val esMasBarato = item.precio == precioMin
    val nombreDistinto = nombreSeleccionado != null && !item.nombre.trim().equals(nombreSeleccionado, ignoreCase = true)
    val diferenciaPct = if (precioMin > 0 && !esMasBarato) {
        (((item.precio - precioMin) / precioMin) * 100).toInt()
    } else null

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        color    = if (esMasBarato) MaterialTheme.colorScheme.primary.copy(0.08f) else MaterialTheme.colorScheme.surfaceContainerLow,
        border   = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(
                if (esMasBarato) MaterialTheme.colorScheme.primary.copy(0.35f)
                else MaterialTheme.colorScheme.outlineVariant.copy(0.25f)
            )
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Storefront, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Text(item.supermercado, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                    if (esMasBarato) {
                        Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primary) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(Icons.Default.Star, null, tint = Color.White, modifier = Modifier.size(11.dp))
                                Text(stringResource(R.string.comparativa_mas_barato), color = Color.White,
                                    style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    item.fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                if (nombreDistinto) {
                    Text(
                        item.nombre,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "$ %,.2f".format(item.precio),
                    fontWeight = FontWeight.ExtraBold, fontSize = 18.sp,
                    color = if (esMasBarato) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                if (diferenciaPct != null && diferenciaPct > 0) {
                    Text(
                        stringResource(R.string.comparativa_diferencia, diferenciaPct),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState(texto: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(12.dp),
        color    = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Box(Modifier.padding(16.dp)) { Text(texto, color = MaterialTheme.colorScheme.outline) }
    }
}

private fun formatMes(anioMes: String): String = try {
    val ym     = YearMonth.parse(anioMes)
    val locale = Locale.getDefault()
    val nombreMes = ym.month.getDisplayName(TextStyle.FULL, locale)
        .replaceFirstChar { it.uppercase(locale) }
    "$nombreMes ${ym.year}"
} catch (_: Exception) { anioMes }

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ComparativaPreciosPreview() { SuperAhorroTheme { ComparativaPreciosScreen() } }
