package com.se3092.pulsefinance.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.se3092.pulsefinance.R

// Both are variable fonts bundled in res/font (SIL Open Font License), so they work offline.
@OptIn(ExperimentalTextApi::class)
private fun variable(resId: Int, weight: FontWeight) = Font(
    resId = resId,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight))
)

/** Headlines and section titles: a grotesque with some personality. */
val Display = FontFamily(
    variable(R.font.bricolage_grotesque, FontWeight.Medium),
    variable(R.font.bricolage_grotesque, FontWeight.SemiBold),
    variable(R.font.bricolage_grotesque, FontWeight.Bold),
    variable(R.font.bricolage_grotesque, FontWeight.ExtraBold)
)

/** Money figures: tabular-feeling geometric numerals. */
val Numeric = FontFamily(
    variable(R.font.space_grotesk, FontWeight.Medium),
    variable(R.font.space_grotesk, FontWeight.SemiBold),
    variable(R.font.space_grotesk, FontWeight.Bold)
)

val Typography = Typography(
    displayLarge = TextStyle(fontFamily = Numeric, fontWeight = FontWeight.Bold, fontSize = 44.sp, lineHeight = 48.sp, letterSpacing = (-1).sp),
    displayMedium = TextStyle(fontFamily = Numeric, fontWeight = FontWeight.Bold, fontSize = 34.sp, lineHeight = 40.sp, letterSpacing = (-0.5).sp),
    headlineLarge = TextStyle(fontFamily = Display, fontWeight = FontWeight.ExtraBold, fontSize = 30.sp, lineHeight = 36.sp, letterSpacing = (-0.5).sp),
    headlineMedium = TextStyle(fontFamily = Display, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 30.sp),
    headlineSmall = TextStyle(fontFamily = Display, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp),
    titleLarge = TextStyle(fontFamily = Display, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp),
    titleMedium = TextStyle(fontFamily = Display, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
    titleSmall = TextStyle(fontFamily = Display, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontFamily = Display, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    labelSmall = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp)
)
