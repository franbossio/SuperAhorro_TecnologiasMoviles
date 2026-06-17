package com.undef.superahorro.BossioCorrea.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.undef.superahorro.BossioCorrea.R

@Composable
internal fun PerfilBottomSheetContent(
    nombre              : String,
    apellido            : String,
    email               : String,
    onVerPerfilClick    : () -> Unit,
    onEstadisticasClick : () -> Unit,
    onCerrarSesionClick : () -> Unit,
    onCerrarClick       : () -> Unit
) {
    val iniciales = "${nombre.firstOrNull() ?: ""}${apellido.firstOrNull() ?: ""}"

    Column(modifier = Modifier.fillMaxWidth().navigationBarsPadding()) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(HeroBgTop, HeroBgBottom))),
                contentAlignment = Alignment.Center
            ) {
                Text(iniciales, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "$nombre $apellido",
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onSurface
                )
                Text(email, fontSize = 13.sp, color = MaterialTheme.colorScheme.outline)
                Spacer(Modifier.height(5.dp))
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(.4f)
                ) {
                    Row(
                        modifier              = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Star, null, tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(12.dp))
                        Text(
                            stringResource(R.string.home_super_ahorrador),
                            fontSize   = 11.sp,
                            color      = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(.4f))

        SheetMenuItem(Icons.Default.Person,   stringResource(R.string.home_mi_perfil),        stringResource(R.string.home_mi_perfil_desc),        onVerPerfilClick)
        SheetMenuItem(Icons.Default.BarChart, stringResource(R.string.home_mis_estadisticas), stringResource(R.string.home_mis_estadisticas_desc), onEstadisticasClick)
        SheetMenuItem(Icons.Default.Logout,   stringResource(R.string.perfil_cerrar_sesion),  stringResource(R.string.home_cerrar_sesion_desc),    onCerrarSesionClick)

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(.4f))

        TextButton(
            onClick  = onCerrarClick,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).height(48.dp)
        ) {
            Text(
                stringResource(R.string.cerrar),
                fontSize   = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color      = MaterialTheme.colorScheme.outline
            )
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
internal fun SheetMenuItem(
    icon     : ImageVector,
    title    : String,
    subtitle : String,
    onClick  : () -> Unit
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(.35f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title,    fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
        }
        Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.size(20.dp))
    }
}
