package com.undef.superahorro.BossioCorrea.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.undef.superahorro.BossioCorrea.R
import com.undef.superahorro.BossioCorrea.ui.components.LabelCaps
import com.undef.superahorro.BossioCorrea.ui.components.StitchTopBar
import com.undef.superahorro.BossioCorrea.ui.theme.SuperAhorroTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick : () -> Unit = {}
) {
    var notificaciones by remember { mutableStateOf(true) }
    var modoOscuro     by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { StitchTopBar(stringResource(R.string.settings_titulo), onBackClick) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            Text(stringResource(R.string.settings_titulo),
                fontSize = 36.sp, fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = (-0.72).sp)
            Text("Personalizá tu experiencia",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline)

            // ── Sección Preferencias ───────────────────────────────────────
            SettingsSection(title = "PREFERENCIAS") {
                ToggleSettingRow(
                    icon    = Icons.Outlined.Notifications,
                    title   = stringResource(R.string.settings_notificaciones),
                    desc    = stringResource(R.string.settings_notificaciones_desc),
                    checked = notificaciones,
                    onToggle = { notificaciones = it }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(0.3f))
                ToggleSettingRow(
                    icon    = Icons.Outlined.DarkMode,
                    title   = stringResource(R.string.settings_tema_oscuro),
                    desc    = stringResource(R.string.settings_tema_oscuro_desc),
                    checked = modoOscuro,
                    onToggle = { modoOscuro = it }
                )
            }

            // ── Sección Cuenta ─────────────────────────────────────────────
            SettingsSection(title = "CUENTA") {
                ActionSettingRow(Icons.Outlined.Language, "Idioma", "Español (Argentina)")
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(0.3f))
                ActionSettingRow(Icons.Outlined.Security, "Privacidad y seguridad", "")
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(0.3f))
                ActionSettingRow(Icons.Outlined.FileDownload, "Exportar datos", "CSV / PDF")
            }

            // ── Sección Acerca de ──────────────────────────────────────────
            SettingsSection(title = "ACERCA DE") {
                InfoRow(Icons.Outlined.Info, stringResource(R.string.settings_version), "1.0.0")
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(0.3f))
                InfoRow(Icons.Outlined.Code, stringResource(R.string.settings_paquete), "com.undef.superahorro")
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(0.3f))
                InfoRow(Icons.Outlined.Group, "Equipo", "BossioCorrea · 2026")
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        LabelCaps(title)
        Spacer(Modifier.height(8.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(16.dp),
            color    = Color(0xFFFFFFFF),
            shadowElevation = 1.dp,
            border   = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBBCABF).copy(alpha = 0.3f))
            )
        ) {
            Column { content() }
        }
    }
}

@Composable
private fun ToggleSettingRow(
    icon: ImageVector, title: String, desc: String, checked: Boolean, onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape    = RoundedCornerShape(10.dp),
                color    = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
            }
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                if (desc.isNotBlank()) Text(desc, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline)
            }
        }
        Switch(
            checked = checked, onCheckedChange = onToggle,
            colors  = SwitchDefaults.colors(
                checkedThumbColor  = Color.White,
                checkedTrackColor  = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun ActionSettingRow(icon: ImageVector, title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(Modifier.size(36.dp), RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primary.copy(0.10f)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
            }
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (value.isNotBlank()) Text(value, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline)
            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(Modifier.size(36.dp), RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(0.5f)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(20.dp))
                }
            }
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
        Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SettingsPreview() { SuperAhorroTheme { SettingsScreen() } }
