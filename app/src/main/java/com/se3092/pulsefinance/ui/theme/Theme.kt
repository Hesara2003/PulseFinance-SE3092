package com.se3092.pulsefinance.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Vivid Neo-Fintech Light Color Scheme. System bars are drawn edge-to-edge (see MainActivity).
private val NeoFintechColorScheme = lightColorScheme(
    primary = NeoGreenDark,
    onPrimary = Color.White,
    primaryContainer = NeoGreen,
    onPrimaryContainer = Color.White,
    secondary = NeoTextSecondary,
    onSecondary = Color.White,
    tertiary = NeoTextTertiary,
    onTertiary = Color.White,
    background = NeoSurface,
    onBackground = NeoTextPrimary,
    surface = NeoSurface,
    onSurface = NeoTextPrimary,
    surfaceVariant = NeoCardLow,
    onSurfaceVariant = NeoTextSecondary,
    surfaceContainerLowest = NeoCard,
    surfaceContainerLow = NeoCard,
    surfaceContainer = NeoCard,
    surfaceContainerHigh = NeoCard,
    surfaceContainerHighest = NeoContainer,
    outline = NeoBorder,
    error = NeoRose,
    onError = Color.White
)

@Composable
fun PulseFinanceTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NeoFintechColorScheme,
        typography = Typography,
        content = content
    )
}
