package com.undef.superahorro.BossioCorrea.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun KpiCard(
    modifier : Modifier,
    icon     : ImageVector,
    valor    : String,
    label    : String,
    iconBg   : Color,
    iconTint : Color
) {
    Surface(
        modifier        = modifier,
        shape           = RoundedCornerShape(14.dp),
        color           = MaterialTheme.colorScheme.surfaceContainerLow,
        shadowElevation = 1.dp,
        border          = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(MaterialTheme.colorScheme.outlineVariant.copy(.35f))
        )
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                modifier         = Modifier.size(32.dp).clip(RoundedCornerShape(9.dp)).background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(17.dp))
            }
            Text(valor, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface)
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline,
                fontWeight = FontWeight.Medium)
        }
    }
}
