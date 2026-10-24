package com.se3092.pulsefinance.ui.screens.analytics

import com.se3092.pulsefinance.ui.components.ProfileAvatar
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import com.se3092.pulsefinance.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.se3092.pulsefinance.domain.model.DAY_MILLIS
import com.se3092.pulsefinance.domain.model.Period
import java.util.Calendar
import java.util.TimeZone
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.lazy.LazyRow
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.ui.components.CategoryDonutChart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.se3092.pulsefinance.ui.theme.OnAccent
import com.se3092.pulsefinance.ui.theme.NeoBorder
import com.se3092.pulsefinance.ui.theme.NeoCard
import com.se3092.pulsefinance.ui.theme.NeoCardLow
import com.se3092.pulsefinance.ui.theme.NeoContainer
import com.se3092.pulsefinance.ui.theme.NeoErrorContainer
import com.se3092.pulsefinance.ui.theme.NeoGreen
import com.se3092.pulsefinance.ui.theme.NeoGreenDark
import com.se3092.pulsefinance.ui.theme.NeoGreenGlow
import com.se3092.pulsefinance.ui.theme.NeoRose
import com.se3092.pulsefinance.ui.theme.NeoSecondaryContainer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Payments
import com.se3092.pulsefinance.ui.common.TransactionItem
import com.se3092.pulsefinance.ui.common.expenseCategoryVisual
import com.se3092.pulsefinance.ui.common.lkr
import com.se3092.pulsefinance.ui.common.relativeDayLabel
import com.se3092.pulsefinance.ui.common.signedLkr
import com.se3092.pulsefinance.ui.common.transactionVisual
import com.se3092.pulsefinance.ui.components.CommittedVsDiscretionaryBar
import com.se3092.pulsefinance.ui.components.EmptyStateView
import com.se3092.pulsefinance.ui.theme.NeoSurface
import com.se3092.pulsefinance.ui.theme.NeoTextPrimary
import com.se3092.pulsefinance.ui.theme.NeoTextSecondary
import com.se3092.pulsefinance.ui.theme.NeoTextTertiary

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showDateRangePicker by rememberSaveable { mutableStateOf(false) }

    if (showDateRangePicker) {
        TransactionDateRangeDialog(
            initialRange = state.filterDateRange,
            onConfirm = { range ->
                viewModel.onDateRangeChanged(range)
                showDateRangePicker = false
            },
            onDismiss = { showDateRangePicker = false }
        )
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

                // Financial Analytics & Clarity label & User Profile Avatar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Financial Analytics & Clarity",
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
                // 1. Segmented Control: Daily / Weekly / Monthly / Yearly (FR-ANA-2, FR-ANA-4)
                item {
                    Surface(
                        shape = CircleShape,
                        color = NeoContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf(
                                AnalyticsInterval.DAILY to "Daily",
                                AnalyticsInterval.WEEKLY to "Weekly",
                                AnalyticsInterval.MONTHLY to "Monthly",
                                AnalyticsInterval.YEARLY to "Yearly"
                            ).forEach { (interval, label) ->
                                val isSelected = state.interval == interval
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(CircleShape)
                                        .then(
                                            if (isSelected) {
                                                Modifier
                                                    .shadow(6.dp, CircleShape, spotColor = NeoGreenGlow)
                                                    .background(NeoGreen)
                                            } else {
                                                Modifier.background(Color.Transparent)
                                            }
                                        )
                                        .clickable { viewModel.switchInterval(interval) }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = if (isSelected) OnAccent else NeoTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Analytics Clarity Card with Capsule Bar Chart
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(24.dp),
                                ambientColor = Color(0x0A0F172A),
                                spotColor = Color(0x0A0F172A)
                            ),
                        shape = RoundedCornerShape(24.dp),
                        color = NeoCard,
                        border = BorderStroke(1.dp, NeoBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Date Range Carousel Control
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(NeoCardLow)
                                        .clickable { viewModel.shiftPeriod(-1) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChevronLeft,
                                        contentDescription = "Previous Period",
                                        tint = NeoTextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(20.dp))

                                Text(
                                    text = state.periodLabel,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = NeoTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )

                                Spacer(modifier = Modifier.width(20.dp))

                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(NeoCardLow)
                                        .alpha(if (state.canGoForward) 1f else 0.35f)
                                        .clickable(enabled = state.canGoForward) { viewModel.shiftPeriod(1) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = "Next Period",
                                        tint = NeoTextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Total Financial Metric Breakdown
                            Text(
                                text = lkr(state.totalSpentLkr, 2),
                                style = MaterialTheme.typography.headlineLarge,
                                color = NeoTextPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 28.sp,
                                letterSpacing = (-0.5).sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Spent · ${lkr(state.incomeLkr)} received in the same period",
                                style = MaterialTheme.typography.bodySmall,
                                color = NeoTextSecondary,
                                fontSize = 13.sp
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Capsule Bar Chart (7-day timeline)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                state.bars.forEachIndexed { index, day ->
                                    val isSelected = index == state.selectedBarIndex
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .clickable { viewModel.selectBar(index) }
                                            .padding(horizontal = 2.dp)
                                    ) {
                                        // Vertical Capsule Container (18dp x 144dp)
                                        Box(
                                            modifier = Modifier
                                                .width(18.dp)
                                                .height(144.dp)
                                                .clip(CircleShape)
                                                .background(NeoContainer)
                                                .padding(2.dp),
                                            contentAlignment = Alignment.BottomCenter
                                        ) {
                                            // Filled Bar Portion
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .fillMaxHeight(day.fillRatio)
                                                    .clip(CircleShape)
                                                    .then(
                                                        if (isSelected) {
                                                            Modifier
                                                                .shadow(8.dp, CircleShape, spotColor = NeoGreen)
                                                                .background(NeoGreen)
                                                        } else {
                                                            Modifier.background(NeoGreen)
                                                        }
                                                    )
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Day number (e.g. 17, 18, 23)
                                        Text(
                                            text = day.primaryLabel,
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                            color = if (isSelected) NeoGreenDark else NeoTextPrimary
                                        )
                                        // Day of week (e.g. Sun, Mon)
                                        Text(
                                            text = day.secondaryLabel,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) NeoGreenDark else NeoTextSecondary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Capsule Selected Stat Popover Note
                            val infiniteTransition = rememberInfiniteTransition(label = "peakPulse")
                            val dotScale by infiniteTransition.animateFloat(
                                initialValue = 0.85f,
                                targetValue = 1.25f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(900),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "scale"
                            )
                            val dotAlpha by infiniteTransition.animateFloat(
                                initialValue = 0.6f,
                                targetValue = 1.0f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(900),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "alpha"
                            )

                            val selectedDay = state.selectedBar
                            if (selectedDay != null) Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = NeoCardLow,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .scale(dotScale)
                                                .alpha(dotAlpha)
                                                .clip(CircleShape)
                                                .background(NeoGreen)
                                        )
                                        val unit = if (state.interval == AnalyticsInterval.WEEKLY) "day" else "week"
                                        val labelText = if (selectedDay.isPeak) {
                                            "Peak $unit: ${selectedDay.primaryLabel} ${selectedDay.secondaryLabel}"
                                        } else {
                                            "Spent: ${selectedDay.primaryLabel} ${selectedDay.secondaryLabel}"
                                        }
                                        Text(
                                            text = labelText,
                                            fontSize = 12.sp,
                                            color = NeoTextSecondary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    Text(
                                        text = "LKR ${String.format("%,.2f", selectedDay.amountLkr)}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeoTextPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Committed vs discretionary for the selected period
                item {
                    CommittedVsDiscretionaryBar(
                        committedAmountLkr = state.committedLkr,
                        discretionaryAmountLkr = state.discretionaryLkr
                    )
                }

                // 4. Where the money went: Category Donut Chart (FR-ANA-1) & Linear Breakdown
                if (state.categoryTotals.isNotEmpty()) {
                    item {
                        CategoryDonutChart(
                            categoryTotals = state.categoryTotals,
                            totalSpentLkr = state.totalSpentLkr
                        )
                    }
                    item {
                        CategoryBreakdownCard(state.categoryTotals)
                    }
                }

                // 5. Transactions Header with Search & Category Filter (FR-ANA-5)
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Transactions",
                                style = MaterialTheme.typography.titleMedium,
                                color = NeoTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )

                            if (state.transactions.size > 3) {
                                Text(
                                    text = if (state.showAllTransactions) "Show less" else "View all (${state.transactions.size})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeoGreenDark,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { viewModel.toggleViewAll() }
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Search Bar
                        OutlinedTextField(
                            value = state.searchQuery,
                            onValueChange = viewModel::onSearchQueryChanged,
                            placeholder = { Text("Search by merchant or note...", fontSize = 13.sp, color = NeoTextTertiary) },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = NeoTextSecondary, modifier = Modifier.size(18.dp))
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeoGreen,
                                unfocusedBorderColor = NeoBorder,
                                focusedTextColor = NeoTextPrimary,
                                unfocusedTextColor = NeoTextPrimary
                            )
                        )

                        // Category Filter Chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item {
                                FilterChip(
                                    selected = state.filterCategory == null,
                                    onClick = { viewModel.onCategoryFilterChanged(null) },
                                    label = { Text("All", fontSize = 11.5.sp) },
                                    shape = CircleShape,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NeoGreen,
                                        selectedLabelColor = OnAccent,
                                        labelColor = NeoTextSecondary
                                    )
                                )
                            }
                            items(ExpenseCategory.entries.size) { idx ->
                                val cat = ExpenseCategory.entries[idx]
                                FilterChip(
                                    selected = state.filterCategory == cat,
                                    onClick = {
                                        viewModel.onCategoryFilterChanged(if (state.filterCategory == cat) null else cat)
                                    },
                                    label = { Text(cat.displayName, fontSize = 11.5.sp) },
                                    shape = CircleShape,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NeoGreen,
                                        selectedLabelColor = OnAccent,
                                        labelColor = NeoTextSecondary
                                    )
                                )
                            }
                        }

                        // Date range + account filter chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item {
                                FilterChip(
                                    selected = state.filterDateRange != null,
                                    onClick = { showDateRangePicker = true },
                                    label = { Text(state.dateRangeLabel ?: "Any date", fontSize = 11.5.sp) },
                                    leadingIcon = {
                                        Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(14.dp))
                                    },
                                    trailingIcon = if (state.filterDateRange != null) {
                                        {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Clear date filter",
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clickable { viewModel.onDateRangeChanged(null) }
                                            )
                                        }
                                    } else null,
                                    shape = CircleShape,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NeoGreen,
                                        selectedLabelColor = OnAccent,
                                        selectedLeadingIconColor = OnAccent,
                                        selectedTrailingIconColor = OnAccent,
                                        labelColor = NeoTextSecondary,
                                        iconColor = NeoTextSecondary
                                    )
                                )
                            }
                            if (state.accounts.isNotEmpty()) item {
                                FilterChip(
                                    selected = state.filterAccountId == null,
                                    onClick = { viewModel.onAccountFilterChanged(null) },
                                    label = { Text("All accounts", fontSize = 11.5.sp) },
                                    shape = CircleShape,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NeoGreen,
                                        selectedLabelColor = OnAccent,
                                        labelColor = NeoTextSecondary
                                    )
                                )
                            }
                            items(state.accounts, key = { it.id }) { account ->
                                FilterChip(
                                    selected = state.filterAccountId == account.id,
                                    onClick = {
                                        viewModel.onAccountFilterChanged(if (state.filterAccountId == account.id) null else account.id)
                                    },
                                    label = { Text(account.name, fontSize = 11.5.sp) },
                                    leadingIcon = {
                                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(14.dp))
                                    },
                                    shape = CircleShape,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NeoGreen,
                                        selectedLabelColor = OnAccent,
                                        selectedLeadingIconColor = OnAccent,
                                        labelColor = NeoTextSecondary,
                                        iconColor = NeoTextSecondary
                                    )
                                )
                            }
                        }
                    }
                }

                // 6. Ledger Stack (income and expenses in this period)
                val visibleTransactions = if (state.showAllTransactions) {
                    state.transactions
                } else {
                    state.transactions.take(3)
                }

                if (!state.isLoading && visibleTransactions.isEmpty()) item {
                    EmptyStateView(
                        title = if (state.hasActiveFilters) "No matching transactions" else "Nothing recorded in this period",
                        message = if (state.hasActiveFilters) {
                            "Try a different keyword, category, account or date range."
                        } else {
                            "Expenses and received income for ${state.periodLabel.lowercase()} will appear here."
                        },
                        icon = Icons.Default.Payments,
                        illustration = R.drawable.illustration_no_data
                    )
                }

                items(visibleTransactions, key = { it.id }) { tx ->
                    TransactionLedgerCard(tx = tx)
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp)) // Clearance for bottom dock
                }
            }
        }
    }
}

