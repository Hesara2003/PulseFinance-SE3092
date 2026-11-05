package com.se3092.pulsefinance.ui.components

import com.se3092.pulsefinance.ui.theme.pulseColors
import androidx.compose.material3.MaterialTheme
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

enum class NoticeTone { SUCCESS, ERROR }

/**
 * A short confirmation or error shown by [PulseNoticeHost]. [illustration] shows what the
 * notice is about (the coffee cup for a logged coffee); without one the tone's glyph is used.
 */
data class UiNotice(
    val title: String,
    val detail: String? = null,
    val tone: NoticeTone = NoticeTone.SUCCESS,
    @DrawableRes val illustration: Int? = null
)

/**
 * The app's single on-theme pop-up: a white card that drops in below the status bar, then
 * dismisses itself (errors stay a little longer). Tapping it dismisses it at once.
 * Place it last in a full-screen Box so it floats above the content.
 */
@Composable
fun PulseNoticeHost(
    notice: UiNotice?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Keep the last notice while the exit animation runs, so the card doesn't empty first.
    var shown by remember { mutableStateOf(notice) }
    if (notice != null) shown = notice

    LaunchedEffect(notice) {
        if (notice != null) {
            delay(if (notice.tone == NoticeTone.ERROR) 4_000 else 2_600)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = notice != null,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -it / 2 }) + scaleIn(initialScale = 0.96f),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -it / 2 }),
        modifier = modifier
            .statusBarsPadding()
            .padding(top = 12.dp, start = 16.dp, end = 16.dp)
    ) {
        shown?.let { NoticeCard(it, onDismiss) }
    }
}

@Composable
private fun NoticeCard(notice: UiNotice, onDismiss: () -> Unit) {
    val isError = notice.tone == NoticeTone.ERROR
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (isError) MaterialTheme.colorScheme.error.copy(alpha = 0.35f) else MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .widthIn(max = 480.dp)
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(20.dp), ambientColor = MaterialTheme.pulseColors.shadow.copy(alpha = 0.16f), spotColor = MaterialTheme.pulseColors.shadow.copy(alpha = 0.16f))
            .clickable(onClick = onDismiss)
            .semantics { liveRegion = LiveRegionMode.Polite }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        when {
                            notice.illustration != null -> MaterialTheme.colorScheme.surfaceContainerLow
                            isError -> MaterialTheme.colorScheme.errorContainer
                            else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.16f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (notice.illustration != null) {
                    Image(
                        painter = painterResource(notice.illustration),
                        contentDescription = null,
                        modifier = Modifier.size(36.dp)
                    )
                } else {
                    Icon(
                        imageVector = if (isError) Icons.Rounded.ErrorOutline else Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notice.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                notice.detail?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
