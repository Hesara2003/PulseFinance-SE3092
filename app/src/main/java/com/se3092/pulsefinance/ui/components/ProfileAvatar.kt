package com.se3092.pulsefinance.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.se3092.pulsefinance.ui.theme.Display
import com.se3092.pulsefinance.ui.theme.Ink
import com.se3092.pulsefinance.ui.theme.OnAccent
import com.se3092.pulsefinance.ui.theme.Saffron
import com.se3092.pulsefinance.ui.theme.SaffronSoft
import com.se3092.pulsefinance.ui.theme.Teal

/**
 * A personal avatar generated from the user's initials: a saffron disc with concentric
 * "lotus" petals whose rotation is derived from the initials, so every user gets a stable,
 * distinct mark without uploading a photo.
 */
@Composable
fun ProfileAvatar(initials: String, size: Dp = 44.dp, modifier: Modifier = Modifier) {
    val seed = initials.fold(7) { acc, c -> acc * 31 + c.code }
    val rotation = (seed % 360).toFloat()
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(Brush.linearGradient(listOf(SaffronSoft, Saffron)))
            val r = this.size.minDimension / 2
            // Six petal arcs around the centre
            for (i in 0 until 6) {
                val angle = Math.toRadians((rotation + i * 60).toDouble())
                val c = Offset(
                    center.x + (r * 0.62f * kotlin.math.cos(angle)).toFloat(),
                    center.y + (r * 0.62f * kotlin.math.sin(angle)).toFloat()
                )
                drawCircle(color = Ink.copy(alpha = 0.10f), radius = r * 0.42f, center = c)
            }
            drawCircle(color = Teal.copy(alpha = 0.9f), radius = r * 0.96f, style = Stroke(width = r * 0.08f))
        }
        if (initials.isNotBlank()) Text(
            text = initials.take(2),
            fontFamily = Display,
            fontWeight = FontWeight.ExtraBold,
            color = OnAccent,
            fontSize = (size.value * 0.36f).sp
        )
    }
}
