package com.se3092.pulsefinance.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Illustration that gently bobs up and down; used for onboarding and empty states. */
@Composable
fun FloatingIllustration(
    @DrawableRes resId: Int,
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
    floatDistance: Dp = 6.dp,
    contentDescription: String? = null
) {
    val transition = rememberInfiniteTransition(label = "floatingIllustration")
    val progress by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatOffset"
    )

    Image(
        painter = painterResource(id = resId),
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .size(size)
            // graphicsLayer keeps the animation in the draw phase: no recomposition per frame
            .graphicsLayer { translationY = progress * floatDistance.toPx() }
    )
}
