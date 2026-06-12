package com.undef.superahorro.BossioCorrea.ui.screens.promociones

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.superahorro.BossioCorrea.R
import com.undef.superahorro.BossioCorrea.data.repository.Promocion
import com.undef.superahorro.BossioCorrea.ui.components.MainBottomBar
import com.undef.superahorro.BossioCorrea.ui.components.PriceChip
import com.undef.superahorro.BossioCorrea.ui.components.StitchCard
import com.undef.superahorro.BossioCorrea.ui.components.StitchTopBar
import com.undef.superahorro.BossioCorrea.ui.navigation.Routes
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState

private const val CIUDAD_SUGERIDA = "Córdoba"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromocionesScreen(
    vm          : PromocionesViewModel = viewModel(),
    onBackClick : () -> Unit = {},
    currentRoute : String = Routes.PROMOCIONES,
    onTabClick   : (String) -> Unit = {}
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    var filtro by remember { mutableStateOf(CIUDAD_SUGERIDA) }
    var filtroProducto by remember { mutableStateOf("") }

    Scaffold(
        topBar = { StitchTopBar(stringResource(R.string.promociones_titulo), onBackClick) },
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { MainBottomBar(currentRoute = currentRoute, onTabClick = onTabClick) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // ── Encabezado y filtro ──────────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Text(
                    stringResource(R.string.promociones_titulo),
                    fontSize = 28.sp, fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    stringResource(R.string.promociones_subtitulo),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(Modifier.height(14.dp))
                OutlinedTextField(
                    value         = filtro,
                    onValueChange = { filtro = it },
                    modifier      = Modifier.fillMaxWidth(),
                    placeholder   = { Text(stringResource(R.string.promociones_filtro_placeholder)) },
                    leadingIcon   = { Icon(Icons.Default.LocationCity, null, tint = MaterialTheme.colorScheme.primary) },
                    singleLine    = true,
                    shape         = RoundedCornerShape(14.dp)
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value         = filtroProducto,
                    onValueChange = { filtroProducto = it },
                    modifier      = Modifier.fillMaxWidth(),
                    placeholder   = { Text(stringResource(R.string.promociones_filtro_producto_placeholder)) },
                    leadingIcon   = { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary) },
                    singleLine    = true,
                    shape         = RoundedCornerShape(14.dp)
                )
            }

            // ── Resultados ────────────────────────────────────────────────────
            when (val state = uiState) {
                is UiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }

                is UiState.Error -> Box(Modifier.fillMaxSize().padding(32.dp), Alignment.Center) {
                    Text(
                        stringResource(R.string.promociones_error),
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center
                    )
                }

                is UiState.Success -> {
                    val todas = state.data
                    val filtradas = todas.filter { promo ->
                        val coincideUbicacion = filtro.isBlank() ||
                                promo.ciudad?.contains(filtro, ignoreCase = true) == true ||
                                promo.supermercado.contains(filtro, ignoreCase = true)
                        val coincideProducto = filtroProducto.isBlank() ||
                                promo.producto.contains(filtroProducto, ignoreCase = true)
                        coincideUbicacion && coincideProducto
                    }

                    when {
                        todas.isEmpty() -> Box(Modifier.fillMaxSize().padding(32.dp), Alignment.Center) {
                            Text(
                                stringResource(R.string.promociones_sin_datos),
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = TextAlign.Center
                            )
                        }

                        filtradas.isEmpty() -> Column(
                            modifier = Modifier.fillMaxSize().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                stringResource(R.string.promociones_sin_resultados),
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(12.dp))
                            TextButton(onClick = { filtro = ""; filtroProducto = "" }) {
                                Text(stringResource(R.string.promociones_ver_todas))
                            }
                        }

                        else -> LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Text(
                                    stringResource(R.string.promociones_resultados, filtradas.size),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                            items(filtradas) { promocion -> PromocionCard(promocion) }
                            item {
                                Text(
                                    stringResource(R.string.promociones_fuente),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PromocionCard(promocion: Promocion) {
    StitchCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        promocion.producto,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Storefront, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.outline)
                        Text(
                            listOfNotNull(promocion.supermercado, promocion.ciudad, promocion.pais).joinToString(" · "),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                PriceChip(text = etiquetaDescuento(promocion))
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    formatearPrecio(promocion.precio, promocion.moneda),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                if (promocion.precioSinDescuento != null && promocion.precioSinDescuento > promocion.precio) {
                    Text(
                        formatearPrecio(promocion.precioSinDescuento, promocion.moneda),
                        style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.LineThrough),
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

private fun formatearPrecio(valor: Double, moneda: String) = "%.2f %s".format(valor, moneda).trim()

private fun etiquetaDescuento(promocion: Promocion): String {
    val original = promocion.precioSinDescuento
    if (original != null && original > promocion.precio && original > 0) {
        val porcentaje = (((original - promocion.precio) / original) * 100).toInt()
        return "-$porcentaje%"
    }
    return "🏷"
}
