package com.se3092.pulsefinance.ui.screens.expense

import com.se3092.pulsefinance.ui.components.ProfileAvatar
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import com.se3092.pulsefinance.R
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.ui.common.presetIllustration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.se3092.pulsefinance.domain.model.SubscriptionRisk
import com.se3092.pulsefinance.domain.model.SubscriptionStatus
import com.se3092.pulsefinance.ui.common.formatDate
import com.se3092.pulsefinance.ui.common.lkr
import com.se3092.pulsefinance.ui.components.EmptyStateView
import com.se3092.pulsefinance.ui.theme.OnAccent
import com.se3092.pulsefinance.ui.theme.NeoBorder
import com.se3092.pulsefinance.ui.theme.NeoCard
import com.se3092.pulsefinance.ui.theme.NeoCardLow
import com.se3092.pulsefinance.ui.theme.NeoContainer
import com.se3092.pulsefinance.ui.theme.NeoContainerHigh
import com.se3092.pulsefinance.ui.theme.NeoErrorContainer
import com.se3092.pulsefinance.ui.theme.NeoGreen
import com.se3092.pulsefinance.ui.theme.NeoGreenDark
import com.se3092.pulsefinance.ui.theme.NeoGreenFixed
import com.se3092.pulsefinance.ui.theme.NeoGreenLight
import com.se3092.pulsefinance.ui.theme.NeoInverseOnSurface
import com.se3092.pulsefinance.ui.theme.NeoInverseSurface
import com.se3092.pulsefinance.ui.theme.NeoOnPrimaryFixed
import com.se3092.pulsefinance.ui.theme.NeoOnSecondaryFixed
import com.se3092.pulsefinance.ui.theme.NeoRose
import com.se3092.pulsefinance.ui.theme.NeoSecondaryContainer
import com.se3092.pulsefinance.ui.theme.NeoSurface
import com.se3092.pulsefinance.ui.theme.NeoTextPrimary
import com.se3092.pulsefinance.ui.theme.NeoTextSecondary
import kotlinx.coroutines.delay

