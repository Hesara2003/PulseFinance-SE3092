package com.se3092.pulsefinance.ui.screens.income

import java.util.Locale
import com.se3092.pulsefinance.ui.theme.pulseColors
import com.se3092.pulsefinance.ui.components.ProfileAvatar
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import com.se3092.pulsefinance.R
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material.icons.filled.Tune
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
import com.se3092.pulsefinance.ui.common.formatDate
import com.se3092.pulsefinance.ui.common.incomeSourceIllustration
import com.se3092.pulsefinance.ui.common.lkr
import com.se3092.pulsefinance.ui.common.signedLkr
import com.se3092.pulsefinance.ui.components.EmptyStateView
import com.se3092.pulsefinance.ui.components.PulseNoticeHost

@Composable
fun IncomeScreen(
    viewModel: IncomeViewModel
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
            // Top Navigation & Branding Header (Exact HTML replica)
            // =========================================================================
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Profile Avatar Circle
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainer),
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
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp,
                                letterSpacing = (-0.5).sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Received cash vs. unpaid invoices",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Payslip & EPF Button (FR-LK-1, FR-LK-3)
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                    .clickable { viewModel.openPayslipSheet() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = "Payslip & EPF Calculator",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Add Income Action Button
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .clickable { viewModel.showAddSheet() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Record income",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }

                // EPF Wealth Accumulation Card (FR-LK-3)
                if (state.totalEpfAccumulatedLkr > 0) item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.08f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Savings, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(20.dp))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Accumulated EPF Wealth",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.pulseColors.textTertiary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.5.sp,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = lkr(state.totalEpfAccumulatedLkr),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = "Employer 12% + Employee 8% social savings you already own",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.5.sp
                                )
                            }
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
                                ambientColor = MaterialTheme.pulseColors.shadow.copy(alpha = 0.04f),
                                spotColor = MaterialTheme.pulseColors.shadow.copy(alpha = 0.04f)
                            ),
                        shape = RoundedCornerShape(28.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
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
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
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
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        letterSpacing = 1.sp
                                    )

                                    val realizedPct = (state.realizedRatio * 100).toInt()
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = "$realizedPct% collected",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Inflow Amount & Subtitle
                                Text(
                                    text = lkr(state.realizedThisMonthLkr, 2),
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 30.sp,
                                    letterSpacing = (-0.5).sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Received this month across every source",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                                                    .background(MaterialTheme.colorScheme.primary)
                                            )
                                            Text(
                                                text = "Received",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = lkr(state.realizedThisMonthLkr, 2),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
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
                                                    .background(MaterialTheme.pulseColors.warning)
                                            )
                                            Text(
                                                text = if (state.overdueLkr > 0) "Unpaid (${lkr(state.overdueLkr)} late)" else "Unpaid invoices",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = lkr(state.pendingLkr, 2),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
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
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
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
                                                    .background(MaterialTheme.colorScheme.primary)
                                            )
                                        }
                                        if (animatedPendingWidth > 0.01f) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(animatedPendingWidth.coerceAtLeast(0.01f))
                                                    .fillMaxHeight()
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.pulseColors.warning)
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
                                        text = "${String.format(Locale.US, "%.1f%%", state.realizedRatio * 100)} collected",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${String.format(Locale.US, "%.1f%%", state.pendingRatio * 100)} still owed",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                        // Restored from the 15/10 redesign: foreign-currency income, on top of the source filter.
                        if (state.foreignCurrencyCount > 0) {
                            CategoryPill(
                                label = "USD & crypto (${state.foreignCurrencyCount})",
                                isSelected = state.foreignCurrencyOnly,
                                onClick = { viewModel.toggleForeignCurrencyFilter() }
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

                // Monthly income that hasn't landed this month yet — one tap to add it (FR-INC-11)
                items(state.recurringDue, key = { "recurring_${it.id}" }) { income ->
                    RecurringIncomeCard(
                        income = income,
                        onAdd = { viewModel.addRecurring(income) },
                        onDismiss = { viewModel.dismissRecurring(income) }
                    )
                }

                // 6. Income records
                if (!state.isLoading && state.incomes.isEmpty()) item {
                    EmptyStateView(
                        title = when {
                            state.totalCount == 0 -> "No income recorded yet"
                            state.filterSource != null -> "Nothing from ${state.filterSource?.displayName} yet"
                            else -> "Nothing in this filter yet"
                        },
                        message = "Tap + to record salary, a freelance milestone (paid or still invoiced), AdSense in USD, or a crypto gain or loss.",
                        icon = Icons.Default.Payments,
                        illustration = R.drawable.illustration_wallet
                    )
                }

                items(state.incomes, key = { it.id }) { income ->
                    IncomeRecordCard(
                        income = income,
                        onMarkReceived = { viewModel.markReceived(income) },
                        onEdit = { viewModel.showEditSheet(income) },
                        onDelete = { viewModel.deleteIncome(income) }
                    )
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
                onCreditedLkrChange = viewModel::onCreditedLkrChanged,
                onToggleCreditedLkrMode = viewModel::onToggleCreditedLkrMode,
                onStatusChange = viewModel::onStatusChanged,
                onDayOffsetChange = viewModel::onDayOffsetChanged,
                onCryptoLossToggle = viewModel::onCryptoLossToggled,
                onRecurringToggle = viewModel::onRecurringToggled,
                onSubmit = viewModel::submitIncome,
                onDismiss = viewModel::hideAddSheet
            )
        }

        // =============================================================================
        // Sri Lankan Payslip & EPF Bottom Sheet (FR-LK-1, FR-LK-2, FR-LK-3)
        // =============================================================================
        if (state.isPayslipSheetVisible) {
            val apitBands by viewModel.apitBands.collectAsStateWithLifecycle()
            PayslipBottomSheet(
                calculator = viewModel.calculatePayslipUseCase,
                apitBands = apitBands,
                onSaveSalary = viewModel::savePayslipSalary,
                onDismiss = viewModel::closePayslipSheet
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
                        .shadow(4.dp, CircleShape, spotColor = MaterialTheme.pulseColors.primaryGlow)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                } else {
                    Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow)
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
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
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
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "THIS MONTH BY SOURCE",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )
            breakdown.forEach { item ->
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
                            Image(
                                painter = painterResource(incomeSourceIllustration(item.source)),
                                contentDescription = null,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(text = item.source.displayName, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                        }
                        Text(
                            text = if (item.count == 0) "—" else lkr(item.amountLkr),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (item.amountLkr < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        if (share > 0f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(share)
                                    .fillMaxHeight()
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
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
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.pulseColors.violet.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(incomeSourceIllustration(IncomeSource.CRYPTO)),
                contentDescription = null,
                modifier = Modifier.size(44.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Crypto net, year to date", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    text = "$entries realised gain/loss entr${if (entries == 1) "y" else "ies"} this year",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = signedLkr(netLkr),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = if (isUp) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}

/** "Add this month's salary": suggested from what he received before, never added on its own. */
@Composable
private fun RecurringIncomeCard(
    income: IncomeStream,
    onAdd: () -> Unit,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Autorenew, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Text(
                    text = "Add this month's ${income.title}?",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${lkr(income.amountInLkr)} last time, on ${formatDate(income.dateReceivedOrExpected)}. Add it once it has landed.",
                fontSize = 12.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = onAdd,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) { Text("Add ${lkr(income.amountInLkr)}", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                TextButton(onClick = onDismiss) {
                    Text("Not yet", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun IncomeRecordCard(
    income: IncomeStream,
    onMarkReceived: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val status = income.effectiveStatus()
    val (statusLabel, statusBg, statusText) = when (status) {
        InvoiceStatus.RECEIVED -> Triple(if (income.isLoss) "Loss" else "Received", if (income.isLoss) MaterialTheme.colorScheme.error.copy(alpha = 0.12f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), if (income.isLoss) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
        InvoiceStatus.PENDING_INVOICE -> Triple("Awaiting payment", MaterialTheme.pulseColors.warning.copy(alpha = 0.15f), MaterialTheme.pulseColors.warning)
        InvoiceStatus.OVERDUE -> Triple("Overdue", MaterialTheme.colorScheme.error.copy(alpha = 0.12f), MaterialTheme.colorScheme.error)
    }
    val dateLine = when (status) {
        InvoiceStatus.RECEIVED -> "Received ${formatDate(income.dateReceivedOrExpected)}"
        InvoiceStatus.PENDING_INVOICE -> "Due ${formatDate(income.dateReceivedOrExpected)}"
        InvoiceStatus.OVERDUE -> {
            val days = income.daysOverdue()
            "Due ${formatDate(income.dateReceivedOrExpected)} · $days day${if (days > 1) "s" else ""} late"
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = MaterialTheme.pulseColors.shadow.copy(alpha = 0.03f),
                spotColor = MaterialTheme.pulseColors.shadow.copy(alpha = 0.03f)
            )
            .animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
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
                    Image(
                        painter = painterResource(incomeSourceIllustration(income.source)),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = income.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${income.clientOrPlatform} · ${income.source.displayName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (income.currency == Currency.LKR) lkr(income.amountInLkr, 2)
                        else "${income.currency.name} ${"%,.2f".format(Locale.US, income.originalAmount)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (income.isLoss) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
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
                color = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.7f),
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
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (income.currency == Currency.LKR) dateLine
                            else "1 ${income.currency.name} = ${"%,.2f".format(Locale.US, income.exchangeRateToLkr)} LKR · $dateLine",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (income.currency != Currency.LKR) {
                        Text(
                            text = "≈ ${lkr(income.amountInLkr)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onEdit) {
                        Text(text = "Edit", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    TextButton(onClick = onDelete) {
                        Text(text = "Delete", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                if (status != InvoiceStatus.RECEIVED) {
                    Button(
                        onClick = onMarkReceived,
                        modifier = Modifier
                            .heightIn(min = 40.dp)
                            .shadow(6.dp, CircleShape, spotColor = MaterialTheme.pulseColors.primaryGlow),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
                        Icon(Icons.Default.Autorenew, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                        Text(text = "Monthly", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
