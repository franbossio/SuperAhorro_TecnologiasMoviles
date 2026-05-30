package com.undef.superahorro.BossioCorrea.ui.screens.perfil

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.superahorro.BossioCorrea.R
import com.undef.superahorro.BossioCorrea.ui.components.LabelCaps
import com.undef.superahorro.BossioCorrea.ui.components.StitchTopBar
import com.undef.superahorro.BossioCorrea.ui.navigation.UiState
import com.undef.superahorro.BossioCorrea.ui.theme.SuperAhorroTheme

// ── Paleta de las tres stat cards ─────────────────────────────────────────────
private val CardCompras = listOf(Color(0xFF006C49), Color(0xFF10B981))   // verde
private val CardProductos = listOf(Color(0xFF005AC2), Color(0xFF71A1FF)) // azul
private val CardAhorrado = listOf(Color(0xFF8B00C9), Color(0xFFD07BFF))  // violeta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    vm                  : PerfilViewModel = viewModel(),
    onGuardarClick      : () -> Unit = {},
    onCerrarSesionClick : () -> Unit = {},
    onBackClick         : () -> Unit = {}
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    var nombre            by remember { mutableStateOf("") }
    var apellido          by remember { mutableStateOf("") }
    var email             by remember { mutableStateOf("") }
    var cantidadCompras   by remember { mutableStateOf(0) }
    var cantidadProductos by remember { mutableStateOf(0) }
    var totalGastado      by remember { mutableStateOf(0.0) }
    var showLogoutDialog  by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            val u = (uiState as UiState.Success<PerfilData>).data
            nombre            = u.nombre
            apellido          = u.apellido
            email             = u.email
            cantidadCompras   = u.cantidadCompras
            cantidadProductos = u.cantidadProductos
            totalGastado      = u.totalGastado
        }
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor      = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor    = MaterialTheme.colorScheme.outlineVariant,
        focusedContainerColor   = MaterialTheme.colorScheme.surfaceContainerLow,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
    )

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(R.string.perfil_cerrar_sesion), fontWeight = FontWeight.Bold) },
            text  = { Text(stringResource(R.string.perfil_cerrar_sesion_confirmacion),
                color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                Button(
                    onClick = { showLogoutDialog = false; onCerrarSesionClick() },
                    colors  = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text(stringResource(R.string.perfil_cerrar_sesion), color = Color.White) }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLogoutDialog = false }) { Text(stringResource(R.string.cancelar)) }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        topBar = { StitchTopBar(stringResource(R.string.perfil_titulo), onBackClick) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            // ── Avatar ────────────────────────────────────────────────────
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = if (nombre.isNotEmpty()) nombre.first().uppercase() else "U",
                        fontSize   = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(
                    onClick  = {},
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                ) {
                    Icon(Icons.Default.CameraAlt, null, tint = Color.White,
                        modifier = Modifier.size(16.dp))
                }
            }

            Spacer(Modifier.height(12.dp))
            Text("$nombre $apellido", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface)
            Text(email, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline)

            Spacer(Modifier.height(24.dp))

            // ── Stats cards rediseñadas ────────────────────────────────────
            StatsRow(
                cantidadCompras   = cantidadCompras,
                cantidadProductos = cantidadProductos,
                totalGastado      = totalGastado
            )

            Spacer(Modifier.height(24.dp))

            // ── Form card ─────────────────────────────────────────────────
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp),
                color    = MaterialTheme.colorScheme.surfaceContainerLow,
                shadowElevation = 1.dp,
                border   = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBBCABF).copy(alpha = 0.3f))
                )
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            LabelCaps(stringResource(R.string.register_nombre))
                            OutlinedTextField(value = nombre, onValueChange = { nombre = it },
                                modifier = Modifier.fillMaxWidth(), singleLine = true,
                                shape = RoundedCornerShape(12.dp), colors = fieldColors)
                        }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            LabelCaps(stringResource(R.string.register_apellido))
                            OutlinedTextField(value = apellido, onValueChange = { apellido = it },
                                modifier = Modifier.fillMaxWidth(), singleLine = true,
                                shape = RoundedCornerShape(12.dp), colors = fieldColors)
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        LabelCaps(stringResource(R.string.campo_email))
                        OutlinedTextField(value = email, onValueChange = { email = it },
                            modifier = Modifier.fillMaxWidth(), singleLine = true,
                            shape = RoundedCornerShape(12.dp), colors = fieldColors)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick   = { vm.guardar(nombre, apellido, email, onGuardarClick) },
                modifier  = Modifier.fillMaxWidth().height(56.dp),
                shape     = RoundedCornerShape(14.dp),
                colors    = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Text(stringResource(R.string.perfil_guardar), fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick  = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                border   = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        MaterialTheme.colorScheme.error.copy(alpha = 0.4f))
                )
            ) {
                Icon(Icons.Default.Logout, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.perfil_cerrar_sesion), fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Stats Row — tres tarjetas con gradiente y animación count-up
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun StatsRow(
    cantidadCompras   : Int,
    cantidadProductos : Int,
    totalGastado      : Double
) {
    val gastoFormateado = when {
        totalGastado >= 1_000_000 -> "$ %.1fM".format(totalGastado / 1_000_000)
        totalGastado >= 1_000     -> "$ %.0fk".format(totalGastado / 1_000)
        else                      -> "$ %.0f".format(totalGastado)
    }
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatGradientCard(
            modifier  = Modifier.weight(1f),
            emoji     = "🛒",
            value     = "$cantidadCompras",
            label     = stringResource(R.string.home_compras),
            gradient  = CardCompras
        )
        StatGradientCard(
            modifier  = Modifier.weight(1f),
            emoji     = "📦",
            value     = "$cantidadProductos",
            label     = stringResource(R.string.perfil_stat_productos),
            gradient  = CardProductos
        )
        StatGradientCard(
            modifier  = Modifier.weight(1f),
            emoji     = "💰",
            value     = gastoFormateado,
            label     = stringResource(R.string.perfil_stat_ahorrado),
            gradient  = CardAhorrado
        )
    }
}

@Composable
private fun StatGradientCard(
    modifier : Modifier,
    emoji    : String,
    value    : String,
    label    : String,
    gradient : List<Color>
) {
    // Animación de entrada: slide-up + fade-in
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(500, easing = EaseOutCubic),
        label         = "card_alpha_$label"
    )
    val offsetY by animateFloatAsState(
        targetValue   = if (visible) 0f else 24f,
        animationSpec = tween(500, easing = EaseOutBack),
        label         = "card_offset_$label"
    )
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = modifier
            .height(110.dp)
            .offset(y = offsetY.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.linearGradient(
                    colors = gradient,
                    start  = Offset(0f, 0f),
                    end    = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
            // alpha directo en el Modifier no existe, lo aplicamos con graphicsLayer
            .then(
                Modifier.graphicsLayer { this.alpha = alpha }
            )
            .padding(14.dp)
    ) {
        // Círculo decorativo de fondo
        Box(
            Modifier
                .size(64.dp)
                .align(Alignment.TopEnd)
                .offset(x = 18.dp, y = (-18).dp)
                .background(Color.White.copy(alpha = 0.10f), CircleShape)
        )

        Column(
            modifier              = Modifier.fillMaxSize(),
            verticalArrangement   = Arrangement.SpaceBetween
        ) {
            // Emoji en la esquina superior izquierda
            Text(emoji, fontSize = 20.sp)

            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Text(
                    text       = value,
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color.White,
                    letterSpacing = (-0.44).sp
                )
                Text(
                    text  = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.75f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PerfilPreview() { SuperAhorroTheme { PerfilScreen() } }