package com.undef.superahorro.BossioCorrea.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.undef.superahorro.BossioCorrea.R
import com.undef.superahorro.BossioCorrea.ui.theme.SuperAhorroTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick : () -> Unit = {}
) {
    var notificaciones by remember { mutableStateOf(true) }
    var modoOscuro     by remember { mutableStateOf(false) }
    var moneda         by remember { mutableStateOf("ARS ($)") }
    var idioma         by remember { mutableStateOf("Español") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_titulo)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.volver))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor            = MaterialTheme.colorScheme.primary,
                    titleContentColor         = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── General ───────────────────────────────────────────────────
            SettingsSection(title = "General") {
                SettingsSwitchItem(
                    icon    = Icons.Default.Notifications,
                    title   = stringResource(R.string.settings_notificaciones),
                    subtitle = stringResource(R.string.settings_notificaciones_desc),
                    checked = notificaciones,
                    onCheckedChange = { notificaciones = it }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                SettingsSwitchItem(
                    icon     = Icons.Default.DarkMode,
                    title    = stringResource(R.string.settings_tema_oscuro),
                    subtitle = stringResource(R.string.settings_tema_oscuro_desc),
                    checked  = modoOscuro,
                    onCheckedChange = { modoOscuro = it }
                )
            }

            // ── Preferencias ──────────────────────────────────────────────
            SettingsSection(title = "Preferencias") {
                SettingsNavigationItem(
                    icon     = Icons.Default.AttachMoney,
                    title    = "Moneda",
                    subtitle = moneda,
                    onClick  = {}
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                SettingsNavigationItem(
                    icon     = Icons.Default.Language,
                    title    = "Idioma",
                    subtitle = idioma,
                    onClick  = {}
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                SettingsNavigationItem(
                    icon     = Icons.Default.Help,
                    title    = "Ayuda y Soporte",
                    subtitle = "Centro de ayuda",
                    onClick  = {}
                )
            }

            // ── Acerca de ─────────────────────────────────────────────────
            SettingsSection(title = "Acerca de") {
                SettingsInfoItem(
                    icon  = Icons.Default.Info,
                    title = stringResource(R.string.settings_version),
                    value = "1.0.0"
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                SettingsInfoItem(
                    icon  = Icons.Default.Code,
                    title = stringResource(R.string.settings_paquete),
                    value = "com.undef.superahorro"
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                SettingsInfoItem(
                    icon  = Icons.Default.School,
                    title = "Materia",
                    value = "Tecnologías Móviles 2026"
                )
            }

            // ── Zona peligrosa ────────────────────────────────────────────
            SettingsSection(title = "Datos") {
                SettingsNavigationItem(
                    icon     = Icons.Default.FileDownload,
                    title    = "Exportar mis datos",
                    subtitle = "Descargar historial en CSV",
                    onClick  = {}
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                SettingsNavigationItem(
                    icon     = Icons.Default.DeleteForever,
                    title    = "Eliminar todos los datos",
                    subtitle = "Esta acción no se puede deshacer",
                    onClick  = {},
                    contentColor = MaterialTheme.colorScheme.error
                )
            }

            // App info footer
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text  = "Super Ahorro — UNDEF 2026",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SettingsSection(
    title   : String,
    content : @Composable ColumnScope.() -> Unit
) {
    Text(
        text       = title,
        style      = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color      = MaterialTheme.colorScheme.primary
    )
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor   = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column { content() }
    }
}

@Composable
private fun SettingsSwitchItem(
    icon            : ImageVector,
    title           : String,
    subtitle        : String,
    checked         : Boolean,
    onCheckedChange : (Boolean) -> Unit
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier              = Modifier.weight(1f)
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingsNavigationItem(
    icon         : ImageVector,
    title        : String,
    subtitle     : String,
    onClick      : () -> Unit,
    contentColor : androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier              = Modifier.weight(1f)
        ) {
            Icon(icon, contentDescription = null, tint = if (contentColor == MaterialTheme.colorScheme.error) contentColor else MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge, color = contentColor)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SettingsInfoItem(
    icon  : ImageVector,
    title : String,
    value : String
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
            Text(title, style = MaterialTheme.typography.bodyLarge)
        }
        Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SettingsPreview() {
    SuperAhorroTheme { SettingsScreen() }
}
