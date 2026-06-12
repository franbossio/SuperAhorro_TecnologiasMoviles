package com.undef.superahorro.BossioCorrea.ui.screens.settings

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.undef.superahorro.BossioCorrea.R
import com.undef.superahorro.BossioCorrea.data.local.NotificationsPreferences
import com.undef.superahorro.BossioCorrea.ui.components.LabelCaps
import com.undef.superahorro.BossioCorrea.ui.components.StitchTopBar
import com.undef.superahorro.BossioCorrea.ui.theme.SuperAhorroTheme
import com.undef.superahorro.BossioCorrea.ui.theme.LanguageViewModel
import com.undef.superahorro.BossioCorrea.ui.theme.ThemeViewModel
import com.undef.superahorro.BossioCorrea.util.exportarComprasCsv
import com.undef.superahorro.BossioCorrea.util.exportarComprasPdf
import kotlinx.coroutines.launch

// Opciones del selector de tema
private enum class ThemeMode { LIGHT, DARK, SYSTEM }

// Opciones del selector de idioma
private enum class LangMode { ES, EN }

// Formatos disponibles para exportar el historial de compras
private enum class FormatoExportacion { CSV, PDF }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    themeViewModel    : ThemeViewModel,
    languageViewModel : LanguageViewModel,
    vm                : SettingsViewModel = viewModel(),
    onBackClick       : () -> Unit = {}
) {
    val isDark          by themeViewModel.isDarkMode.collectAsStateWithLifecycle()
    val useSystemTheme  by themeViewModel.useSystemTheme.collectAsStateWithLifecycle()
    val languageCode    by languageViewModel.languageCode.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val scope    = rememberCoroutineScope()

    val notificationsPrefs = remember { NotificationsPreferences(context) }
    val notificaciones by notificationsPrefs.habilitadas.collectAsStateWithLifecycle(initialValue = true)

    var mostrarDialogoExportar by remember { mutableStateOf(false) }
    var exportando by remember { mutableStateOf(false) }

    fun exportar(formato: FormatoExportacion) {
        scope.launch {
            exportando = true
            val compras = vm.obtenerCompras()
            if (compras.isEmpty()) {
                Toast.makeText(context, context.getString(R.string.settings_exportar_sin_datos), Toast.LENGTH_SHORT).show()
            } else {
                val uri = when (formato) {
                    FormatoExportacion.CSV -> exportarComprasCsv(context, compras)
                    FormatoExportacion.PDF -> exportarComprasPdf(context, compras)
                }
                val mimeType = when (formato) {
                    FormatoExportacion.CSV -> "text/csv"
                    FormatoExportacion.PDF -> "application/pdf"
                }
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = mimeType
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(
                    Intent.createChooser(intent, context.getString(R.string.settings_exportar_compartir))
                )
                mostrarDialogoExportar = false
            }
            exportando = false
        }
    }

    val langMode = if (languageCode == "en") LangMode.EN else LangMode.ES

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
                    onToggle = { habilitado ->
                        scope.launch { notificationsPrefs.setHabilitadas(habilitado) }
                    }
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
                LanguageSelectorRow(
                    selected = langMode,
                    onSelect = { mode ->
                        languageViewModel.setLanguage(if (mode == LangMode.EN) "en" else "es")
                    }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(0.3f))
                ActionSettingRow(Icons.Outlined.Security, stringResource(R.string.settings_privacidad), "")
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(0.3f))
                ActionSettingRow(
                    icon    = Icons.Outlined.FileDownload,
                    title   = stringResource(R.string.settings_exportar),
                    value   = stringResource(R.string.settings_exportar_valor),
                    onClick = { mostrarDialogoExportar = true }
                )
            }

            // ── Sección Acerca de ──────────────────────────────────────────
            SettingsSection(title = "ACERCA DE") {
                InfoRow(Icons.Outlined.Info, stringResource(R.string.settings_version), "1.0.0")
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(0.3f))
                InfoRow(Icons.Outlined.Code, stringResource(R.string.settings_paquete), "com.undef.superahorro")
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(0.3f))
                InfoRow(Icons.Outlined.Group, stringResource(R.string.settings_equipo), "BossioCorrea · 2026")
            }

            Spacer(Modifier.height(32.dp))
        }
    }

    if (mostrarDialogoExportar) {
        AlertDialog(
            onDismissRequest = { if (!exportando) mostrarDialogoExportar = false },
            title = {
                Text(
                    stringResource(R.string.settings_exportar_dialog_titulo),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        stringResource(R.string.settings_exportar_dialog_mensaje),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (exportando) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick  = { exportar(FormatoExportacion.CSV) },
                        enabled  = !exportando,
                        shape    = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Outlined.TableChart, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.settings_exportar_csv))
                    }
                    Button(
                        onClick  = { exportar(FormatoExportacion.PDF) },
                        enabled  = !exportando,
                        shape    = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Outlined.PictureAsPdf, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.settings_exportar_pdf))
                    }
                    OutlinedButton(
                        onClick  = { mostrarDialogoExportar = false },
                        enabled  = !exportando,
                        shape    = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(stringResource(R.string.cancelar)) }
                }
            },
            shape          = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
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

// ── Selector de idioma ───────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageSelectorRow(
    selected : LangMode,
    onSelect : (LangMode) -> Unit
) {
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
                        Icons.Outlined.Language, null,
                        tint     = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column {
                Text(
                    stringResource(R.string.settings_idioma),
                    style      = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    stringResource(R.string.settings_idioma_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        val options = listOf(
            LangMode.ES to stringResource(R.string.settings_idioma_espanol),
            LangMode.EN to stringResource(R.string.settings_idioma_ingles)
        )

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, (mode, label) ->
                SegmentedButton(
                    shape    = SegmentedButtonDefaults.itemShape(index, options.size),
                    onClick  = { onSelect(mode) },
                    selected = selected == mode,
                    colors   = SegmentedButtonDefaults.colors(
                        activeContainerColor   = MaterialTheme.colorScheme.primary,
                        activeContentColor     = Color.White,
                        inactiveContainerColor = MaterialTheme.colorScheme.surface,
                        inactiveContentColor   = MaterialTheme.colorScheme.onSurface
                    ),
                    icon = {}
                ) {
                    Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium)
                }
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
private fun ActionSettingRow(icon: ImageVector, title: String, value: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 14.dp),
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

