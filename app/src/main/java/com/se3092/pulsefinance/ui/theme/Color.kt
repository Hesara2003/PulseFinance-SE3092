package com.se3092.pulsefinance.ui.theme

import androidx.compose.ui.graphics.Color

// The raw brand palette. Screens don't use these directly: Theme.kt maps them onto the
// Material 3 colour roles (MaterialTheme.colorScheme) and MaterialTheme.pulseColors.

// Vivid Neo-Fintech Design System Palette
val NeoGreen = Color(0xFF00C950)            // primary-container: Signal Emerald
val NeoGreenDark = Color(0xFF006E28)        // primary: Deep Trust Green
val NeoGreenLight = Color(0xFF3BE366)       // primary-fixed-dim
val NeoGreenFixed = Color(0xFF6BFF84)       // primary-fixed
val NeoGreenGlow = Color(0x3800C950)        // glow for shadow/elevation

// Canvas & Surfaces (Crisp Porcelain White)
val NeoSurface = Color(0xFFF7F9FB)          // surface / background
val NeoCard = Color(0xFFFFFFFF)             // surface-container-lowest (Pure White)
val NeoCardLow = Color(0xFFF2F4F6)          // surface-container-low
val NeoContainer = Color(0xFFECEEF0)        // surface-container
val NeoContainerHigh = Color(0xFFE6E8EA)    // surface-container-high
val NeoTrack = Color(0xFFE2E8F0)            // capsule chart empty track

// Typography & Text Layers (Deep Slate & Muted Slates)
val NeoTextPrimary = Color(0xFF191C1E)      // on-surface: High contrast charcoal
val NeoTextSecondary = Color(0xFF565E74)    // secondary: Balanced slate
val NeoTextTertiary = Color(0xFF595F66)     // tertiary: Subtle muted
val NeoSecondaryContainer = Color(0xFFDAE2FD) // secondary-container
val NeoOnSecondaryFixed = Color(0xFF131B2E)   // on-secondary-fixed
val NeoOnPrimaryFixed = Color(0xFF002107)     // on-primary-fixed
val NeoSurfaceHighest = Color(0xFFE0E3E5)     // surface-container-highest
val NeoInverseSurface = Color(0xFF2D3133)     // inverse-surface
val NeoInverseOnSurface = Color(0xFFEFF1F3)   // inverse-on-surface
val NeoBorder = Color(0xFFE2E8F0)           // card border / divider

// Status & Semantic Accents. Attention states use amber rather than red: Kavindu is
// avoidant about money, and alarm colours make avoiders look away.
val NeoAmber = Color(0xFFF59E0B)            // Warning / Overdue
val NeoRose = Color(0xFFB45309)             // attention text (deep amber, readable on white)
val NeoErrorContainer = Color(0xFFFEF3C7)   // attention container (pale amber)

// Category and chart colours (MaterialTheme.pulseColors.food / transport / rent / violet)
val FoodColor = Color(0xFFF97316)
val TransportColor = Color(0xFF3B82F6)
val RentColor = Color(0xFF8B5CF6)
val VioletAccent = Color(0xFF8B5CF6)

/** Quiet text (MaterialTheme.pulseColors.textQuiet). */
val Dusk = Color(0xFF8A93A3)
