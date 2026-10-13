package com.se3092.pulsefinance.ui.screens.income

import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import com.se3092.pulsefinance.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material.icons.filled.Token
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.TextButton
import com.se3092.pulsefinance.domain.model.Currency
import com.se3092.pulsefinance.domain.model.DAY_MILLIS
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import com.se3092.pulsefinance.ui.common.formatDate
import com.se3092.pulsefinance.ui.common.incomeSourceVisual
import com.se3092.pulsefinance.ui.common.lkr
import com.se3092.pulsefinance.ui.common.signedLkr
import com.se3092.pulsefinance.ui.components.EmptyStateView
import com.se3092.pulsefinance.ui.theme.NeoRose
import com.se3092.pulsefinance.ui.theme.NeoAmber
import com.se3092.pulsefinance.ui.theme.NeoBorder
import com.se3092.pulsefinance.ui.theme.NeoCard
import com.se3092.pulsefinance.ui.theme.NeoCardLow
import com.se3092.pulsefinance.ui.theme.NeoContainer
import com.se3092.pulsefinance.ui.theme.NeoGreen
import com.se3092.pulsefinance.ui.theme.NeoGreenDark
import com.se3092.pulsefinance.ui.theme.NeoGreenGlow
import com.se3092.pulsefinance.ui.theme.NeoGreenLight
import com.se3092.pulsefinance.ui.theme.NeoSecondaryContainer
import com.se3092.pulsefinance.ui.theme.NeoSurface
import com.se3092.pulsefinance.ui.theme.NeoTextPrimary
import com.se3092.pulsefinance.ui.theme.NeoTextSecondary
import kotlinx.coroutines.delay

