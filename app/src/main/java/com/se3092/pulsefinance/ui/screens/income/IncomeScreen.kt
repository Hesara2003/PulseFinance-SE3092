package com.se3092.pulsefinance.ui.screens.income

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
import com.se3092.pulsefinance.R
import com.se3092.pulsefinance.domain.model.Currency
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import com.se3092.pulsefinance.ui.components.EmptyStateView
import com.se3092.pulsefinance.ui.util.Formatters
import com.se3092.pulsefinance.ui.util.TransactionVisuals
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.width
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
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(NeoGreenDark, NeoGreen)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = "PulseFinance Emblem",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
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
                            Text(
                                text = state.userInitials,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 12.sp
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
                                text = "Realized Cash vs. Pending Milestones",
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
                                .background(NeoCardLow)
                                .clickable { viewModel.showAddSheet() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Tune Filters",
                                tint = NeoTextSecondary,
                                modifier = Modifier.size(20.dp)
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
                                        text = "TOTAL PROJECTED CYCLE",
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
                                                text = "$realizedPct% Inflow",
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
                                    text = Formatters.lkr(state.totalProjectedLkr, decimals = 2),
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = NeoTextPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 30.sp,
                                    letterSpacing = (-0.5).sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Expected total this settlement cycle",
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
                                                text = "Realized Inflow",
                                                fontSize = 12.sp,
                                                color = NeoTextSecondary
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = Formatters.lkr(state.totalRealizedLkr, decimals = 2),
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
                                                text = "Pending Claims",
                                                fontSize = 12.sp,
                                                color = NeoTextSecondary
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = Formatters.lkr(state.totalPendingLkr, decimals = 2),
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
                                        text = "${String.format("%.1f%%", state.realizedRatio * 100)} Cleared into Accounts",
                                        fontSize = 11.sp,
                                        color = NeoTextSecondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${String.format("%.1f%%", state.pendingRatio * 100)} In Escrow / Transit",
                                        fontSize = 11.sp,
                                        color = NeoTextSecondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Category Filter Pills
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CategoryPill(
                            label = "All (4)",
                            isSelected = state.filterCategory == "all",
                            onClick = { viewModel.setFilterCategory("all") }
                        )
                        CategoryPill(
                            label = "Active Contracts",
                            isSelected = state.filterCategory == "contracts",
                            onClick = { viewModel.setFilterCategory("contracts") }
                        )
                        CategoryPill(
                            label = "USD Wires",
                            isSelected = state.filterCategory == "usd",
                            onClick = { viewModel.setFilterCategory("usd") }
                        )
                        CategoryPill(
                            label = "Staking / Yield",
                            isSelected = state.filterCategory == "yield",
                            onClick = { viewModel.setFilterCategory("yield") }
                        )
                    }
                }

                // 4. Income Streams List (Filtered, live from Room)
                if (!state.isLoading && state.visibleStreams.isEmpty()) {
                    item {
                        EmptyStateView(
                            title = if (state.streams.isEmpty()) "No income streams yet" else "Nothing in this filter",
                            message = if (state.streams.isEmpty())
                                "Add your salary, freelance milestones, AdSense or crypto yield to see realized vs. pending cash."
                            else
                                "Try another category to see the rest of your streams.",
                            illustration = R.drawable.illustration_finance_app,
                            actionLabel = if (state.streams.isEmpty()) "Add income stream" else null,
                            onAction = if (state.streams.isEmpty()) viewModel::showAddSheet else null
                        )
                    }
                }

                items(state.visibleStreams, key = { it.id }) { stream ->
                    IncomeStreamItem(
                        stream = stream,
                        onMarkReceived = { viewModel.markReceived(stream.id) }
                    )
                }

                // 5. Auto-sweep suggestion for realized foreign-currency inflow
                if (state.foreignRealizedLkr > 0) item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 10.dp,
                                shape = RoundedCornerShape(24.dp),
                                ambientColor = Color(0x3800C950),
                                spotColor = Color(0x3800C950)
                            ),
                        shape = RoundedCornerShape(24.dp),
                        color = Color.Transparent
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(NeoGreen, NeoGreenLight)
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.22f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.TipsAndUpdates,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = "Auto-Sweep to High Yield Vault",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.height(1.dp))
                                        Text(
                                            text = "${Formatters.lkr(state.foreignRealizedLkr)} from FX inflows eligible for 11.2% p.a.",
                                            fontSize = 11.sp,
                                            color = Color.White.copy(alpha = 0.9f)
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = NeoGreenDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
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
        if (state.isAddSheetVisible) {
            AddIncomeBottomSheet(
                state = state,
                onTitleChange = viewModel::onTitleChanged,
                onClientChange = viewModel::onClientChanged,
                onAmountChange = viewModel::onAmountChanged,
                onSourceChange = viewModel::onSourceChanged,
                onCurrencyChange = viewModel::onCurrencyChanged,
                onRateChange = viewModel::onRateChanged,
                onStatusChange = viewModel::onStatusChanged,
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

@Composable
private fun IncomeStreamItem(
    stream: IncomeStream,
    onMarkReceived: () -> Unit
) {
    val visual = TransactionVisuals.forIncome(stream.source)
    val now = System.currentTimeMillis()
    val isRealized = stream.isRealized
    val isOverdue = stream.status == InvoiceStatus.OVERDUE
    val accent = when {
        isRealized -> NeoGreenDark
        isOverdue -> NeoAmber
        else -> NeoTextSecondary
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
                            .background(if (isOverdue) NeoAmber.copy(alpha = 0.15f) else visual.container),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = visual.icon,
                            contentDescription = null,
                            tint = if (isOverdue) NeoAmber else visual.tint,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stream.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NeoTextPrimary,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${stream.clientOrPlatform} · ${stream.source.displayName}",
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
                        text = Formatters.original(stream.originalAmount, stream.currency),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NeoTextPrimary,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(accent.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = when {
                                isRealized -> "Realized"
                                isOverdue -> {
                                    val days = Formatters.daysBetween(stream.dateReceivedOrExpected, now)
                                    if (days > 0) "Overdue $days Days" else "Overdue"
                                }
                                else -> "Pending"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isOverdue) Color(0xFFB45309) else accent
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (isRealized) Icons.Default.Verified else Icons.Default.Schedule,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = buildString {
                            append(if (isRealized) "Cleared " else "Due ")
                            append(Formatters.relativeDay(stream.dateReceivedOrExpected, now))
                            if (stream.currency != Currency.LKR) {
                                append(" · ≈ ")
                                append(Formatters.lkr(stream.amountInLkr))
                            }
                            if (stream.isRecurringMonthly) append(" · Monthly")
                        },
                        fontSize = 12.sp,
                        color = NeoTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (!isRealized) {
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
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Mark Received", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
