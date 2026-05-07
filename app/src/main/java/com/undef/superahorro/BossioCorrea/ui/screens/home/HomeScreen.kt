package com.undef.superahorro.BossioCorrea.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.superahorro.BossioCorrea.R
import com.undef.superahorro.BossioCorrea.ui.components.LabelCaps
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import com.undef.superahorro.BossioCorrea.ui.theme.SuperAhorroTheme
import com.undef.superahorro.BossioCorrea.ui.theme.StitchPrimary
import com.undef.superahorro.BossioCorrea.ui.theme.StitchPrimaryContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    vm                  : HomeViewModel = viewModel(),
    onNuevaCompraClick  : () -> Unit = {},
    onListadoClick      : () -> Unit = {},
    onHistorialClick    : () -> Unit = {},
    onEstadisticasClick : () -> Unit = {},
    onPerfilClick       : () -> Unit = {},
    onSettingsClick     : () -> Unit = {}
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("SUPER AHORRO", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary, letterSpacing = (-0.3).sp)
                },
                actions = {
                    IconButton(onClick = onPerfilClick) {
                        Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.outline)
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, null, tint = MaterialTheme.colorScheme.outline)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF8FAFC)
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->

        when (val state = uiState) {
            is UiState.Loading -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            is UiState.Error   -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                Text(stringResource(R.string.error_generico))
            }
            is UiState.Success -> {
                val data = state.data
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Spacer(Modifier.height(4.dp))

                    // ── Welcome header ────────────────────────────────────
                    Column {
                        LabelCaps("Bienvenido")
                        Spacer(Modifier.height(4.dp))
                        Text(data.saludo, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface, letterSpacing = (-0.72).sp)
                    }

                    // ── Total spent card con gradiente suave ──────────────
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
                                    LabelCaps("TOTAL GASTADO ESTE MES")
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = "$ %,.2f".format(data.gastoMes),
                                        fontSize = 28.sp, fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        letterSpacing = (-0.56).sp
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.TrendingUp, null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp))
                                        Text("${data.cantidadCompras} compras",
                                            color = MaterialTheme.colorScheme.primary,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            // Mini gráfico de barras decorativo
                            Row(
                                Modifier.fillMaxWidth().height(48.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.Bottom
                            ) {
                                val heights = listOf(0.4f, 0.6f, 0.5f, 0.8f, 0.65f, 0.9f, 0.7f)
                                heights.forEach { h ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight(h)
                                            .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                            .background(
                                                Brush.verticalGradient(
                                                    listOf(
                                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                                    )
                                                )
                                            )
                                    )
                                }
                            }
                        }
                    }

                    // ── Última compra card ────────────────────────────────
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable { onListadoClick() },
                        shape    = RoundedCornerShape(16.dp),
                        color    = Color(0xFFFFFFFF),
                        shadowElevation = 1.dp,
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBBCABF).copy(alpha = 0.3f))
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                LabelCaps(stringResource(R.string.home_ultima_compra))
                                Spacer(Modifier.height(4.dp))
                                Text(data.ultimoSuper, style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(data.ultimaFecha, style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.outline)
                            }
                            Text("$ %,.0f".format(data.ultimoTotal),
                                fontSize = 22.sp, fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    // ── Botón nueva compra ────────────────────────────────
                    Button(
                        onClick   = onNuevaCompraClick,
                        modifier  = Modifier.fillMaxWidth().height(56.dp),
                        shape     = RoundedCornerShape(14.dp),
                        colors    = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        elevation = ButtonDefaults.buttonElevation(4.dp)
                    ) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.home_nueva_compra),
                            fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    }

                    // ── Quick access grid ─────────────────────────────────
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        QuickCard(Modifier.weight(1f), "📋", stringResource(R.string.home_mis_compras), onListadoClick)
                        QuickCard(Modifier.weight(1f), "📅", stringResource(R.string.home_historial), onHistorialClick)
                        QuickCard(Modifier.weight(1f), "📊", stringResource(R.string.home_estadisticas), onEstadisticasClick)
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun QuickCard(modifier: Modifier, emoji: String, label: String, onClick: () -> Unit) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape    = RoundedCornerShape(14.dp),
        color    = Color(0xFFFFFFFF),
        shadowElevation = 1.dp,
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBBCABF).copy(alpha = 0.3f))
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 28.sp)
            Spacer(Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomePreview() { SuperAhorroTheme { HomeScreen() } }
