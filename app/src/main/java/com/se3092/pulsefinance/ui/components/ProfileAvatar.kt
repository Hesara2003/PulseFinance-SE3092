package com.se3092.pulsefinance.ui.components

import com.se3092.pulsefinance.ui.theme.pulseColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.se3092.pulsefinance.ui.theme.Display

/**
 * The signed-in user's profile photo URL (from Google sign-in), provided once at the root so
 * every avatar shows it without each screen passing it down. Null for email accounts.
 */
val LocalProfilePhotoUrl = staticCompositionLocalOf<String?> { null }

/**
 * The user's avatar. With a Google account it shows the Google profile photo; otherwise, and
 * while the photo loads or if it can't load (offline), a mark generated from the initials:
 * a saffron disc with "lotus" petals whose rotation is derived from the initials, so every
 * user gets a stable, distinct mark.
 */
@Composable
fun ProfileAvatar(
    initials: String,
    size: Dp = 44.dp,
    modifier: Modifier = Modifier,
    photoUrl: String? = LocalProfilePhotoUrl.current
) {
    val seed = initials.fold(7) { acc, c -> acc * 31 + c.code }
    val rotation = (seed % 360).toFloat()
    // Read here: Canvas drawing is not @Composable.
    val scheme = MaterialTheme.colorScheme
    val petalLight = MaterialTheme.pulseColors.primaryLight
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(Brush.linearGradient(listOf(petalLight, scheme.primaryContainer)))
            val r = this.size.minDimension / 2
            // Six petal arcs around the centre
            for (i in 0 until 6) {
                val angle = Math.toRadians((rotation + i * 60).toDouble())
                val c = Offset(
                    center.x + (r * 0.62f * kotlin.math.cos(angle)).toFloat(),
                    center.y + (r * 0.62f * kotlin.math.sin(angle)).toFloat()
                )
                drawCircle(color = scheme.onSurface.copy(alpha = 0.10f), radius = r * 0.42f, center = c)
            }
            drawCircle(color = scheme.primary.copy(alpha = 0.9f), radius = r * 0.96f, style = Stroke(width = r * 0.08f))
        }
        if (initials.isNotBlank()) Text(
            text = initials.take(2),
            fontFamily = Display,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = (size.value * 0.36f).sp
        )
        if (!photoUrl.isNullOrBlank()) {
            val sizePx = with(LocalDensity.current) { size.roundToPx() }
            // Drawn over the generated mark: if loading fails the mark simply stays visible.
            AsyncImage(
                model = googlePhotoAtSize(photoUrl, sizePx),
                contentDescription = "Profile photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().clip(CircleShape)
            )
        }
    }
}

/** Google photo URLs end in "=s96-c"; ask for one sharp at this size instead of the 96px default. */
internal fun googlePhotoAtSize(url: String, sizePx: Int): String =
    if (url.contains("googleusercontent.com")) url.replace(Regex("=s\\d+(-c)?$"), "") + "=s${sizePx.coerceAtLeast(96)}-c"
    else url
