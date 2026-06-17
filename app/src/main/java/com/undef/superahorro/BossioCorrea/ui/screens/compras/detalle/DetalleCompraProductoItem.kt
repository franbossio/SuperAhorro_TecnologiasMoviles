package com.undef.superahorro.BossioCorrea.ui.screens.compras.detalle

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.undef.superahorro.BossioCorrea.domain.model.Producto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProductoSwipeable(
    producto   : Producto,
    index      : Int,
    onEliminar : () -> Unit
) {
    val swipeState = rememberSwipeToDismissBoxState(
        confirmValueChange  = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) { onEliminar(); false } else false
        },
        positionalThreshold = { it * 0.40f }
    )

    SwipeToDismissBox(
        state                       = swipeState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent           = { ProductoSwipeBg(swipeState) },
        content                     = { ProductoRow(producto, index) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductoSwipeBg(state: SwipeToDismissBoxState) {
    val activo = state.dismissDirection == SwipeToDismissBoxValue.EndToStart
    val bg by animateColorAsState(
        targetValue   = if (activo) MaterialTheme.colorScheme.errorContainer
                        else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0f),
        animationSpec = tween(200),
        label         = "swipeBg"
    )
    val iconScale by animateFloatAsState(
        targetValue = if (activo) 1.15f else 0.85f,
        label       = "swipeScale"
    )
    Box(
        modifier         = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .padding(end = 20.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier            = Modifier.scale(iconScale)
        ) {
            Icon(
                Icons.Default.Delete, null,
                tint     = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                "Eliminar",
                color      = MaterialTheme.colorScheme.error,
                style      = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ProductoRow(p: Producto, index: Int) {
    Surface(
        modifier        = Modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(12.dp),
        color           = MaterialTheme.colorScheme.surfaceContainerLow,
        shadowElevation = 1.dp,
        border          = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(Color(0xFFBBCABF).copy(alpha = 0.3f))
        )
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape    = CircleShape,
                color    = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        "$index",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize   = 14.sp,
                        color      = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    p.nombre,
                    style      = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onSurface,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            "# ${p.codigo}",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style    = MaterialTheme.typography.labelSmall,
                            color    = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                    ) {
                        Text(
                            "×${p.cantidad}",
                            modifier   = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style      = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color      = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                if (p.descripcion.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        p.descripcion,
                        style    = MaterialTheme.typography.bodySmall,
                        color    = MaterialTheme.colorScheme.outline,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
            ) {
                Text(
                    "$ %,.2f".format(p.subtotal),
                    modifier   = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    style      = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