/** Date-range filter for the transaction list (FR-ANA-5). Returns a local-time [Period]; the end day is inclusive. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionDateRangeDialog(
    initialRange: Period?,
    onConfirm: (Period) -> Unit,
    onDismiss: () -> Unit
) {
    val pickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = initialRange?.startMillis?.let(::localDayToUtc),
        initialSelectedEndDateMillis = initialRange?.let { localDayToUtc(it.endMillis - DAY_MILLIS) }
    )
    val start = pickerState.selectedStartDateMillis
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = start != null,
                onClick = {
                    if (start != null) {
                        val end = pickerState.selectedEndDateMillis ?: start
                        onConfirm(Period(utcDayToLocal(start), utcDayToLocal(end) + DAY_MILLIS))
                    }
                }
            ) { Text("Apply", color = NeoGreenDark, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = NeoTextSecondary) }
        }
    ) {
        DateRangePicker(
            state = pickerState,
            modifier = Modifier.height(500.dp),
            title = { Text("Filter by date", modifier = Modifier.padding(start = 24.dp, top = 16.dp)) }
        )
    }
}

/** The picker works in UTC midnights; convert to and from the start of the same calendar day in local time. */
private fun utcDayToLocal(utcMillis: Long): Long {
    val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = utcMillis }
    return Calendar.getInstance().apply {
        clear()
        set(utc.get(Calendar.YEAR), utc.get(Calendar.MONTH), utc.get(Calendar.DAY_OF_MONTH))
    }.timeInMillis
}

