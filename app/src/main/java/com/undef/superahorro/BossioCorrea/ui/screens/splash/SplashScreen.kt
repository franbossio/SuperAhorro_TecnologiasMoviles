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

    val infinite = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infinite.animateFloat(
        initialValue  = 0.05f, targetValue = 0.12f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "glow"
    )
    val iconScale by infinite.animateFloat(
        initialValue  = 1f, targetValue = 1.04f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "scale"
    )

    val primary = MaterialTheme.colorScheme.primary   // #006c49
    val surface = MaterialTheme.colorScheme.surface   // #f7f9fb

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(primary.copy(alpha = 0.06f), surface),
                    radius = 1400f
                )
            )
    ) {
        // Blob top-left
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset((-80).dp, (-80).dp)
                .clip(CircleShape)
                .background(primary.copy(alpha = 0.05f))
                .blur(60.dp)
        )
        // Blob bottom-right
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.BottomEnd)
                .offset(80.dp, 80.dp)
                .clip(CircleShape)
                .background(primary.copy(alpha = 0.05f))
                .blur(60.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .offset(y = translateY.dp)
                .graphicsLayer(alpha = alpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ── Logo ──────────────────────────────────────────────────────
            Box(modifier = Modifier.size(140.dp), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(primary.copy(alpha = glowAlpha))
                        .blur(20.dp)
                )
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(iconScale)
                        .clip(RoundedCornerShape(40.dp))
                        .background(Color(0xFFFFFFFF))
                        .border(1.dp, primary.copy(alpha = 0.10f), RoundedCornerShape(40.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter            = painterResource(R.drawable.logo),
                        contentDescription = "Logo Super Ahorro",
                        modifier           = Modifier.size(110.dp)
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── Brand ─────────────────────────────────────────────────────
            Text(
                text          = stringResource(R.string.splash_titulo).uppercase(),
                fontSize      = 36.sp,
                fontWeight    = FontWeight.ExtraBold,
                color         = primary,
                letterSpacing = (-0.72).sp,
                textAlign     = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text      = stringResource(R.string.splash_subtitulo),
                style     = MaterialTheme.typography.bodyMedium,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier  = Modifier.widthIn(max = 260.dp)
            )

            Spacer(Modifier.height(36.dp))

            // ── Feature bento 3 cards ─────────────────────────────────────
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FeatureCard(Modifier.weight(1f), Icons.Outlined.ReceiptLong, "ESCANEO",  "Tickets")
                FeatureCard(Modifier.weight(1f), Icons.Outlined.Analytics,   "STATS",    "Tu gasto visual")
                FeatureCard(Modifier.weight(1f), Icons.Outlined.PriceCheck,  "OFERTAS",  "Precios")
            }

            Spacer(Modifier.height(36.dp))

            // ── CTA primario ──────────────────────────────────────────────
            Button(
                onClick   = onCrearCuentaClick,
                modifier  = Modifier.fillMaxWidth().height(56.dp),
                shape     = RoundedCornerShape(14.dp),
                colors    = ButtonDefaults.buttonColors(containerColor = primary, contentColor = Color.White),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text("Empezar", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
            }

            Spacer(Modifier.height(12.dp))

            // ── CTA secundario ────────────────────────────────────────────
            OutlinedButton(
                onClick = onIngresarClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = primary),
                border   = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(primary.copy(alpha = 0.35f))
                )
            ) {
                Text(stringResource(R.string.splash_btn_ingresar), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(28.dp))

            // ── Trust badge ───────────────────────────────────────────────
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier              = Modifier.graphicsLayer(alpha = 0.55f)
            ) {
                Icon(Icons.Default.VerifiedUser, null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp))
                Text("DATOS SEGUROS Y ENCRIPTADOS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.8.sp)
            }
        }
    }
}

@Composable
private fun FeatureCard(modifier: Modifier, icon: ImageVector, label: String, desc: String) {
    val primary = MaterialTheme.colorScheme.primary
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFFFF),
            contentColor   = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(1.dp),
        border    = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )
        )
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(
                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
                    .background(primary.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = primary, modifier = Modifier.size(18.dp))
            }
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.6.sp)
            Text(desc, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SplashPreview() { SuperAhorroTheme { SplashScreen() } }
