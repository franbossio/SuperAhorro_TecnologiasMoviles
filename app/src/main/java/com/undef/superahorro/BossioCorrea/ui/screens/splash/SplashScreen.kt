package com.undef.superahorro.BossioCorrea.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.PriceCheck
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.undef.superahorro.BossioCorrea.R
import com.undef.superahorro.BossioCorrea.ui.theme.SuperAhorroTheme

@Composable
fun SplashScreen(
    onIngresarClick    : () -> Unit = {},
    onCrearCuentaClick : () -> Unit = {}
) {
    // ── Animación de entrada (fade + scale) ────────────────────────────────
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val alpha by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label         = "alpha"
    )
    val translateY by animateFloatAsState(
        targetValue   = if (visible) 0f else 40f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label         = "translateY"
    )

    // ── Pulso del icono ────────────────────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.15f,
        targetValue   = 0.30f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    val iconScale by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = 1.04f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconScale"
    )

    // Colores del tema
    val primary          = MaterialTheme.colorScheme.primary          // #006c49
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer // #10b981
    val surface          = MaterialTheme.colorScheme.surface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    // Fondo con mesh gradient (emula los radial-gradient del HTML)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        primary.copy(alpha = 0.06f),
                        surface
                    ),
                    radius = 1400f
                )
            )
    ) {
        // ── Blobs decorativos (top-left y bottom-right del HTML) ───────────
        Box(
            modifier = Modifier
                .size(320.dp)
                .offset((-80).dp, (-80).dp)
                .clip(CircleShape)
                .background(primary.copy(alpha = 0.05f))
                .blur(60.dp)
        )
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.BottomEnd)
                .offset(80.dp, 80.dp)
                .clip(CircleShape)
                .background(primary.copy(alpha = 0.05f))
                .blur(60.dp)
        )

        // ── Contenido centrado ─────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .offset(y = translateY.dp)
                .graphicsLayer(alpha = alpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Sección Logo ───────────────────────────────────────────────
            Box(
                modifier         = Modifier.size(140.dp),
                contentAlignment = Alignment.Center
            ) {
                // Glow exterior (group-hover blur del HTML)
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(primary.copy(alpha = glowAlpha))
                        .blur(20.dp)
                )
                // Card del ícono (rounded-[40px] del HTML)
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(iconScale)
                        .clip(RoundedCornerShape(40.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(
                            width = 1.dp,
                            color = primary.copy(alpha = 0.10f),
                            shape = RoundedCornerShape(40.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo Super Ahorro",
                        modifier = Modifier.size(120.dp)
                    )
                }

            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Brand Identity ─────────────────────────────────────────────
            Text(
                text       = stringResource(R.string.splash_titulo).uppercase(),
                fontSize   = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = primary,
                letterSpacing = (-0.5).sp,
                textAlign  = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text      = stringResource(R.string.splash_subtitulo),
                style     = MaterialTheme.typography.bodyMedium,
                color     = onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier  = Modifier.widthIn(max = 260.dp)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // ── Feature Bento (3 cards horizontales del HTML) ─────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FeatureCard(
                    modifier = Modifier.weight(1f),
                    icon     = Icons.Outlined.ReceiptLong,
                    label    = "ESCANEO",
                    desc     = "Tickets"
                )
                FeatureCard(
                    modifier = Modifier.weight(1f),
                    icon     = Icons.Outlined.Analytics,
                    label    = "STATS",
                    desc     = "Tu gasto visual"
                )
                FeatureCard(
                    modifier = Modifier.weight(1f),
                    icon     = Icons.Outlined.PriceCheck,
                    label    = "OFERTAS",
                    desc     = "Mejores precios"
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // ── CTA: Botón primario (Get Started del HTML) ─────────────────
            Button(
                onClick  = onCrearCuentaClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primary,
                    contentColor   = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text       = "Empezar",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector        = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier           = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── CTA: Botón secundario (Sign In del HTML) ───────────────────
            OutlinedButton(
                onClick  = onIngresarClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = primary
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        primary.copy(alpha = 0.35f)
                    )
                )
            ) {
                Text(
                    text       = stringResource(R.string.splash_btn_ingresar),
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Trust indicator (Secure & Encrypted del HTML) ──────────────
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier              = Modifier.graphicsLayer(alpha = 0.55f)
            ) {
                Icon(
                    imageVector        = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint               = onSurfaceVariant,
                    modifier           = Modifier.size(14.dp)
                )
                Text(
                    text  = "DATOS SEGUROS Y ENCRIPTADOS",
                    style = MaterialTheme.typography.labelSmall,
                    color = onSurfaceVariant,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}

@Composable
private fun FeatureCard(
    modifier : Modifier = Modifier,
    icon     : ImageVector,
    label    : String,
    desc     : String
) {
    val primary = MaterialTheme.colorScheme.primary

    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor   = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border    = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )
        )
    ) {
        Column(
            modifier            = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(primary.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = icon,
                    contentDescription = null,
                    tint               = primary,
                    modifier           = Modifier.size(18.dp)
                )
            }
            Text(
                text       = label,
                fontSize   = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp,
                color      = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text  = desc,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SplashPreview() {
    SuperAhorroTheme { SplashScreen() }
}