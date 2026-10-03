package com.se3092.pulsefinance.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Vivid Neo-Fintech Light Color Scheme
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
    outline = NeoBorder,
    error = NeoRose,
    onError = Color.White
)

@Composable
fun PulseFinanceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = NeoFintechColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = NeoSurface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
