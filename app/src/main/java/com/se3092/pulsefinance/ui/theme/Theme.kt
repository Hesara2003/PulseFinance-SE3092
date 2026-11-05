package com.se3092.pulsefinance.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * The brand palette (Color.kt) mapped onto every Material 3 colour role, so components and
 * screens read colours from MaterialTheme.colorScheme instead of hard-coding them.
 * System bars are drawn edge-to-edge (see MainActivity).
 */
private val PulseColorScheme = lightColorScheme(
    primary = NeoGreenDark,               // accent text, money in, links
    onPrimary = Color.White,
    primaryContainer = NeoGreen,          // filled buttons, selected chips, the hero card
    onPrimaryContainer = Color.White,
    inversePrimary = NeoGreenLight,
    secondary = NeoTextSecondary,
    onSecondary = Color.White,
    secondaryContainer = NeoSecondaryContainer,
    onSecondaryContainer = NeoOnSecondaryFixed,
    tertiary = NeoTextTertiary,
    onTertiary = Color.White,
    background = NeoSurface,              // the page behind the cards
    onBackground = NeoTextPrimary,
    surface = NeoCard,                    // cards and sheets
    onSurface = NeoTextPrimary,
    surfaceVariant = NeoCardLow,
    onSurfaceVariant = NeoTextSecondary,
    surfaceContainerLowest = NeoCard,
    surfaceContainerLow = NeoCardLow,     // icon tiles, inset panels
    surfaceContainer = NeoContainer,
    surfaceContainerHigh = NeoContainerHigh,
    surfaceContainerHighest = NeoSurfaceHighest,
    inverseSurface = NeoInverseSurface,
    inverseOnSurface = NeoInverseOnSurface,
    outline = NeoTextTertiary,
    outlineVariant = NeoBorder,           // card borders and dividers
    error = NeoRose,                      // attention: overdue, frozen, errors (deep amber)
    onError = Color.White,
    errorContainer = NeoErrorContainer,
    onErrorContainer = NeoRose,
    scrim = Color.Black
)

/**
 * Colours the app needs that Material 3 has no role for: the warning amber, status palettes for
 * the subscription audit, chart and category colours, light green tints and the shadow tint.
 * Read them as MaterialTheme.pulseColors.
 */
@Immutable
data class PulseColors(
    val textTertiary: Color = NeoTextTertiary,
    val textQuiet: Color = Dusk,
    val primaryLight: Color = NeoGreenLight,
    val primaryFixed: Color = NeoGreenFixed,
    val onPrimaryFixed: Color = NeoOnPrimaryFixed,
    val primaryGlow: Color = NeoGreenGlow,
    /** Darker end of the Safe-to-Spend hero gradient. */
    val primaryDeep: Color = Color(0xFF00A844),
    val warning: Color = NeoAmber,
    val track: Color = NeoTrack,
    // Status palettes (subscription audit: needs review, cancelled, kept)
    val danger: Color = Color(0xFFE11D48),
    val dangerStrong: Color = Color(0xFF9F1239),
    val dangerContainer: Color = Color(0xFFFFF1F2),
    val dangerBorder: Color = Color(0xFFFFCCD5),
    val onDanger: Color = Color.White,
    val success: Color = Color(0xFF047857),
    val successContainer: Color = Color(0xFFECFDF5),
    val successBorder: Color = Color(0xFFA7F3D0),
    val caution: Color = Color(0xFFC2410C),
    val cautionContainer: Color = Color(0xFFFFF7ED),
    val cautionBorder: Color = Color(0xFFFED7AA),
    // Category and chart colours
    val food: Color = FoodColor,
    val transport: Color = TransportColor,
    val rent: Color = RentColor,
    val violet: Color = VioletAccent,
    /** Slate tint for card shadows; apply an alpha with copy(alpha = …). */
    val shadow: Color = Color(0xFF0F172A)
)

private val LocalPulseColors = staticCompositionLocalOf { PulseColors() }

/** The app's extra colour roles, alongside MaterialTheme.colorScheme. */
val MaterialTheme.pulseColors: PulseColors
    @Composable
    @ReadOnlyComposable
    get() = LocalPulseColors.current

@Composable
fun PulseFinanceTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalPulseColors provides PulseColors()) {
        MaterialTheme(
            colorScheme = PulseColorScheme,
            typography = Typography,
            content = content
        )
    }
}
