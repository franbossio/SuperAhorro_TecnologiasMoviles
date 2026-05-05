package com.undef.superahorro.BossioCorrea.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// ─── Light color scheme ───────────────────────────────────────────────────────
private val LightColors = lightColorScheme(
    primary            = Verde40,
    onPrimary          = Neutral99,
    primaryContainer   = VerdeContainer90,
    onPrimaryContainer = Verde10,

    secondary          = NeutralVar30,
    onSecondary        = Neutral99,
    secondaryContainer = NeutralVar90,
    onSecondaryContainer = NeutralVar30,

    tertiary           = Amarillo40,
    onTertiary         = Neutral99,
    tertiaryContainer  = Amarillo90,
    onTertiaryContainer = Amarillo40,

    error              = Rojo40,
    onError            = Neutral99,
    errorContainer     = Rojo90,
    onErrorContainer   = Rojo40,

    background         = Neutral99,
    onBackground       = Neutral10,

    surface            = Neutral99,
    onSurface          = Neutral10,
    surfaceVariant     = NeutralVar90,
    onSurfaceVariant   = NeutralVar30,

    outline            = NeutralVar50,
)

// ─── Dark color scheme ────────────────────────────────────────────────────────
private val DarkColors = darkColorScheme(
    primary            = Verde80,
    onPrimary          = Verde20,
    primaryContainer   = Verde30,
    onPrimaryContainer = Verde90,

    secondary          = NeutralVar80,
    onSecondary        = NeutralVar30,
    secondaryContainer = NeutralVar30,
    onSecondaryContainer = NeutralVar90,

    tertiary           = Amarillo80,
    onTertiary         = Amarillo40,
    tertiaryContainer  = Amarillo40,
    onTertiaryContainer = Amarillo90,

    error              = Rojo80,
    onError            = Rojo40,
    errorContainer     = Rojo40,
    onErrorContainer   = Rojo90,

    background         = Neutral10,
    onBackground       = Neutral90,

    surface            = Neutral10,
    onSurface          = Neutral90,
    surfaceVariant     = NeutralVar30,
    onSurfaceVariant   = NeutralVar80,

    outline            = NeutralVar50,
)

@Composable
fun SuperAhorroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // NUNCA usamos dynamicColor para garantizar coherencia visual
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography  = Typography,
        content     = content
    )
}