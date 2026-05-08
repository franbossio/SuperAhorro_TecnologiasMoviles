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
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val alpha by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "alpha"
    )
    val translateY by animateFloatAsState(
        targetValue   = if (visible) 0f else 40f,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "ty"
    )

    val infinite = rememberInfiniteTransition(label = "pulse")
    val glowAlpha by infinite.animateFloat(
        initialValue  = 0.18f, targetValue = 0.32f,
        animationSpec = infiniteRepeatable(tween(2000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "glow"
    )
    val iconScale by infinite.animateFloat(
        initialValue  = 1f, targetValue = 1.04f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "scale"
    )
    // Blob flotante secundario
    val blobOffset by infinite.animateFloat(
        initialValue  = 0f, targetValue = 18f,
        animationSpec = infiniteRepeatable(tween(3000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "blob"
    )

    val primary = MaterialTheme.colorScheme.primary   // #006c49
    val emerald = Color(0xFF10B981)                   // emerald-500

    // Fondo: verde sólido de punta a punta, con acento esmeralda abajo
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.00f to primary,
                        0.60f to primary,
                        1.00f to Color(0xFF004D33)   // verde más oscuro al pie
                    )
                )
            )
    ) {

        // ── Blob decorativo top-left (grande, difuso) ─────────────────────
        Box(
            modifier = Modifier
                .size(340.dp)
                .offset((-90).dp, (-60).dp)
                .clip(CircleShape)
                .background(emerald.copy(alpha = glowAlpha))
                .blur(80.dp)
        )

        // ── Blob decorativo bottom-right ──────────────────────────────────
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.BottomEnd)
                .offset(70.dp, (blobOffset - 40).dp)
                .clip(CircleShape)
                .background(primary.copy(alpha = 0.18f))
                .blur(70.dp)
        )

        // ── Blob accent central-left ──────────────────────────────────────
        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.CenterStart)
                .offset((-60).dp, (-blobOffset).dp)
                .clip(CircleShape)
                .background(emerald.copy(alpha = 0.12f))
                .blur(50.dp)
        )

        // ── Contenido principal ───────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .offset(y = translateY.dp)
                .graphicsLayer(alpha = alpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Logo ───────────────────────────────────────────────────────
            Box(modifier = Modifier.size(140.dp), contentAlignment = Alignment.Center) {
                // Halo exterior con color
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(emerald.copy(alpha = glowAlpha * 0.8f), Color.Transparent)
                            )
                        )
                        .blur(24.dp)
                )
                // Contenedor del logo
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(iconScale)
                        .clip(RoundedCornerShape(40.dp))
                        .background(Color.White)
                        .border(
                            width = 1.5.dp,
                            brush = Brush.linearGradient(listOf(emerald.copy(0.5f), primary.copy(0.2f))),
                            shape = RoundedCornerShape(40.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter            = painterResource(R.drawable.logo_sin_letra),
                        contentDescription = "Logo Super Ahorro",
                        modifier           = Modifier.size(110.dp)
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── Brand ──────────────────────────────────────────────────────
            Text(
                text          = stringResource(R.string.splash_titulo).uppercase(),
                fontSize      = 36.sp,
                fontWeight    = FontWeight.ExtraBold,
                color         = Color.White,
                letterSpacing = (-0.72).sp,
                textAlign     = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text      = stringResource(R.string.splash_subtitulo),
                style     = MaterialTheme.typography.bodyMedium,
                color     = Color.White.copy(alpha = 0.78f),
                textAlign = TextAlign.Center,
                modifier  = Modifier.widthIn(max = 260.dp)
            )

            Spacer(Modifier.height(36.dp))

            // ── Feature bento 3 cards ──────────────────────────────────────
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FeatureCard(Modifier.weight(1f), Icons.Outlined.ReceiptLong, stringResource(R.string.escaneo),  stringResource(R.string.sub_escaneo),
                    cardColor = Color.White.copy(alpha = 0.15f), iconBg = emerald.copy(0.30f), iconTint = Color.White)
                FeatureCard(Modifier.weight(1f), Icons.Outlined.Analytics,   stringResource(R.string.stats),    stringResource(R.string.sub_stats),
                    cardColor = Color.White.copy(alpha = 0.15f), iconBg = emerald.copy(0.30f), iconTint = Color.White)
                FeatureCard(Modifier.weight(1f), Icons.Outlined.PriceCheck,  stringResource(R.string.ofertas),    stringResource(R.string.sub_ofertas),
                    cardColor = Color.White.copy(alpha = 0.15f), iconBg = emerald.copy(0.30f), iconTint = Color.White)
            }

            Spacer(Modifier.height(36.dp))

            // ── CTA primario ───────────────────────────────────────────────
            Button(
                onClick   = onCrearCuentaClick,
                modifier  = Modifier.fillMaxWidth().height(56.dp),
                shape     = RoundedCornerShape(14.dp),
                colors    = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor   = primary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(stringResource(R.string.empezar), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
            }

            Spacer(Modifier.height(12.dp))

            // ── CTA secundario ─────────────────────────────────────────────
            OutlinedButton(
                onClick  = onIngresarClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border   = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color.White.copy(alpha = 0.75f))
                )
            ) {
                Text(
                    stringResource(R.string.splash_btn_ingresar),
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(28.dp))

            // ── Trust badge ────────────────────────────────────────────────
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier              = Modifier.graphicsLayer(alpha = 0.60f)
            ) {
                Icon(
                    Icons.Default.VerifiedUser, null,
                    tint     = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    stringResource(R.string.seguridad),
                    style         = MaterialTheme.typography.labelSmall,
                    color         = Color.White,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}

// ── Feature card con fondo translúcido (glassmorphism suave) ─────────────────

@Composable
private fun FeatureCard(
    modifier  : Modifier,
    icon      : ImageVector,
    label     : String,
    desc      : String,
    cardColor : Color,
    iconBg    : Color,
    iconTint  : Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(cardColor)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(Color.White.copy(0.40f), Color.White.copy(0.10f))
                ),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
            }
            Text(
                label,
                fontSize      = 10.sp,
                fontWeight    = FontWeight.Bold,
                letterSpacing = 0.6.sp,
                color         = Color.White
            )
            Text(
                desc,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.72f)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SplashPreview() { SuperAhorroTheme { SplashScreen() } }