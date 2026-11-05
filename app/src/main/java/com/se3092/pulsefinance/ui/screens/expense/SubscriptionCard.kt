package com.se3092.pulsefinance.ui.screens.expense

import com.se3092.pulsefinance.ui.theme.pulseColors
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.HistoryToggleOff
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.se3092.pulsefinance.domain.model.DetectedSubscription
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.SubscriptionRisk
import com.se3092.pulsefinance.domain.model.SubscriptionStatus
import com.se3092.pulsefinance.ui.common.formatDate
import com.se3092.pulsefinance.ui.common.lkr
import com.se3092.pulsefinance.ui.common.presetIllustration

/**
 * One detected subscription in the audit: what it is and its status, its charge history and app
 * activity (FR-SUB-5), the "did you use it" question (FR-SUB-4) and its cost with Keep / Cancel.
 */
@Composable
internal fun SubscriptionCard(
    sub: DetectedSubscription,
    onCancel: () -> Unit,
    onKeep: () -> Unit,
    onUsageAnswer: (Boolean) -> Unit
) {
    val isAlert = sub.isFlagged || sub.risk == SubscriptionRisk.HIGH
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = MaterialTheme.pulseColors.shadow.copy(alpha = 0.02f),
                spotColor = MaterialTheme.pulseColors.shadow.copy(alpha = 0.02f)
            )
            .animateContentSize(),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (isAlert) MaterialTheme.pulseColors.dangerBorder else MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            SubscriptionHeader(sub)
            Spacer(modifier = Modifier.height(14.dp))
            SubscriptionActivity(sub)
            if (sub.consecutiveUnusedMonths >= 2) {
                Spacer(modifier = Modifier.height(8.dp))
                UnusedMonthsWarning(months = sub.consecutiveUnusedMonths)
            }
            Spacer(modifier = Modifier.height(10.dp))
            UsageQuestion(onUsageAnswer)
            Spacer(modifier = Modifier.height(14.dp))
            DrainRateAndActions(sub, onKeep, onCancel)
        }
    }
}

/** How a status badge looks: its label and its palette. */
private data class StatusStyle(val label: String, val background: Color, val border: Color, val text: Color)

@Composable
private fun statusStyle(status: SubscriptionStatus): StatusStyle {
    val pulse = MaterialTheme.pulseColors
    val scheme = MaterialTheme.colorScheme
    return when (status) {
        SubscriptionStatus.NEEDS_REVIEW -> StatusStyle("Needs review", pulse.cautionContainer, pulse.cautionBorder, pulse.caution)
        SubscriptionStatus.STILL_CHARGING -> StatusStyle("Still charging", pulse.dangerContainer, pulse.dangerBorder, pulse.danger)
        SubscriptionStatus.KEPT -> StatusStyle("Active · Kept", pulse.successContainer, pulse.successBorder, pulse.success)
        SubscriptionStatus.CANCELLED -> StatusStyle("Cancelled", scheme.surfaceContainerLow, scheme.outlineVariant, scheme.onSurfaceVariant)
    }
}

/** Illustrated icon, name and account, with the status and High Risk badges. */
@Composable
private fun SubscriptionHeader(sub: DetectedSubscription) {
    val style = statusStyle(sub.status)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            // Illustrated Preset Icon
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLow),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(presetIllustration(sub.title, ExpenseCategory.SUBSCRIPTION)),
                    contentDescription = sub.title,
                    modifier = Modifier.size(34.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sub.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = sub.accountName.ifBlank { "Auto-debit" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (sub.risk == SubscriptionRisk.HIGH) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.pulseColors.dangerContainer,
                    border = BorderStroke(1.dp, MaterialTheme.pulseColors.dangerBorder)
                ) {
                    Text(
                        text = "High Risk ⚠️",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.pulseColors.danger,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            Surface(
                shape = CircleShape,
                color = style.background,
                border = BorderStroke(1.dp, style.border)
            ) {
                Text(
                    text = style.label,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = style.text,
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.5.dp)
                )
            }
        }
    }
}

/** Last charge and charge count, and how recently the linked app was opened (FR-SUB-5). */
@Composable
private fun SubscriptionActivity(sub: DetectedSubscription) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.HistoryToggleOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = buildAnnotatedString {
                        append("Last charged ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)) {
                            append(formatDate(sub.lastChargedMillis, "dd MMM"))
                        }
                        append(" · ${sub.chargeCount} charge${if (sub.chargeCount > 1) "s" else ""} logged")
                    },
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (sub.lastAppOpenedDaysAgo != null) {
            val unusedLong = sub.lastAppOpenedDaysAgo >= 30
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (unusedLong) MaterialTheme.pulseColors.dangerContainer else MaterialTheme.colorScheme.surfaceContainerLow,
                border = if (unusedLong) BorderStroke(1.dp, MaterialTheme.pulseColors.dangerBorder) else null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = null,
                        tint = if (unusedLong) MaterialTheme.pulseColors.danger else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = if (unusedLong) {
                            "App unused for ${sub.lastAppOpenedDaysAgo} days (Usage stats)"
                        } else {
                            "Linked app active ${sub.lastAppOpenedDaysAgo} days ago"
                        },
                        fontSize = 12.sp,
                        fontWeight = if (unusedLong) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (unusedLong) MaterialTheme.pulseColors.danger else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/** Shown after two "didn't use it" answers in a row (FR-SUB-4). */
@Composable
private fun UnusedMonthsWarning(months: Int) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.pulseColors.dangerContainer,
        border = BorderStroke(1.dp, MaterialTheme.pulseColors.dangerBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.pulseColors.danger,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Unused for ${months} consecutive months! Cancellation recommended.",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.pulseColors.dangerStrong
            )
        }
    }
}

/** "Did you use this this month?" with Yes / No (FR-SUB-4). */
@Composable
private fun UsageQuestion(onUsageAnswer: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Did you use this this month?",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.pulseColors.successContainer,
                border = BorderStroke(1.dp, MaterialTheme.pulseColors.successBorder),
                modifier = Modifier.clickable { onUsageAnswer(true) }
            ) {
                Text(
                    text = "Yes 👍",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.pulseColors.success,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                )
            }
            Surface(
                shape = CircleShape,
                color = MaterialTheme.pulseColors.dangerContainer,
                border = BorderStroke(1.dp, MaterialTheme.pulseColors.dangerBorder),
                modifier = Modifier.clickable { onUsageAnswer(false) }
            ) {
                Text(
                    text = "No 👎",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.pulseColors.danger,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                )
            }
        }
    }
}

/** Monthly cost, and Keep / "I cancelled it" while it needs a decision, or the yearly cost once decided. */
@Composable
private fun DrainRateAndActions(sub: DetectedSubscription, onKeep: () -> Unit, onCancel: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "DRAIN RATE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.6.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface, fontSize = 17.sp)) {
                        append(lkr(sub.monthlyCostLkr))
                    }
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)) {
                        append(" / mo")
                    }
                }
            )
        }

        if (sub.isFlagged || sub.risk == SubscriptionRisk.HIGH) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.clickable { onKeep() }
                ) {
                    Text(
                        text = "Keep",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
                Button(
                    onClick = onCancel,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.pulseColors.danger,
                        contentColor = MaterialTheme.pulseColors.onDanger
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                    modifier = Modifier.height(38.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "I cancelled it",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Text(
                    text = "${lkr(sub.annualCostLkr)} / yr",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}
