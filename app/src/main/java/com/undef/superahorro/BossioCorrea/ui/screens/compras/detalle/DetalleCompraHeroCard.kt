package com.undef.superahorro.BossioCorrea.ui.screens.compras.detalle

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.undef.superahorro.BossioCorrea.domain.model.Compra
import java.time.format.DateTimeFormatter

@Composable
internal fun HeroCard(compra: Compra) {
    val fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    var visible by remember { mutableStateOf(false) }
    val animTotal by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label         = "hero_anim"
    )
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF003D29), Color(0xFF006C49), Color(0xFF10A870)),
                    start  = Offset(0f, 0f),
                    end    = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
            .padding(24.dp)
    ) {
        Box(
            Modifier
                .size(180.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-40).dp)
                .background(Color.White.copy(alpha = 0.05f), CircleShape)
        )
        Box(
            Modifier
                .size(100.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-30).dp, y = 30.dp)
                .background(Color.White.copy(alpha = 0.04f), CircleShape)
        )

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "ESTABLECIMIENTO",
                        style         = MaterialTheme.typography.labelSmall,
                        color         = Color.White.copy(alpha = 0.65f),
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        compra.supermercado,
                        fontSize      = 26.sp,
                        fontWeight    = FontWeight.ExtraBold,
                        color         = Color.White,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        "${compra.fecha.format(fmt)}  ·  ${compra.hora}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.65f)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.15f)
                ) {
                    Text(
                        "COMPLETADO",
                        modifier      = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style         = MaterialTheme.typography.labelSmall,
                        color         = Color.White,
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
            Spacer(Modifier.height(16.dp))

            Text(
                "TOTAL ABONADO",
                style         = MaterialTheme.typography.labelSmall,
                color         = Color.White.copy(alpha = 0.65f),
                fontWeight    = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "$ %,.2f".format(compra.total * animTotal),
                fontSize      = 42.sp,
                fontWeight    = FontWeight.ExtraBold,
                color         = Color.White,
                letterSpacing = (-1.0).sp
            )

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
            Spacer(Modifier.height(16.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                HeroStat("PRODUCTOS",  "${compra.productos.size}")
                HeroStat("PROMEDIO",
                    "$ %,.0f".format(
                        if (compra.productos.isEmpty()) 0.0
                        else compra.total / compra.productos.size
                    )
                )
                HeroStat("IVA EST.", "$ %,.0f".format(compra.total * 0.21))
            }
        }
    }
}

@Composable
internal fun HeroStat(label: String, value: String) {
    Column {
        Text(
            label,
            style         = MaterialTheme.typography.labelSmall,
            color         = Color.White.copy(0.55f),
            letterSpacing = 0.8.sp
        )
        Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
    }
}
