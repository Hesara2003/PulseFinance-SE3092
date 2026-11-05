package com.se3092.pulsefinance.ui.screens.expense

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
import com.se3092.pulsefinance.ui.theme.OnAccent
import com.se3092.pulsefinance.ui.theme.NeoBorder
import com.se3092.pulsefinance.ui.theme.NeoCard
import com.se3092.pulsefinance.ui.theme.NeoCardLow
import com.se3092.pulsefinance.ui.theme.NeoContainer
import com.se3092.pulsefinance.ui.theme.NeoContainerHigh
import com.se3092.pulsefinance.ui.theme.NeoErrorContainer
import com.se3092.pulsefinance.ui.theme.NeoGreenDark
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
import com.se3092.pulsefinance.ui.components.PulseNoticeHost

@Composable
fun ExpenseScreen(
    viewModel: ExpenseViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

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
                        color = NeoTextPrimary,
                        fontSize = 18.sp,
                        letterSpacing = (-0.2).sp
                    )
                }

                // Profile Avatar Circle
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(NeoContainer),
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
                                color = NeoGreenDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.2.sp
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Zombie Subscriptions",
                                style = MaterialTheme.typography.headlineMedium,
                                color = NeoTextPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 23.sp,
                                letterSpacing = (-0.3).sp
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Recurring auto-debits you've logged",
                                style = MaterialTheme.typography.bodyMedium,
                                color = NeoTextSecondary,
                                fontSize = 14.sp
                            )
                        }

                        // Auto Mode Glyph Container
                        Surface(
                            modifier = Modifier.size(42.dp),
                            shape = CircleShape,
                            color = NeoCard,
                            border = BorderStroke(1.dp, NeoBorder),
                            shadowElevation = 2.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoMode,
                                    contentDescription = "Audit Engine",
                                    tint = NeoTextPrimary,
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
                        color = Color(0xFFFFF1F2),
                        border = BorderStroke(1.dp, Color(0xFFFFCCD5)),
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
                                        .background(Color(0xFFE11D48)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Warning",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .scale(pingScale)
                                        .align(Alignment.TopEnd)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE11D48))
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${state.flaggedCount} subscription${if (state.flaggedCount > 1) "s" else ""} to review",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF9F1239),
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = buildAnnotatedString {
                                        append("Still charging ")
                                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF9F1239))) {
                                            append("${lkr(state.flaggedMonthlyLkr)} / month")
                                        }
                                        append(". Keep the ones you use; cancel the rest with the provider.")
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF881337),
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
                                    ambientColor = Color(0x060F172A),
                                    spotColor = Color(0x060F172A)
                                ),
                            shape = RoundedCornerShape(22.dp),
                            color = NeoCard,
                            border = BorderStroke(1.dp, NeoBorder)
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
                                            .background(Color(0xFFFFE4E6)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                                            contentDescription = null,
                                            tint = Color(0xFFE11D48),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = "ANNUAL DRAIN",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeoTextSecondary,
                                        letterSpacing = 0.6.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = lkr(state.flaggedAnnualLkr),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NeoTextPrimary,
                                    fontSize = 20.sp,
                                    letterSpacing = (-0.5).sp
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
                                    elevation = 4.dp,
                                    shape = RoundedCornerShape(22.dp),
                                    ambientColor = Color(0x060F172A),
                                    spotColor = Color(0x060F172A)
                                ),
                            shape = RoundedCornerShape(22.dp),
                            color = NeoCard,
                            border = BorderStroke(1.dp, NeoBorder)
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
                                            .background(Color(0xFFDCFCE7)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.RocketLaunch,
                                            contentDescription = null,
                                            tint = NeoGreenDark,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = "POTENTIAL SAVINGS",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeoTextSecondary,
                                        letterSpacing = 0.6.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = state.shareOfGoalRemainingPct?.let { "%.1f%%".format(it) } ?: "—",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NeoGreenDark,
                                    fontSize = 20.sp,
                                    letterSpacing = (-0.5).sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = state.goalTitle?.let { "of what's left for $it" } ?: "Set a goal to compare",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
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
                            color = NeoTextPrimary,
                            fontSize = 18.sp
                        )

                        Surface(
                            shape = CircleShape,
                            color = NeoCardLow,
                            border = BorderStroke(1.dp, NeoBorder)
                        ) {
                            Text(
                                text = "Flagged first",
                                fontSize = 11.sp,
                                color = NeoTextSecondary,
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
                val groupedSubs = state.subscriptions.groupBy { it.accountName.ifBlank { "Unassigned Account" } }
                groupedSubs.forEach { (accountName, subs) ->
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
                                        .background(Color(0xFFECFDF5)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalance,
                                        contentDescription = null,
                                        tint = NeoGreenDark,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                Text(
                                    text = accountName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeoTextPrimary
                                )
                            }
                            if (subs.any { it.isSecondaryAccount }) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFECFDF5),
                                    border = BorderStroke(1.dp, Color(0xFFA7F3D0))
                                ) {
                                    Text(
                                        text = "⚡ Secondary Account (Auto-Debit)",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF047857),
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
            Color(0xFFFFF7ED),
            Color(0xFFFED7AA),
            Color(0xFFC2410C)
        )
        SubscriptionStatus.STILL_CHARGING -> Quadruple(
            "Still charging",
            Color(0xFFFFF1F2),
            Color(0xFFFFCCD5),
            Color(0xFFBE123C)
        )
        SubscriptionStatus.KEPT -> Quadruple(
            "Active · Kept",
            Color(0xFFECFDF5),
            Color(0xFFA7F3D0),
            Color(0xFF047857)
        )
        SubscriptionStatus.CANCELLED -> Quadruple(
            "Cancelled",
            NeoCardLow,
            NeoBorder,
            NeoTextSecondary
        )
    }

    val isAlert = sub.isFlagged || sub.risk == SubscriptionRisk.HIGH
    val cardBorderColor = if (isAlert) Color(0xFFFFCCD5) else NeoBorder

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = Color(0x060F172A),
                spotColor = Color(0x060F172A)
            )
            .animateContentSize(),
        shape = RoundedCornerShape(22.dp),
        color = NeoCard,
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
                            .background(NeoCardLow),
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

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (sub.risk == SubscriptionRisk.HIGH) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFFF1F2),
                            border = BorderStroke(1.dp, Color(0xFFFFCCD5))
                        ) {
                            Text(
                                text = "High Risk ⚠️",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE11D48),
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
                            imageVector = Icons.Default.HistoryToggleOff,
                            contentDescription = null,
                            tint = NeoTextSecondary,
                            modifier = Modifier.size(15.dp)
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
                    val unusedLong = sub.lastAppOpenedDaysAgo >= 30
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (unusedLong) Color(0xFFFFF1F2) else NeoCardLow,
                        border = if (unusedLong) BorderStroke(1.dp, Color(0xFFFFCCD5)) else null,
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
                                tint = if (unusedLong) Color(0xFFE11D48) else NeoGreenDark,
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
                                color = if (unusedLong) Color(0xFFBE123C) else NeoTextSecondary
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
                    color = Color(0xFFFFF1F2),
                    border = BorderStroke(1.dp, Color(0xFFFFCCD5)),
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
                            tint = Color(0xFFE11D48),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Unused for ${sub.consecutiveUnusedMonths} consecutive months! Cancellation recommended.",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF9F1239)
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
                    .background(NeoCardLow)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Did you use this this month?",
                    fontSize = 12.sp,
                    color = NeoTextPrimary,
                    fontWeight = FontWeight.Medium
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFDCFCE7),
                        border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                        modifier = Modifier.clickable { onUsageAnswer(true) }
                    ) {
                        Text(
                            text = "Yes 👍",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFE4E6),
                        border = BorderStroke(1.dp, Color(0xFFFDA4AF)),
                        modifier = Modifier.clickable { onUsageAnswer(false) }
                    ) {
                        Text(
                            text = "No 👎",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFBE123C),
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
                        color = NeoTextSecondary,
                        letterSpacing = 0.6.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold, color = NeoTextPrimary, fontSize = 17.sp)) {
                                append(lkr(sub.monthlyCostLkr))
                            }
                            withStyle(SpanStyle(color = NeoTextSecondary, fontSize = 12.sp)) {
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
                            color = NeoCardLow,
                            border = BorderStroke(1.dp, NeoBorder),
                            modifier = Modifier.clickable { onKeep() }
                        ) {
                            Text(
                                text = "Keep",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NeoTextPrimary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                        Button(
                            onClick = onCancel,
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE11D48),
                                contentColor = Color.White
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
                        color = NeoCardLow,
                        border = BorderStroke(1.dp, NeoBorder)
                    ) {
                        Text(
                            text = "${lkr(sub.annualCostLkr)} / yr",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeoTextSecondary,
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