private fun localDayToUtc(localMillis: Long): Long {
    val local = Calendar.getInstance().apply { timeInMillis = localMillis }
    return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        clear()
        set(local.get(Calendar.YEAR), local.get(Calendar.MONTH), local.get(Calendar.DAY_OF_MONTH))
    }.timeInMillis
}

@Composable
private fun CategoryBreakdownCard(totals: List<CategoryTotal>) {
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
                text = "WHERE IT WENT",
                style = MaterialTheme.typography.labelSmall,
                color = NeoTextSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )
            totals.forEach { total ->
                val visual = expenseCategoryVisual(total.category)
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
                            Text(text = total.category.displayName, fontSize = 13.sp, color = NeoTextPrimary)
                        }
                        Text(
                            text = "${lkr(total.amountLkr)} · ${(total.share * 100).toInt()}%",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeoTextPrimary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(NeoContainer)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(total.share.coerceAtLeast(0.02f))
                                .fillMaxHeight()
                                .clip(CircleShape)
                                .background(if (total.category.isTypicallyCommitted) NeoTextSecondary else NeoGreen)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionLedgerCard(tx: TransactionItem) {
    val visual = transactionVisual(tx)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color(0x060F172A),
                spotColor = Color(0x060F172A)
            ),
        shape = RoundedCornerShape(16.dp),
        color = NeoCard,
        border = BorderStroke(1.dp, NeoBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(visual.container),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = visual.icon,
                        contentDescription = tx.title,
                        tint = visual.tint,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tx.title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = NeoTextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tx.subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = NeoTextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = signedLkr(tx.amountLkr),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (tx.isIncoming) NeoGreenDark else NeoTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = tx.originalAmountLabel ?: relativeDayLabel(tx.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = NeoTextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}
