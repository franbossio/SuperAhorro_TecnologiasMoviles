package com.undef.superahorro.BossioCorrea.ui.screens.compras.detalle

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.io.File

@Composable
internal fun TicketImageCard(imagePath: String) {
    var expandido by remember { mutableStateOf(false) }

    Surface(
        modifier        = Modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(16.dp),
        color           = MaterialTheme.colorScheme.surfaceContainerLow,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier            = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                "TICKET",
                style         = MaterialTheme.typography.labelSmall,
                fontWeight    = FontWeight.Bold,
                color         = MaterialTheme.colorScheme.outline,
                letterSpacing = 1.5.sp
            )
            AsyncImage(
                model              = File(imagePath),
                contentDescription = "Ticket de compra",
                modifier           = Modifier
                    .fillMaxWidth()
                    .height(if (expandido) 600.dp else 220.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { expandido = !expandido },
                contentScale       = if (expandido) ContentScale.Fit else ContentScale.Crop
            )
            Text(
                if (expandido) "Tocá para comprimir" else "Tocá para ver completo",
                style    = MaterialTheme.typography.labelSmall,
                color    = MaterialTheme.colorScheme.outline,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
