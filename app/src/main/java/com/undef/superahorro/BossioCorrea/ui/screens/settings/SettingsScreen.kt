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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.undef.superahorro.BossioCorrea.R
import com.undef.superahorro.BossioCorrea.ui.components.LabelCaps
import com.undef.superahorro.BossioCorrea.ui.components.StitchTopBar
import com.undef.superahorro.BossioCorrea.ui.theme.SuperAhorroTheme
import com.undef.superahorro.BossioCorrea.ui.theme.ThemeViewModel

// Opciones del selector de tema
private enum class ThemeMode { LIGHT, DARK, SYSTEM }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    themeViewModel : ThemeViewModel,
    onBackClick    : () -> Unit = {}
) {
    val isDark          by themeViewModel.isDarkMode.collectAsStateWithLifecycle()
    val useSystemTheme  by themeViewModel.useSystemTheme.collectAsStateWithLifecycle()
    var notificaciones by remember { mutableStateOf(true) }

    // Modo actual derivado del estado real del ViewModel
    // (SYSTEM no se persiste aún en este mock, pero el selector lo muestra)
    var themeMode by remember(isDark, useSystemTheme) {
        mutableStateOf(
            when {
                useSystemTheme -> ThemeMode.SYSTEM
                isDark         -> ThemeMode.DARK
                else           -> ThemeMode.LIGHT
            }
        )
    }

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

            Text(
                stringResource(R.string.settings_titulo),
                fontSize = 36.sp, fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = (-0.72).sp
            )
            Text(
                stringResource(R.string.settings_personalizar),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )

            // ── Sección Apariencia ─────────────────────────────────────────
            SettingsSection(title = stringResource(R.string.settings_apariencia)) {

                // Toggle de notificaciones
                ToggleSettingRow(
                    icon     = Icons.Outlined.Notifications,
                    title    = stringResource(R.string.settings_notificaciones),
                    desc     = stringResource(R.string.settings_notificaciones_desc),
                    checked  = notificaciones,
                    onToggle = { notificaciones = it }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(0.3f))

                // ── Selector de tema (3 opciones) ──────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape    = RoundedCornerShape(10.dp),
                            color    = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Outlined.DarkMode, null,
                                    tint     = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                stringResource(R.string.settings_tema_oscuro),
                                style      = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                stringResource(R.string.settings_tema_oscuro_desc),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    // Segmented button: Claro / Oscuro / Por defecto
                    ThemeModeSelector(
                        selected = themeMode,
                        onSelect = { mode ->
                            themeMode = mode
                            when (mode) {
                                ThemeMode.LIGHT  -> themeViewModel.setDarkMode(false)
                                ThemeMode.DARK   -> themeViewModel.setDarkMode(true)
                                // SYSTEM: respeta el sistema → usar isSystemInDarkTheme en el futuro
                                ThemeMode.SYSTEM -> themeViewModel.setSystemTheme()
                            }
                        }
                    )
                }
            }

            // ── Sección Cuenta ─────────────────────────────────────────────
            SettingsSection(title = stringResource(R.string.settings_cuenta).uppercase()) {
                ActionSettingRow(Icons.Outlined.Language, stringResource(R.string.settings_idioma), stringResource(R.string.settings_idioma_valor))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(0.3f))
                ActionSettingRow(Icons.Outlined.Security, stringResource(R.string.settings_privacidad), "")
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(0.3f))
                ActionSettingRow(Icons.Outlined.FileDownload, stringResource(R.string.settings_exportar), stringResource(R.string.settings_exportar_valor))
            }

            // ── Sección Acerca de ──────────────────────────────────────────
            SettingsSection(title = stringResource(R.string.settings_acerca_de).uppercase()) {
                InfoRow(Icons.Outlined.Info, stringResource(R.string.settings_version), stringResource(R.string.settings_version_valor))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(0.3f))
                InfoRow(Icons.Outlined.Code, stringResource(R.string.settings_paquete), stringResource(R.string.settings_paquete_valor))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(0.3f))
                InfoRow(Icons.Outlined.Group, stringResource(R.string.settings_equipo), stringResource(R.string.settings_equipo_valor))
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── Segmented button de tema ─────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeModeSelector(
    selected : ThemeMode,
    onSelect : (ThemeMode) -> Unit
) {
    val options = listOf(
        ThemeMode.LIGHT  to stringResource(R.string.settings_claro),
        ThemeMode.DARK   to stringResource(R.string.settings_oscuro),
        ThemeMode.SYSTEM to stringResource(R.string.settings_sistema)
    )

    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, (mode, label) ->
            SegmentedButton(
                shape    = SegmentedButtonDefaults.itemShape(index, options.size),
                onClick  = { onSelect(mode) },
                selected = selected == mode,
                colors   = SegmentedButtonDefaults.colors(
                    activeContainerColor  = MaterialTheme.colorScheme.primary,
                    activeContentColor    = Color.White,
                    inactiveContainerColor = MaterialTheme.colorScheme.surface,
                    inactiveContentColor  = MaterialTheme.colorScheme.onSurface
                ),
                icon = {}   // quitamos el check mark por defecto
            ) {
                Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium)
            }
        }
    }
}

// ── Componentes auxiliares (sin cambios) ─────────────────────────────────────

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        LabelCaps(title)
        Spacer(Modifier.height(8.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(16.dp),
            color    = MaterialTheme.colorScheme.surface,
            shadowElevation = 1.dp,
            border   = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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
                if (desc.isNotBlank()) Text(
                    desc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
        Switch(
            checked = checked, onCheckedChange = onToggle,
            colors  = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                Modifier.size(36.dp), RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primary.copy(0.10f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
            }
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (value.isNotBlank()) Text(
                value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
            Icon(
                Icons.Default.ChevronRight, null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp)
            )
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                Modifier.size(36.dp), RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(0.5f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(20.dp))
                }
            }
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
        Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
    }
}