@Composable
fun IncomeScreen(
    viewModel: IncomeViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Auto-dismiss toast
    LaunchedEffect(state.toastNotification) {
        if (state.toastNotification != null) {
            delay(3500)
            viewModel.dismissToast()
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

                // Multi Stream Income Hub label & User Profile Avatar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Multi Stream Income Hub",
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
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(NeoGreenDark, NeoGreen)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
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
                // 1. Header Section: Title & Tune Filter Action
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Income Streams",
                                style = MaterialTheme.typography.headlineLarge,
                                color = NeoTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp,
                                letterSpacing = (-0.5).sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Received cash vs. unpaid invoices",
                                style = MaterialTheme.typography.bodyMedium,
                                color = NeoTextSecondary,
                                fontSize = 14.sp
                            )
                        }

                        // Tune Action Button
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(NeoGreen)
                                .clickable { viewModel.showAddSheet() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Record income",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // 2. Total Inflow Summary Card
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(28.dp),
                                ambientColor = Color(0x0A0F172A),
                                spotColor = Color(0x0A0F172A)
                            ),
                        shape = RoundedCornerShape(28.dp),
                        color = NeoCard,
                        border = BorderStroke(1.dp, NeoBorder)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            // Ambient Green Glow in top-right
                            Box(
                                modifier = Modifier
                                    .size(170.dp)
                                    .align(Alignment.TopEnd)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                NeoGreen.copy(alpha = 0.15f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(22.dp)
                            ) {
                                // Top row: Projected Cycle & Trend Badge
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = state.monthLabel.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NeoTextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        letterSpacing = 1.sp
                                    )

                                    val realizedPct = (state.realizedRatio * 100).toInt()
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(NeoGreen.copy(alpha = 0.15f))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                                contentDescription = null,
                                                tint = NeoGreenDark,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = "$realizedPct% collected",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = NeoGreenDark
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Inflow Amount & Subtitle
                                Text(
                                    text = lkr(state.realizedThisMonthLkr, 2),
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = NeoTextPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 30.sp,
                                    letterSpacing = (-0.5).sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Received this month across every source",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NeoTextSecondary,
                                    fontSize = 12.sp
                                )

                                Spacer(modifier = Modifier.height(18.dp))

                                // Realized vs Pending Breakdown (2 columns)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // Column 1: Realized Inflow
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clip(CircleShape)
                                                    .background(NeoGreen)
                                            )
                                            Text(
                                                text = "Received",
                                                fontSize = 12.sp,
                                                color = NeoTextSecondary
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = lkr(state.realizedThisMonthLkr, 2),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NeoTextPrimary
                                        )
                                    }

                                    // Column 2: Pending Claims
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clip(CircleShape)
                                                    .background(NeoAmber)
                                            )
                                            Text(
                                                text = if (state.overdueLkr > 0) "Unpaid (${lkr(state.overdueLkr)} late)" else "Unpaid invoices",
                                                fontSize = 12.sp,
                                                color = NeoTextSecondary
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = lkr(state.pendingLkr, 2),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NeoTextPrimary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Segmented Dual Capsule Progress Bar
                                val animatedRealizedWidth by animateFloatAsState(
                                    targetValue = state.realizedRatio,
                                    animationSpec = tween(700),
                                    label = "realizedWidth"
                                )
                                val animatedPendingWidth by animateFloatAsState(
                                    targetValue = state.pendingRatio,
                                    animationSpec = tween(700),
                                    label = "pendingWidth"
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(12.dp)
                                        .clip(CircleShape)
                                        .background(NeoContainer)
                                        .padding(2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        if (animatedRealizedWidth > 0.01f) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(animatedRealizedWidth.coerceAtLeast(0.01f))
                                                    .fillMaxHeight()
                                                    .clip(CircleShape)
                                                    .background(NeoGreen)
                                            )
                                        }
                                        if (animatedPendingWidth > 0.01f) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(animatedPendingWidth.coerceAtLeast(0.01f))
                                                    .fillMaxHeight()
                                                    .clip(CircleShape)
                                                    .background(NeoAmber)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${String.format("%.1f%%", state.realizedRatio * 100)} collected",
                                        fontSize = 11.sp,
                                        color = NeoTextSecondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${String.format("%.1f%%", state.pendingRatio * 100)} still owed",
                                        fontSize = 11.sp,
                                        color = NeoTextSecondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Source Filter Pills (counts are real)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CategoryPill(
                            label = "All (${state.totalCount})",
                            isSelected = state.filterSource == null,
                            onClick = { viewModel.setFilter(null) }
                        )
                        IncomeSource.entries.forEach { source ->
                            CategoryPill(
                                label = "${source.shortLabel} (${state.countBySource[source] ?: 0})",
                                isSelected = state.filterSource == source,
                                onClick = { viewModel.setFilter(source) }
                            )
                        }
                    }
                }

                // 4. This month by source — the four channels side by side for once
                if (state.totalCount > 0) item {
                    SourceBreakdownCard(state.sourceBreakdown, state.realizedThisMonthLkr)
                }

                // 5. Crypto running net position (gains minus losses, year to date)
                if (state.cryptoEntriesYtd > 0) item {
                    CryptoNetCard(state.cryptoNetYtdLkr, state.cryptoEntriesYtd)
                }

                // 6. Income records
                if (!state.isLoading && state.incomes.isEmpty()) item {
                    EmptyStateView(
                        title = if (state.filterSource == null) "No income recorded yet" else "Nothing from ${state.filterSource?.displayName} yet",
                        message = "Tap + to record salary, a freelance milestone (paid or still invoiced), AdSense in USD, or a crypto gain or loss.",
                        icon = Icons.Default.Payments,
                        illustration = R.drawable.illustration_wallet
                    )
                }

                items(state.incomes, key = { it.id }) { income ->
                    IncomeRecordCard(
                        income = income,
                        onMarkReceived = { viewModel.markReceived(income) },
                        onDelete = { viewModel.deleteIncome(income) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp)) // Clearance for bottom dock
                }
            }
        }

        // =============================================================================
        // Top Floating Toast Notification (Exact HTML replica)
        // =============================================================================
        AnimatedVisibility(
            visible = state.toastNotification != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 70.dp, start = 16.dp, end = 16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = NeoCard,
                shadowElevation = 14.dp,
                border = BorderStroke(1.dp, NeoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(NeoGreen.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = NeoGreenDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Text(
                                text = state.toastNotification?.first ?: "",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeoTextPrimary
                            )
                            Text(
                                text = state.toastNotification?.second ?: "",
                                fontSize = 11.sp,
                                color = NeoTextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.dismissToast() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = NeoTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // =============================================================================
        // Add Income Stream Bottom Sheet
        // =============================================================================
        if (state.form.isVisible) {
            AddIncomeBottomSheet(
                form = state.form,
                onTitleChange = viewModel::onTitleChanged,
                onClientChange = viewModel::onClientChanged,
                onAmountChange = viewModel::onAmountChanged,
                onSourceChange = viewModel::onSourceChanged,
                onCurrencyChange = viewModel::onCurrencyChanged,
                onRateChange = viewModel::onRateChanged,
                onStatusChange = viewModel::onStatusChanged,
                onDayOffsetChange = viewModel::onDayOffsetChanged,
                onCryptoLossToggle = viewModel::onCryptoLossToggled,
                onRecurringToggle = viewModel::onRecurringToggled,
                onSubmit = viewModel::submitIncome,
                onDismiss = viewModel::hideAddSheet
            )
        }
    }
}

@Composable
private fun CategoryPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .then(
                if (isSelected) {
                    Modifier
                        .shadow(4.dp, CircleShape, spotColor = NeoGreenGlow)
                        .background(NeoGreen)
                } else {
                    Modifier.background(NeoCardLow)
                }
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isSelected) Color.White else NeoTextSecondary
        )
    }
}

private val IncomeSource.shortLabel: String
    get() = when (this) {
        IncomeSource.SALARY -> "Salary"
        IncomeSource.FREELANCE -> "Freelance"
        IncomeSource.ADSENSE -> "AdSense"
        IncomeSource.CRYPTO -> "Crypto"
    }