@Composable
fun ExpenseScreen(
    viewModel: ExpenseViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Auto-dismiss toast
    LaunchedEffect(state.message) {
        if (state.message != null) {
            delay(2800)
            viewModel.dismissMessage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NeoSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // =========================================================================
            // Top Navigation & Branding Header (Exact HTML replica)
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
                        color = NeoTextPrimary,
                        fontSize = 17.sp,
                        letterSpacing = (-0.2).sp
                    )
                }

                // Zombie Subscription Audit label & User Profile Avatar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Zombie Subscription Audit",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = NeoTextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Profile Avatar Circle
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(NeoContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        ProfileAvatar(initials = state.initials, size = 34.dp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

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
                                color = NeoGreenDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Zombie Subscriptions",
                                style = MaterialTheme.typography.headlineMedium,
                                color = NeoTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Recurring auto-debits you've logged",
                                style = MaterialTheme.typography.bodyMedium,
                                color = NeoTextSecondary,
                                fontSize = 14.sp
                            )
                        }

                        // Auto Mode Glyph Container
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(NeoContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoMode,
                                contentDescription = "Audit Engine",
                                tint = NeoTextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // 2. Red Alert Warning Banner
                if (state.flaggedCount > 0) item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = NeoErrorContainer
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
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

                            Box(modifier = Modifier.size(36.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .align(Alignment.Center)
                                        .clip(CircleShape)
                                        .background(NeoRose),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Warning",
                                        tint = OnAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .scale(pingScale)
                                        .align(Alignment.TopEnd)
                                        .clip(CircleShape)
                                        .background(NeoRose)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${state.flaggedCount} subscription${if (state.flaggedCount > 1) "s" else ""} to review",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = NeoRose,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = buildAnnotatedString {
                                        append("Still charging ")
                                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("${lkr(state.flaggedMonthlyLkr)} / month")
                                        }
                                        append(". Keep the ones you use; cancel the rest with the provider.")
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NeoRose.copy(alpha = 0.9f),
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
                                    elevation = 6.dp,
                                    shape = RoundedCornerShape(20.dp),
                                    ambientColor = Color(0x060F172A),
                                    spotColor = Color(0x060F172A)
                                ),
                            shape = RoundedCornerShape(20.dp),
                            color = NeoCard,
                            border = BorderStroke(1.dp, NeoBorder)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                                        contentDescription = null,
                                        tint = NeoRose,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "TOTAL ANNUAL DRAIN",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeoTextSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = lkr(state.flaggedAnnualLkr),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NeoTextPrimary,
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Per year if left running",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeoTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Metric Card 2: Potential Savings
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(
                                    elevation = 6.dp,
                                    shape = RoundedCornerShape(20.dp),
                                    ambientColor = Color(0x060F172A),
                                    spotColor = Color(0x060F172A)
                                ),
                            shape = RoundedCornerShape(20.dp),
                            color = NeoCard,
                            border = BorderStroke(1.dp, NeoBorder)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RocketLaunch,
                                        contentDescription = null,
                                        tint = NeoGreenDark,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "POTENTIAL SAVINGS",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeoTextSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = state.shareOfGoalRemainingPct?.let { "%.1f%%".format(it) } ?: "—",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NeoGreenDark,
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = state.goalTitle?.let { "of what's left for $it" } ?: "Set a goal to compare",
                                    maxLines = 2,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeoTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // 4. Detected Outflows Section Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Detected Subscriptions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = NeoTextPrimary,
                            fontSize = 17.sp
                        )

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(NeoContainerHigh)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Flagged first",
                                fontSize = 11.sp,
                                color = NeoTextSecondary,
                                fontWeight = FontWeight.Medium
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
                val groupedSubs = state.subscriptions.groupBy { it.accountName.ifBlank { "Unassigned Account" } }
                groupedSubs.forEach { (accountName, subs) ->
                    item(key = "header_$accountName") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.ic_preset_bank),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = accountName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeoTextPrimary
                                )
                            }
                            if (subs.any { it.isSecondaryAccount }) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(NeoGreenFixed)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "⚡ Secondary Account (Auto-Debit)",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeoOnPrimaryFixed
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
                            .height(56.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = CircleShape,
                                spotColor = Color(0x30000000)
                            ),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (allClear) NeoInverseSurface.copy(alpha = 0.5f) else NeoInverseSurface,
                            contentColor = NeoInverseOnSurface,
                            disabledContainerColor = NeoInverseSurface.copy(alpha = 0.4f),
                            disabledContentColor = NeoInverseOnSurface.copy(alpha = 0.7f)
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
                                    tint = NeoGreenLight,
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
                                    tint = NeoGreenLight,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = buildAnnotatedString {
                                        append("Cancel ${state.flaggedCount} flagged ")
                                        withStyle(SpanStyle(color = NeoGreenLight)) {
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

        // =============================================================================
        // Top Floating Toast Notification (Exact HTML prototype replica)
        // =============================================================================
        AnimatedVisibility(
            visible = state.message != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 70.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = NeoInverseSurface,
                shadowElevation = 14.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Success",
                        tint = NeoGreenFixed,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = state.message.orEmpty(),
                        color = NeoInverseOnSurface,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ZombieCardItem(
    sub: DetectedSubscription,
    onCancel: () -> Unit,
    onKeep: () -> Unit,
    onUsageAnswer: (Boolean) -> Unit
) {
    val (statusLabel, statusBg, statusText) = when (sub.status) {
        SubscriptionStatus.NEEDS_REVIEW -> Triple("Needs review", NeoErrorContainer, NeoRose)
        SubscriptionStatus.STILL_CHARGING -> Triple("Still charging", NeoErrorContainer, NeoRose)
        SubscriptionStatus.KEPT -> Triple("Kept", NeoGreenFixed, NeoOnPrimaryFixed)
        SubscriptionStatus.CANCELLED -> Triple("Cancelled", NeoSecondaryContainer, NeoOnSecondaryFixed)
    }

    val riskBorderColor = when (sub.risk) {
        SubscriptionRisk.HIGH -> NeoRose.copy(alpha = 0.5f)
        SubscriptionRisk.MEDIUM -> Color(0xFFF59E0B).copy(alpha = 0.4f)
        SubscriptionRisk.LOW -> NeoBorder
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = Color(0x080F172A),
                spotColor = Color(0x080F172A)
            )
            .animateContentSize(),
        shape = RoundedCornerShape(22.dp),
        color = NeoCard,
        border = BorderStroke(1.dp, riskBorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header Row: Icon, Title/Account, and Status + Risk Badges
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
                    // What it is; the status tag and the card border already say how it's doing
                    Image(
                        painter = painterResource(presetIllustration(sub.title, ExpenseCategory.SUBSCRIPTION)),
                        contentDescription = null,
                        modifier = Modifier.size(46.dp)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = sub.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NeoTextPrimary,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = sub.accountName.ifBlank { "Auto-debit" },
                            style = MaterialTheme.typography.bodySmall,
                            color = NeoTextSecondary,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (sub.risk == SubscriptionRisk.HIGH) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(NeoRose)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "High Risk ⚠️",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnAccent
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(statusBg)
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = statusLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Charge history & App Activity Pills (FR-SUB-5)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NeoCardLow,
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
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = NeoTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = buildAnnotatedString {
                                append("Last charged ")
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = NeoTextPrimary)) {
                                    append(formatDate(sub.lastChargedMillis, "dd MMM"))
                                }
                                append(" · ${sub.chargeCount} charge${if (sub.chargeCount > 1) "s" else ""} logged")
                            },
                            fontSize = 12.sp,
                            color = NeoTextSecondary
                        )
                    }
                }

                if (sub.lastAppOpenedDaysAgo != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (sub.lastAppOpenedDaysAgo >= 30) NeoErrorContainer.copy(alpha = 0.5f) else NeoCardLow,
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
                                tint = if (sub.lastAppOpenedDaysAgo >= 30) NeoRose else NeoGreenDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (sub.lastAppOpenedDaysAgo >= 30) {
                                    "App unused for ${sub.lastAppOpenedDaysAgo} days (Usage stats)"
                                } else {
                                    "Linked app active ${sub.lastAppOpenedDaysAgo} days ago"
                                },
                                fontSize = 12.sp,
                                fontWeight = if (sub.lastAppOpenedDaysAgo >= 30) FontWeight.Bold else FontWeight.Normal,
                                color = if (sub.lastAppOpenedDaysAgo >= 30) NeoRose else NeoTextSecondary
                            )
                        }
                    }
                }
            }

            // Consecutive Unused Months Banner (FR-SUB-4)
            if (sub.consecutiveUnusedMonths >= 2) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NeoErrorContainer.copy(alpha = 0.7f),
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
                            tint = NeoRose,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Unused for ${sub.consecutiveUnusedMonths} consecutive months! Cancellation recommended.",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeoRose
                        )
                    }
                }
            }

            // Interactive Monthly Usage Prompt (FR-SUB-4)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(NeoCardLow)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Did you use this this month?",
                    fontSize = 12.sp,
                    color = NeoTextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = CircleShape,
                        color = NeoGreenFixed,
                        modifier = Modifier.clickable { onUsageAnswer(true) }
                    ) {
                        Text(
                            text = "Yes 👍",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeoOnPrimaryFixed,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = NeoErrorContainer,
                        modifier = Modifier.clickable { onUsageAnswer(false) }
                    ) {
                        Text(
                            text = "No 👎",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeoRose,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
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
                        color = NeoTextSecondary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = NeoTextPrimary, fontSize = 16.sp)) {
                                append(lkr(sub.monthlyCostLkr))
                            }
                            withStyle(SpanStyle(color = NeoTextSecondary, fontSize = 12.sp)) {
                                append(" / mo")
                            }
                        }
                    )
                }

                if (sub.isFlagged || sub.risk == SubscriptionRisk.HIGH) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        TextButton(onClick = onKeep) {
                            Text(text = "Keep", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = NeoTextSecondary)
                        }
                        Button(
                            onClick = onCancel,
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeoRose,
                                contentColor = OnAccent
                            ),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
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
                    Text(
                        text = "${lkr(sub.annualCostLkr)} / yr",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NeoTextSecondary
                    )
                }
            }
        }
    }
}
