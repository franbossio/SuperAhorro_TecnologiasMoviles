package com.undef.superahorro.BossioCorrea.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary                = StitchPrimary,
    onPrimary              = StitchOnPrimary,
    primaryContainer       = StitchPrimaryContainer,
    onPrimaryContainer     = StitchOnPrimaryContainer,
    secondary              = StitchSecondary,
    onSecondary            = StitchOnSecondary,
    secondaryContainer     = StitchSecondaryContainer,
    onSecondaryContainer   = StitchOnSecondaryContainer,
    tertiary               = StitchTertiary,
    onTertiary             = StitchOnTertiary,
    tertiaryContainer      = StitchTertiaryContainer,
    onTertiaryContainer    = StitchOnTertiaryContainer,
    error                  = StitchError,
    onError                = StitchOnError,
    errorContainer         = StitchErrorContainer,
    onErrorContainer       = StitchOnErrorContainer,
    background             = StitchBackground,
    onBackground           = StitchOnBackground,
    surface                = StitchSurface,
    onSurface              = StitchOnSurface,
    surfaceVariant         = StitchSurfaceVariant,
    onSurfaceVariant       = StitchOnSurfaceVariant,
    outline                = StitchOutline,
    outlineVariant         = StitchOutlineVariant,
    inversePrimary         = StitchInversePrimary,
    inverseSurface         = StitchInverseSurface,
    inverseOnSurface       = StitchInverseOnSurface,
)

private val DarkColors = darkColorScheme(
    primary                = StitchPrimaryDark,
    onPrimary              = StitchOnPrimaryDark,
    primaryContainer       = StitchPrimaryContainerDark,
    onPrimaryContainer     = StitchOnPrimaryContainerDark,
    background             = StitchBackgroundDark,
    onBackground           = StitchOnBackgroundDark,
    surface                = StitchSurfaceDark,
    onSurface              = StitchOnSurfaceDark,
    surfaceVariant         = StitchSurfaceVariantDark,
    onSurfaceVariant       = StitchOnSurfaceVariantDark,
    outline                = StitchOutlineDark,
)

@Composable
fun SuperAhorroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography  = Typography,
        content     = content
    )
}