@Composable
private fun SourceBreakdownCard(breakdown: List<SourceTotal>, totalLkr: Double) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = NeoCard,
        border = BorderStroke(1.dp, NeoBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "THIS MONTH BY SOURCE",
                style = MaterialTheme.typography.labelSmall,
                color = NeoTextSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )
            breakdown.forEach { item ->
                val visual = incomeSourceVisual(item.source)
                val share = if (totalLkr > 0) (item.amountLkr / totalLkr).toFloat().coerceIn(0f, 1f) else 0f
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(visual.container),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(visual.icon, contentDescription = null, tint = visual.tint, modifier = Modifier.size(14.dp))
                            }
                            Text(text = item.source.displayName, fontSize = 13.sp, color = NeoTextPrimary, fontWeight = FontWeight.Medium)
                        }
                        Text(
                            text = if (item.count == 0) "—" else lkr(item.amountLkr),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (item.amountLkr < 0) NeoRose else NeoTextPrimary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(NeoContainer)
                    ) {
                        if (share > 0f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(share)
                                    .fillMaxHeight()
                                    .clip(CircleShape)
                                    .background(NeoGreen)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CryptoNetCard(netLkr: Double, entries: Int) {
    val isUp = netLkr >= 0
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFEEF2FF)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Token, contentDescription = null, tint = Color(0xFF4F46E5), modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Crypto net, year to date", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF3730A3))
                Text(
                    text = "$entries realised gain/loss entr${if (entries == 1) "y" else "ies"} this year",
                    fontSize = 11.sp,
                    color = Color(0xFF4338CA)
                )
            }
            Text(
                text = signedLkr(netLkr),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = if (isUp) NeoGreenDark else NeoRose
            )
        }
    }
}

@Composable
private fun IncomeRecordCard(
    income: IncomeStream,
    onMarkReceived: () -> Unit,
    onDelete: () -> Unit
) {
    val status = income.effectiveStatus()
    val visual = incomeSourceVisual(income.source)
    val (statusLabel, statusBg, statusText) = when (status) {
        InvoiceStatus.RECEIVED -> Triple(if (income.isLoss) "Loss" else "Received", if (income.isLoss) NeoRose.copy(alpha = 0.12f) else NeoGreen.copy(alpha = 0.15f), if (income.isLoss) NeoRose else NeoGreenDark)
        InvoiceStatus.PENDING_INVOICE -> Triple("Awaiting payment", NeoAmber.copy(alpha = 0.15f), Color(0xFFB45309))
        InvoiceStatus.OVERDUE -> Triple("Overdue", NeoRose.copy(alpha = 0.12f), NeoRose)
    }
    val dateLine = when (status) {
        InvoiceStatus.RECEIVED -> "Received ${formatDate(income.dateReceivedOrExpected)}"
        InvoiceStatus.PENDING_INVOICE -> "Due ${formatDate(income.dateReceivedOrExpected)}"
        InvoiceStatus.OVERDUE -> {
            val days = ((System.currentTimeMillis() - income.dateReceivedOrExpected) / DAY_MILLIS).coerceAtLeast(1)
            "Due ${formatDate(income.dateReceivedOrExpected)} · $days day${if (days > 1) "s" else ""} late"
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color(0x080F172A),
                spotColor = Color(0x080F172A)
            )
            .animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        color = NeoCard,
        border = BorderStroke(1.dp, NeoBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
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
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(visual.container),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(visual.icon, contentDescription = null, tint = visual.tint, modifier = Modifier.size(24.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = income.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NeoTextPrimary,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${income.clientOrPlatform} · ${income.source.displayName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeoTextSecondary,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (income.currency == Currency.LKR) lkr(income.amountInLkr, 2)
                        else "${income.currency.name} ${"%,.2f".format(income.originalAmount)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (income.isLoss) NeoRose else NeoTextPrimary,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(statusBg)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = statusLabel, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = statusText)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Meta pill: dates, and the exact FX rate captured at the time
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = NeoCardLow.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (income.currency == Currency.LKR) Icons.Default.Schedule else Icons.Default.CurrencyExchange,
                            contentDescription = null,
                            tint = NeoTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (income.currency == Currency.LKR) dateLine
                            else "1 ${income.currency.name} = ${"%,.2f".format(income.exchangeRateToLkr)} LKR · $dateLine",
                            fontSize = 11.sp,
                            color = NeoTextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (income.currency != Currency.LKR) {
                        Text(
                            text = "≈ ${lkr(income.amountInLkr)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeoTextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDelete) {
                    Text(text = "Delete", fontSize = 12.sp, color = NeoTextSecondary)
                }
                if (status != InvoiceStatus.RECEIVED) {
                    Button(
                        onClick = onMarkReceived,
                        modifier = Modifier
                            .height(40.dp)
                            .shadow(6.dp, CircleShape, spotColor = NeoGreenGlow),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeoGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Mark received", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (income.isRecurringMonthly) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Autorenew, contentDescription = null, tint = NeoGreenDark, modifier = Modifier.size(14.dp))
                        Text(text = "Monthly", fontSize = 11.sp, color = NeoGreenDark, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
