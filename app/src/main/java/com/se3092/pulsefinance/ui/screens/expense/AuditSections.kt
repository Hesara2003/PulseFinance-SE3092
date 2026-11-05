package com.se3092.pulsefinance.ui.screens.expense

import androidx.compose.ui.graphics.vector.ImageVector
import com.se3092.pulsefinance.ui.theme.pulseColors
import com.se3092.pulsefinance.ui.components.ProfileAvatar
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import com.se3092.pulsefinance.R
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.se3092.pulsefinance.ui.common.lkr

// Sections of the subscription audit screen (ExpenseScreen), one composable each.

/** Logo, app name and the user's avatar. */
@Composable
internal fun AuditTopBar(initials: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // PulseFinance Emblem & Brand Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.logo_mark),
                contentDescription = "PulseFinance logo",
                modifier = Modifier.size(36.dp)
            )
            Text(
                text = "PulseFinance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp,
                letterSpacing = (-0.2).sp
            )
        }

        // Profile Avatar Circle
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainer),
            contentAlignment = Alignment.Center
        ) {
            ProfileAvatar(initials = initials, size = 36.dp)
        }
    }
}

/** "Zombie Subscriptions" heading with the audit glyph. */
@Composable
internal fun AuditTitle() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "AUDIT INTELLIGENCE",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "Zombie Subscriptions",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 23.sp,
                letterSpacing = (-0.3).sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "Recurring auto-debits you've logged",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }

        // Auto Mode Glyph Container
        Surface(
            modifier = Modifier.size(42.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            shadowElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.AutoMode,
                    contentDescription = "Audit Engine",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/** Red alert: how many subscriptions need a decision and what they still cost each month. */
@Composable
internal fun LeakAlertCard(flaggedCount: Int, flaggedMonthlyLkr: Double) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.pulseColors.dangerContainer,
        border = BorderStroke(1.dp, MaterialTheme.pulseColors.dangerBorder),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Pulsing Warning Icon Container
            val infiniteTransition = rememberInfiniteTransition(label = "pingWarning")
            val pingScale by infiniteTransition.animateFloat(
                initialValue = 1.0f,
                targetValue = 1.35f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "scale"
            )

            Box(modifier = Modifier.size(38.dp)) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(MaterialTheme.pulseColors.danger),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = MaterialTheme.pulseColors.onDanger,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .scale(pingScale)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(MaterialTheme.pulseColors.danger)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${flaggedCount} subscription${if (flaggedCount > 1) "s" else ""} to review",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.pulseColors.dangerStrong,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = buildAnnotatedString {
                        append("Still charging ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.pulseColors.dangerStrong)) {
                            append("${lkr(flaggedMonthlyLkr)} / month")
                        }
                        append(". Keep the ones you use; cancel the rest with the provider.")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.pulseColors.dangerStrong,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

/** Annual drain and the share of the goal it represents, side by side. */
@Composable
internal fun AuditMetrics(flaggedAnnualLkr: Double, shareOfGoalRemainingPct: Double?, goalTitle: String?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AuditMetricCard(
            icon = Icons.AutoMirrored.Filled.TrendingDown,
            iconTint = MaterialTheme.pulseColors.danger,
            iconBackground = MaterialTheme.pulseColors.dangerContainer,
            label = "ANNUAL DRAIN",
            value = lkr(flaggedAnnualLkr),
            valueColor = MaterialTheme.colorScheme.onSurface,
            caption = "Per year if left running",
            modifier = Modifier.weight(1f)
        )
        AuditMetricCard(
            icon = Icons.Default.RocketLaunch,
            iconTint = MaterialTheme.colorScheme.primary,
            iconBackground = MaterialTheme.pulseColors.successContainer,
            label = "POTENTIAL SAVINGS",
            value = shareOfGoalRemainingPct?.let { "%.1f%%".format(it) } ?: "—",
            valueColor = MaterialTheme.colorScheme.primary,
            caption = goalTitle?.let { "of what's left for $it" } ?: "Set a goal to compare",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AuditMetricCard(
    icon: ImageVector,
    iconTint: Color,
    iconBackground: Color,
    label: String,
    value: String,
    valueColor: Color,
    caption: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = MaterialTheme.pulseColors.shadow.copy(alpha = 0.02f),
                spotColor = MaterialTheme.pulseColors.shadow.copy(alpha = 0.02f)
            ),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(iconBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
                }
                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.6.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = valueColor,
                fontSize = 20.sp,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = caption,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}

/** "Detected Subscriptions" with the "Flagged first" sort chip. */
@Composable
internal fun DetectedSubscriptionsHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Detected Subscriptions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 18.sp
        )

        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Text(
                text = "Flagged first",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}

/** One account's heading in the grouped list, with the auto-debit pill for the secondary account (FR-SUB-6). */
@Composable
internal fun AccountGroupHeader(accountName: String, isSecondaryAccount: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.pulseColors.successContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(15.dp)
                )
            }
            Text(
                text = accountName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        if (isSecondaryAccount) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.pulseColors.successContainer,
                border = BorderStroke(1.dp, MaterialTheme.pulseColors.successBorder)
            ) {
                Text(
                    text = "⚡ Secondary Account (Auto-Debit)",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.pulseColors.success,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

/** Cancel every flagged subscription at once, or "Every subscription reviewed" when none are left. */
@Composable
internal fun CancelAllFlaggedButton(flaggedCount: Int, flaggedMonthlyLkr: Double, onClick: () -> Unit) {
    val allClear = flaggedCount == 0
    Button(
        onClick = onClick,
        enabled = !allClear,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                spotColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.19f)
            ),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (allClear) MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.inverseSurface,
            contentColor = MaterialTheme.colorScheme.inverseOnSurface,
            disabledContainerColor = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.4f),
            disabledContentColor = MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.7f)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (allClear) {
                Icon(
                    imageVector = Icons.Default.DoneAll,
                    contentDescription = null,
                    tint = MaterialTheme.pulseColors.primaryLight,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Every subscription reviewed",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = MaterialTheme.pulseColors.primaryLight,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = buildAnnotatedString {
                        append("Cancel ${flaggedCount} flagged ")
                        withStyle(SpanStyle(color = MaterialTheme.pulseColors.primaryLight)) {
                            append("(save ${lkr(flaggedMonthlyLkr)}/mo)")
                        }
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
