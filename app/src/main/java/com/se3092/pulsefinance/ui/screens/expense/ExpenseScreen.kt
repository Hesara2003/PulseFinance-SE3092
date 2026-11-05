package com.se3092.pulsefinance.ui.screens.expense

import com.se3092.pulsefinance.ui.theme.pulseColors
import com.se3092.pulsefinance.ui.components.ProfileAvatar
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import com.se3092.pulsefinance.R
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HistoryToggleOff
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.se3092.pulsefinance.domain.model.DetectedSubscription
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.SubscriptionRisk
import com.se3092.pulsefinance.domain.model.SubscriptionStatus
import com.se3092.pulsefinance.ui.common.formatDate
import com.se3092.pulsefinance.ui.common.lkr
import com.se3092.pulsefinance.ui.common.presetIllustration
import com.se3092.pulsefinance.ui.components.EmptyStateView
import com.se3092.pulsefinance.ui.components.PulseNoticeHost

@Composable
fun ExpenseScreen(
    viewModel: ExpenseViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // =========================================================================
            // Top Navigation & Branding Header
            // =========================================================================
            Spacer(modifier = Modifier.height(10.dp))
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
                    ProfileAvatar(initials = state.initials, size = 36.dp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // =========================================================================
            // Main Content Scrollable
            // =========================================================================
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // 1. Audit Intelligence Title & Auto Mode Icon
                item {
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

                // 2. High-Impact Leak Alert Card
                if (state.flaggedCount > 0) item {
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
                                    text = "${state.flaggedCount} subscription${if (state.flaggedCount > 1) "s" else ""} to review",
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
                                            append("${lkr(state.flaggedMonthlyLkr)} / month")
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

                // 3. Grid of 2 Metric Cards: Total Annual Drain & Potential Savings
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Metric Card 1: Total Annual Drain
                        Surface(
                            modifier = Modifier
                                .weight(1f)
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
                                            .background(MaterialTheme.pulseColors.dangerContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                                            contentDescription = null,
                                            tint = MaterialTheme.pulseColors.danger,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = "ANNUAL DRAIN",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        letterSpacing = 0.6.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = lkr(state.flaggedAnnualLkr),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 20.sp,
                                    letterSpacing = (-0.5).sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Per year if left running",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Metric Card 2: Potential Savings
                        Surface(
                            modifier = Modifier
                                .weight(1f)
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
                                            .background(MaterialTheme.pulseColors.successContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.RocketLaunch,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = "POTENTIAL SAVINGS",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        letterSpacing = 0.6.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = state.shareOfGoalRemainingPct?.let { "%.1f%%".format(it) } ?: "—",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 20.sp,
                                    letterSpacing = (-0.5).sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = state.goalTitle?.let { "of what's left for $it" } ?: "Set a goal to compare",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // 4. Detected Outflows Section Header
                item {
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

                if (!state.isLoading && state.subscriptions.isEmpty()) item {
                    EmptyStateView(
                        title = "No subscriptions detected yet",
                        message = "Log gym fees, streaming or SaaS auto-debits with the \"Subscriptions / Auto-Debits\" category. Anything charged in the last 6 weeks shows up here with its yearly cost.",
                        icon = Icons.Default.AutoMode,
                        illustration = R.drawable.illustration_no_data
                    )
                }

                // 5. Subscription Cards List Grouped by Account (FR-SUB-6)
                state.subscriptionsByAccount.forEach { (accountName, subs) ->
                    item(key = "header_$accountName") {
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
                            if (subs.any { it.isSecondaryAccount }) {
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
                    items(
                        items = subs,
                        key = { it.key }
                    ) { sub ->
                        ZombieCardItem(
                            sub = sub,
                            onCancel = { viewModel.markCancelled(sub) },
                            onKeep = { viewModel.keepSubscription(sub) },
                            onUsageAnswer = { used -> viewModel.recordUsageAnswer(sub, used) }
                        )
                    }
                }

                // 6. Action Button: cancel every flagged subscription at once
                if (state.subscriptions.isNotEmpty()) item {
                    val allClear = state.flaggedCount == 0
                    Button(
                        onClick = { viewModel.markAllFlaggedCancelled() },
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
                                        append("Cancel ${state.flaggedCount} flagged ")
                                        withStyle(SpanStyle(color = MaterialTheme.pulseColors.primaryLight)) {
                                            append("(save ${lkr(state.flaggedMonthlyLkr)}/mo)")
                                        }
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp)) // Clearance for bottom dock
                }
            }
        }

        PulseNoticeHost(
            notice = state.notice,
            onDismiss = viewModel::dismissNotice,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun ZombieCardItem(
    sub: DetectedSubscription,
    onCancel: () -> Unit,
    onKeep: () -> Unit,
    onUsageAnswer: (Boolean) -> Unit
) {
    val (statusLabel, statusBg, statusBorder, statusText) = when (sub.status) {
        SubscriptionStatus.NEEDS_REVIEW -> Quadruple(
            "Needs review",
            MaterialTheme.pulseColors.cautionContainer,
            MaterialTheme.pulseColors.cautionBorder,
            MaterialTheme.pulseColors.caution
        )
        SubscriptionStatus.STILL_CHARGING -> Quadruple(
            "Still charging",
            MaterialTheme.pulseColors.dangerContainer,
            MaterialTheme.pulseColors.dangerBorder,
            MaterialTheme.pulseColors.danger
        )
        SubscriptionStatus.KEPT -> Quadruple(
            "Active · Kept",
            MaterialTheme.pulseColors.successContainer,
            MaterialTheme.pulseColors.successBorder,
            MaterialTheme.pulseColors.success
        )
        SubscriptionStatus.CANCELLED -> Quadruple(
            "Cancelled",
            MaterialTheme.colorScheme.surfaceContainerLow,
            MaterialTheme.colorScheme.outlineVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    val isAlert = sub.isFlagged || sub.risk == SubscriptionRisk.HIGH
    val cardBorderColor = if (isAlert) MaterialTheme.pulseColors.dangerBorder else MaterialTheme.colorScheme.outlineVariant

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
        border = BorderStroke(1.dp, cardBorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header Row: Illustrated Icon, Title/Account, and Status + Risk Badges
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
                        color = statusBg,
                        border = BorderStroke(1.dp, statusBorder)
                    ) {
                        Text(
                            text = statusLabel,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusText,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Charge history & App Activity Pills (FR-SUB-5)
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

            // Consecutive Unused Months Banner (FR-SUB-4)
            if (sub.consecutiveUnusedMonths >= 2) {
                Spacer(modifier = Modifier.height(8.dp))
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
                            text = "Unused for ${sub.consecutiveUnusedMonths} consecutive months! Cancellation recommended.",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.pulseColors.dangerStrong
                        )
                    }
                }
            }

            // Interactive Monthly Usage Prompt (FR-SUB-4)
            Spacer(modifier = Modifier.height(10.dp))
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

            Spacer(modifier = Modifier.height(14.dp))

            // Drain Rate & Actions
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
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
