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
    surfaceContainerLowest = StitchSurfaceContainerLowest,
    surfaceContainerLow    = StitchSurfaceContainerLow,
    surfaceContainer       = StitchSurfaceContainer,
    surfaceContainerHigh   = StitchSurfaceContainerHigh,
    surfaceContainerHighest= StitchSurfaceContainerHighest,
    outline                = StitchOutline,
    outlineVariant         = StitchOutlineVariant,
    inversePrimary         = StitchInversePrimary,
    inverseSurface         = StitchInverseSurface,
    inverseOnSurface       = StitchInverseOnSurface,
)

private val DarkColors = darkColorScheme(
    primary                = DarkPrimary,
    onPrimary              = DarkOnPrimary,
    primaryContainer       = DarkPrimaryContainer,
    onPrimaryContainer     = DarkOnPrimaryContainer,
    secondary              = DarkSecondary,
    onSecondary            = DarkOnSecondary,
    secondaryContainer     = DarkSecondaryContainer,
    onSecondaryContainer   = DarkOnSecondaryContainer,
    tertiary               = DarkTertiary,
    onTertiary             = DarkOnTertiary,
    tertiaryContainer      = DarkTertiaryContainer,
    onTertiaryContainer    = DarkOnTertiaryContainer,
    error                  = DarkError,
    onError                = DarkOnError,
    errorContainer         = DarkErrorContainer,
    onErrorContainer       = DarkOnErrorContainer,
    background             = DarkBackground,
    onBackground           = DarkOnBackground,
    surface                = DarkSurface,
    onSurface              = DarkOnSurface,
    surfaceVariant         = DarkSurfaceVariant,
    onSurfaceVariant       = DarkOnSurfaceVariant,
    surfaceContainerLowest = DarkSurfaceContainerLowest,
    surfaceContainerLow    = DarkSurfaceContainerLow,
    surfaceContainer       = DarkSurfaceContainer,
    surfaceContainerHigh   = DarkSurfaceContainerHigh,
    surfaceContainerHighest= DarkSurfaceContainerHighest,
    outline                = DarkOutline,
    outlineVariant         = DarkOutlineVariant,
    inversePrimary         = DarkInversePrimary,
    inverseSurface         = DarkInverseSurface,
    inverseOnSurface       = DarkInverseOnSurface,
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