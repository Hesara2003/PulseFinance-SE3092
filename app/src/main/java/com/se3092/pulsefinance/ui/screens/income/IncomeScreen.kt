package com.se3092.pulsefinance.ui.screens.income

import com.se3092.pulsefinance.ui.components.ProfileAvatar
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
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import com.se3092.pulsefinance.ui.util.Formatters
import com.se3092.pulsefinance.ui.util.TransactionVisuals
import com.se3092.pulsefinance.ui.util.formatDate
import com.se3092.pulsefinance.ui.components.EmptyStateView
import com.se3092.pulsefinance.ui.theme.OnAccent
import com.se3092.pulsefinance.ui.theme.Teal
import com.se3092.pulsefinance.ui.theme.VioletAccent
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
                        ProfileAvatar(initials = state.userInitials, size = 34.dp)
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
                                tint = OnAccent,
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
                                                Teal.copy(alpha = 0.15f),
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
                                        text = formatDate(System.currentTimeMillis(), "MMMM").uppercase(),
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
                                            .background(Teal.copy(alpha = 0.15f))
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
                                    text = Formatters.lkr(state.totalRealizedLkr, decimals = 2),
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
                                val overdueLkr = state.streams
                                    .filter { it.status == InvoiceStatus.OVERDUE }
                                    .sumOf { it.amountInLkr }
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
                                                    .background(Teal)
                                            )
                                            Text(
                                                text = "Received",
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
                                                text = if (overdueLkr > 0) "Unpaid (${Formatters.lkr(overdueLkr)} late)" else "Unpaid invoices",
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
                                                    .background(Teal)
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
                        listOf(
                            "all" to "All (${state.streams.size})",
                            "contracts" to "Salary & Freelance",
                            "usd" to "USD",
                            "yield" to "Crypto"
                        ).forEach { (key, label) ->
                            CategoryPill(
                                label = label,
                                isSelected = state.filterCategory == key,
                                onClick = { viewModel.setFilterCategory(key) }
                            )
                        }
                    }
                }

                // 6. Income records
                if (!state.isLoading && state.visibleStreams.isEmpty()) item {
                    EmptyStateView(
                        title = if (state.streams.isEmpty()) "No income recorded yet" else "Nothing in this filter yet",
                        message = "Tap + to record salary, a freelance milestone (paid or still invoiced), AdSense in USD, or a crypto gain or loss.",
                        icon = Icons.Default.Payments,
                        illustration = R.drawable.illustration_wallet
                    )
                }

                items(state.visibleStreams, key = { it.id }) { income ->
                    IncomeRecordCard(
                        income = income,
                        onMarkReceived = { viewModel.markReceived(income.id) },
                        onDelete = { viewModel.deleteStream(income.id) }
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
            color = if (isSelected) OnAccent else NeoTextSecondary
        )
    }
}

@Composable
private fun IncomeRecordCard(
    income: IncomeStream,
    onMarkReceived: () -> Unit,
    onDelete: () -> Unit
) {
    val status = income.status
    val visual = TransactionVisuals.forIncome(income.source)
    val (statusLabel, statusBg, statusText) = when (status) {
        InvoiceStatus.RECEIVED -> Triple("Received", Teal.copy(alpha = 0.15f), NeoGreenDark)
        InvoiceStatus.PENDING_INVOICE -> Triple("Awaiting payment", NeoAmber.copy(alpha = 0.15f), NeoAmber)
        InvoiceStatus.OVERDUE -> Triple("Overdue", NeoRose.copy(alpha = 0.12f), NeoRose)
    }
    val dateLine = when (status) {
        InvoiceStatus.RECEIVED -> "Received ${formatDate(income.dateReceivedOrExpected)}"
        InvoiceStatus.PENDING_INVOICE -> "Due ${formatDate(income.dateReceivedOrExpected)}"
        InvoiceStatus.OVERDUE -> {
            val days = ((System.currentTimeMillis() - income.dateReceivedOrExpected) / (24L * 60 * 60 * 1000)).coerceAtLeast(1)
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
                        text = if (income.currency == Currency.LKR) Formatters.lkr(income.amountInLkr, decimals = 2)
                        else "${income.currency.name} ${"%,.2f".format(income.originalAmount)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NeoTextPrimary,
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
                            text = "≈ ${Formatters.lkr(income.amountInLkr)}",
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
                            contentColor = OnAccent
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
