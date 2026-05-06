package com.undef.superahorro.BossioCorrea.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * TopAppBar con estilo Stitch:
 * fondo slate-50 (#F8FAFC), borde inferior suave, sombra mínima, título en verde primario.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StitchTopBar(
    title       : String,
    onBackClick : (() -> Unit)? = null,
    actions     : @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text       = title,
                fontWeight = FontWeight.Bold,
                fontSize   = 18.sp,
                color      = MaterialTheme.colorScheme.primary
            )
        },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector        = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint               = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        actions = actions,
        colors  = TopAppBarDefaults.topAppBarColors(
            containerColor         = Color(0xFFF8FAFC),   // slate-50
            scrolledContainerColor = Color(0xFFF8FAFC),
            titleContentColor      = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier
            .shadow(elevation = 2.dp, spotColor = Color(0x0D1E293B))
            .border(
                width = 0.5.dp,
                color = Color(0xFFE2E8F0),   // slate-200/50
                shape = RoundedCornerShape(0.dp)
            )
    )
}

/**
 * Card estilo Stitch: blanca, borde outline-variant/30, sombra [0_4px_20px_rgba(30,41,59,0.05)]
 */
@Composable
fun StitchCard(
    modifier  : Modifier = Modifier,
    content   : @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFFFF),   // surface-container-lowest
            contentColor   = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border    = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                Color(0xFFBBCABF).copy(alpha = 0.3f)
            )
        )
    ) {
        content()
    }
}

/** Chip/Badge de precio verde */
@Composable
fun PriceChip(text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape    = RoundedCornerShape(20.dp),
        color    = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
    ) {
        Text(
            text      = text,
            modifier  = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            color     = MaterialTheme.colorScheme.primary,
            style     = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

/** Label en mayúsculas estilo Stitch (label-caps) */
@Composable
fun LabelCaps(text: String, modifier: Modifier = Modifier) {
    Text(
        text      = text.uppercase(),
        modifier  = modifier,
        style     = MaterialTheme.typography.labelSmall,
        color     = MaterialTheme.colorScheme.outline,
        letterSpacing = 0.8.sp
    )
}
