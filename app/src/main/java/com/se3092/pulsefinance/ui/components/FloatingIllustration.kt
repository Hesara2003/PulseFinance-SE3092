package com.se3092.pulsefinance.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Illustration bobbing gently over a soft green glow — the onboarding hero. */
@Composable
fun FloatingIllustration(
    @DrawableRes imageRes: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
    imageSize: Dp = 300.dp
) {
    val float = rememberInfiniteTransition(label = "illustrationFloat")
    val floatOffset by float.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(imageSize),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(imageSize - 20.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.18f), Color.Transparent)))
        )
        Image(
            painter = painterResource(imageRes),
            contentDescription = contentDescription,
            modifier = Modifier
                .size(imageSize)
                .offset(y = floatOffset.dp)
        )
    }
}
