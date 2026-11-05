package com.se3092.pulsefinance.ui.screens.dashboard

import com.se3092.pulsefinance.ui.common.presetIllustration
import com.se3092.pulsefinance.ui.common.transactionIllustration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material3.AlertDialog
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import com.se3092.pulsefinance.domain.model.Account
import com.se3092.pulsefinance.ui.common.BiometricHelper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.se3092.pulsefinance.R
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.se3092.pulsefinance.ui.common.compactAmount
import com.se3092.pulsefinance.ui.common.lkr
import com.se3092.pulsefinance.ui.common.signedLkr
import com.se3092.pulsefinance.ui.components.CommittedVsDiscretionaryBar
import com.se3092.pulsefinance.ui.components.ExpenseItemCard
import com.se3092.pulsefinance.ui.components.ProfileAvatar
import com.se3092.pulsefinance.ui.components.QuestCard
import com.se3092.pulsefinance.ui.screens.expense.AddExpenseBottomSheet
import com.se3092.pulsefinance.ui.theme.Display
import com.se3092.pulsefinance.ui.theme.NeoBorder
import com.se3092.pulsefinance.ui.theme.Numeric
import com.se3092.pulsefinance.ui.theme.OnAccent
import com.se3092.pulsefinance.ui.theme.Saffron
import com.se3092.pulsefinance.ui.theme.SaffronSoft
import com.se3092.pulsefinance.ui.theme.Teal
import com.se3092.pulsefinance.ui.theme.WarmAmber
import com.se3092.pulsefinance.ui.theme.NeoCard
import com.se3092.pulsefinance.ui.theme.NeoCardLow
import com.se3092.pulsefinance.ui.theme.NeoContainer
import com.se3092.pulsefinance.ui.theme.NeoContainerHigh
import com.se3092.pulsefinance.ui.theme.NeoErrorContainer
import com.se3092.pulsefinance.ui.theme.NeoGreen
import com.se3092.pulsefinance.ui.theme.NeoGreenDark
import com.se3092.pulsefinance.ui.theme.NeoGreenGlow
import com.se3092.pulsefinance.ui.theme.NeoRose
import com.se3092.pulsefinance.ui.theme.NeoSurface
import com.se3092.pulsefinance.ui.theme.NeoTextPrimary
import com.se3092.pulsefinance.ui.theme.NeoTextSecondary
import com.se3092.pulsefinance.ui.theme.NeoTextTertiary
import com.se3092.pulsefinance.ui.theme.NeoTrack
import com.se3092.pulsefinance.ui.components.PulseNoticeHost
import com.se3092.pulsefinance.ui.common.accountIllustration

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToIncome: () -> Unit,
    onNavigateToGoal: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onSignOut: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var isBalanceMasked by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = NeoSurface,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddExpense() },
                containerColor = NeoGreen,
                contentColor = OnAccent,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 76.dp)
                    .shadow(14.dp, CircleShape, spotColor = NeoGreenGlow)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // =========================================================================
            // 1. Greeting with the user's generated avatar
            // =========================================================================
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfileAvatar(initials = state.initials, size = 48.dp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = state.greeting,
                            fontSize = 13.sp,
                            color = NeoTextSecondary
                        )
                        Text(
                            text = state.firstName,
                            fontFamily = Display,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            color = NeoTextPrimary
                        )
                    }
                    IconButton(onClick = { isBalanceMasked = !isBalanceMasked }) {
                        Icon(
                            imageVector = if (isBalanceMasked) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Hide amounts",
                            tint = NeoTextSecondary
                        )
                    }
                    IconButton(onClick = { viewModel.exportCsv(context) }) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = "Export CSV",
                            tint = NeoTextSecondary
                        )
                    }
                    IconButton(onClick = {
                        viewModel.signOut()
                        onSignOut()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Sign out",
                            tint = NeoTextTertiary
                        )
                    }
                }
            }

            // =========================================================================
            // 2. Hero: what Kavindu can spend today, and how the month is going
            // =========================================================================
            item {
                SafeToSpendHero(
                    dailyAllowanceLkr = state.summary.safeToSpendDailyLkr,
                    daysRemaining = state.summary.daysRemainingInMonth,
                    monthLabel = state.monthLabel,
                    netLkr = state.summary.netSavingsLkr,
                    inLkr = state.summary.totalRealizedIncomeLkr,
                    outLkr = state.summary.totalExpensesLkr,
                    incomeBasisNote = if (state.summary.incomeBasisIsAverage)
                        "Based on your 3-month average income (${lkr(state.summary.incomeBasisLkr)}) until this month's salary lands."
                    else null,
                    masked = isBalanceMasked,
                    isLoading = state.isLoading
                )
            }

            // =========================================================================
            // 2b. Sri Lankan Seasonal Peaks Proactive Alert (FR-LK-4, FR-LK-5)
            // =========================================================================
            state.seasonalAlert?.let { alert ->
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = NeoCard,
                        border = BorderStroke(1.dp, Saffron.copy(alpha = 0.5f)),
                        shadowElevation = 3.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(SaffronSoft),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AcUnit,
                                        contentDescription = null,
                                        tint = Saffron,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = alert.festivalName,
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeoTextPrimary
                                    )
                                    Text(
                                        text = "Peak in ${alert.daysUntilPeak} days · Set aside ${lkr(alert.suggestedSetAsideLkr)}",
                                        fontSize = 12.sp,
                                        color = Saffron,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            Text(
                                text = alert.message,
                                fontSize = 12.sp,
                                color = NeoTextSecondary,
                                lineHeight = 17.sp
                            )
                            // One-tap set-aside: holds the amount back from Safe-to-Spend until the peak (FR-LK-5)
                            val reserve = state.seasonalReserve
                            if (reserve != null) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(SaffronSoft)
                                        .padding(start = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Saffron,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "${lkr(reserve.amountLkr)} set aside · held back from Safe-to-Spend",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = NeoTextPrimary,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp)
                                    )
                                    TextButton(onClick = viewModel::undoSeasonalSetAside) {
                                        Text("Undo", color = Saffron, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            } else if (alert.suggestedSetAsideLkr > 0) {
                                Button(
                                    onClick = viewModel::setAsideForSeasonalPeak,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Saffron, contentColor = Color.White)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Savings,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Set aside ${lkr(alert.suggestedSetAsideLkr)}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // 2c. Recorded Accounts & Spending Freeze Section (FR-ACC-1...7)
            // =========================================================================
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Accounts & Channels",
                                style = MaterialTheme.typography.titleMedium,
                                color = NeoTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "Combined balance: ${if (isBalanceMasked) "••••••" else lkr(state.combinedBalanceLkr)}",
                                fontSize = 12.sp,
                                color = NeoTextSecondary
                            )
                        }
                        if (state.totalFreezeBreaks > 0) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(NeoErrorContainer)
                                    .padding(horizontal = 9.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "${state.totalFreezeBreaks} Freeze Break${if (state.totalFreezeBreaks > 1) "s" else ""}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeoRose
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        state.accounts.forEach { account ->
                            AccountCardItem(
                                account = account,
                                isBalanceMasked = isBalanceMasked,
                                isRevealed = state.revealedAccountIds.contains(account.id),
                                onRevealClick = {
                                    val activity = context as? FragmentActivity
                                    if (activity != null) {
                                        BiometricHelper.authenticate(
                                            activity = activity,
                                            title = "Reveal ${account.name} Number",
                                            subtitle = "Device authentication required to view sensitive account details",
                                            onSuccess = { viewModel.setAccountRevealed(account.id, true) },
                                            onError = { /* keep masked on error */ }
                                        )
                                    } else {
                                        viewModel.setAccountRevealed(account.id, true)
                                    }
                                },
                                onCopyClick = {
                                    BiometricHelper.copyWithAutoWipe(
                                        context = context,
                                        label = "${account.name} Account Number",
                                        text = account.accountNumber,
                                        scope = coroutineScope,
                                        wipeDelaySeconds = 60L
                                    ) {
                                        viewModel.setAccountRevealed(account.id, false)
                                    }
                                },
                                onFreezeToggle = {
                                    if (account.isFrozen) viewModel.unfreezeAccount(account)
                                    else viewModel.freezeAccount(account, 14)
                                }
                            )
                        }
                    }
                }
            }

            // =========================================================================
            // 3. Money waiting: unpaid invoices framed as money owed to him, not an alarm
            // =========================================================================
            if (state.unpaidInvoices.isNotEmpty()) item {
                val firstUnpaid = state.unpaidInvoices.first()
                val hasOverdue = state.overdueInvoiceCount > 0
                MoneyWaitingCard(
                    amount = lkr(state.summary.pendingIncomeLkr),
                    detail = if (hasOverdue)
                        "${firstUnpaid.clientOrPlatform.ifBlank { firstUnpaid.title }} is past the due date. Tap to follow up."
                    else
                        "${state.unpaidInvoices.size} invoice${if (state.unpaidInvoices.size > 1) "s" else ""} not paid yet. Tap to review.",
                    onClick = onNavigateToIncome
                )
            }

            // =========================================================================
            // 3b. MacBook Quest
            // =========================================================================
            item {
                val quest = state.quest
                if (quest != null) {
                    QuestCard(quest = quest, initials = state.initials, onClick = onNavigateToGoal)
                } else if (!state.isLoading) {
                    SetGoalPromptCard(onClick = onNavigateToGoal)
                }
            }

            // =========================================================================
            // 4. 3-Tap Quick Presets Carousel
            // =========================================================================
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Log it in one tap",
                            style = MaterialTheme.typography.titleMedium,
                            color = NeoTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Your usual spends",
                            fontSize = 11.5.sp,
                            color = NeoTextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        // Top, so the shorter "Add" tile (no amount line) lines up with the presets
                        verticalAlignment = Alignment.Top
                    ) {
                        // "+ Add" circular button
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { viewModel.openAddExpense() }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(NeoContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Custom",
                                    tint = NeoTextSecondary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Add",
                                fontSize = 11.sp,
                                color = NeoTextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // The user's own repeated spends (starter templates until habits emerge)
                        state.presets.forEach { preset ->
                            QuickPresetBadge(
                                illustration = presetIllustration(preset.title, preset.category),
                                label = preset.title,
                                amount = preset.amountLkr?.let(::compactAmount)
                            ) {
                                viewModel.openAddExpense(preset)
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // 5. Recent Transactions Section (Horizontal Slider Replica)
            // =========================================================================
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Lately",
                            style = MaterialTheme.typography.titleMedium,
                            color = NeoTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "View all",
                            fontSize = 12.sp,
                            color = NeoGreenDark,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { onNavigateToAnalytics() }
                        )
                    }

                    if (state.recentTransactions.isEmpty()) {
                        Text(
                            text = "Nothing logged yet. Tap + to record your first expense, or add income from the Income hub.",
                            fontSize = 12.5.sp,
                            color = NeoTextSecondary,
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    } else {
                        // Horizontal Cards Slider — newest income and expenses together
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            state.recentTransactions.forEach { tx ->
                                TransactionSquareCard(
                                    currency = tx.originalAmountLabel?.substringBefore(' ') ?: "LKR",
                                    amount = tx.originalAmountLabel?.substringAfter(' ')
                                        ?: "%,.0f".format(kotlin.math.abs(tx.amountLkr)),
                                    title = tx.title,
                                    illustration = transactionIllustration(tx),
                                    isIncoming = tx.isIncoming
                                )
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // 6. Weekly Velocity Teaser Card
            // =========================================================================
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = NeoCard,
                    border = BorderStroke(1.dp, NeoBorder),
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(7.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(NeoGreen)
                                )
                                Column {
                                    Text(
                                        text = "Discretionary this week",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeoTextPrimary
                                    )
                                    Text(
                                        text = lkr(state.weekDiscretionaryLkr),
                                        fontSize = 11.5.sp,
                                        color = NeoTextSecondary
                                    )
                                }
                            }
                            // Fewer discretionary rupees than last week is the win worth showing.
                            state.weekChangePct?.let { change ->
                                Text(
                                    text = if (change <= 0) "${-change}% less than last week" else "+$change% vs last week",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (change <= 0) NeoGreenDark else NeoRose
                                )
                            }
                        }

                        // Micro Capsule 7-Day Chart (Mon..Sun of the current week)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            state.weekBars.forEach { bar ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(10.dp)
                                            .height(40.dp)
                                            .clip(CircleShape)
                                            .background(NeoContainerHigh),
                                        contentAlignment = Alignment.BottomCenter
                                    ) {
                                        if (bar.amountLkr > 0) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height((40 * bar.fillRatio).coerceAtLeast(4f).dp)
                                                    .clip(CircleShape)
                                                    .background(NeoGreen)
                                            )
                                        }
                                    }
                                    Text(
                                        text = bar.label,
                                        fontSize = 10.sp,
                                        color = if (bar.isToday) NeoGreenDark else NeoTextSecondary,
                                        fontWeight = if (bar.isToday) FontWeight.ExtraBold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // 7. Safe-to-Spend & Goal Pacing Cards (Extended Features)
            // =========================================================================
            if (!state.isLoading && !state.summary.hasAnyData) item {
                GettingStartedCard(
                    onLogExpense = { viewModel.openAddExpense() },
                    onAddIncome = onNavigateToIncome,
                    onSetGoal = onNavigateToGoal,
                    hasGoal = state.primaryGoal != null
                )
            }

            item {
                CommittedVsDiscretionaryBar(
                    committedAmountLkr = state.summary.committedExpensesLkr,
                    discretionaryAmountLkr = state.summary.discretionaryExpensesLkr
                )
            }

            // Real Expense Ledger List (if logged items exist)
            if (state.recentExpenses.isNotEmpty()) {
                item {
                    Text(
                        text = "Everything you logged",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeoTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                items(state.recentExpenses, key = { it.id }) { expense ->
                    ExpenseItemCard(
                        expense = expense,
                        onDelete = { viewModel.deleteExpense(expense.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(90.dp)) // Clearance for bottom navigation dock
            }
        }

        // Scaffold stacks its content from the top, so this floats over the list.
        PulseNoticeHost(notice = state.notice, onDismiss = viewModel::dismissNotice)

        if (state.addExpense.isVisible) {
            AddExpenseBottomSheet(
                form = state.addExpense,
                presets = state.presets,
                accounts = state.accounts,
                onTitleChange = viewModel::onTitleChanged,
                onAmountChange = viewModel::onAmountChanged,
                onCategoryChange = viewModel::onCategoryChanged,
                onCommittedToggle = viewModel::onCommittedToggled,
                onAccountSelect = viewModel::onAccountSelected,
                onDaysAgoSelect = viewModel::onDaysAgoSelected,
                onPresetSelect = viewModel::onPresetSelected,
                onSubmit = viewModel::submitExpense,
                onDismiss = viewModel::dismissAddExpense
            )
        }

        state.frozenWarning?.let { warning ->
            AlertDialog(
                onDismissRequest = { viewModel.dismissFrozenWarning() },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = NeoRose)
                        Text("Spending Freeze Active", fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "You placed a Spending Freeze on ${warning.account.name} to curb impulsive spending.",
                            fontSize = 13.5.sp,
                            color = NeoTextPrimary
                        )
                        Text(
                            text = "Spending ${lkr(warning.amountLkr)} on '${warning.title}' will delay your savings goal by ~${warning.goalDaysDelayed} day${if (warning.goalDaysDelayed > 1) "s" else ""}.",
                            fontSize = 13.sp,
                            color = NeoRose,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "We will never block your expense, but this will be recorded as a Freeze Break.",
                            fontSize = 12.sp,
                            color = NeoTextSecondary
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.confirmFrozenExpenseBreak() },
                        colors = ButtonDefaults.buttonColors(containerColor = NeoRose, contentColor = OnAccent)
                    ) {
                        Text("Log anyway", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissFrozenWarning() }) {
                        Text("Cancel", color = NeoTextSecondary)
                    }
                },
                containerColor = NeoCard,
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

@Composable
private fun GettingStartedCard(
    onLogExpense: () -> Unit,
    onAddIncome: () -> Unit,
    onSetGoal: () -> Unit,
    hasGoal: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = NeoCard,
        border = BorderStroke(1.dp, NeoBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Get your first clear picture",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = NeoTextPrimary
            )
            Text(
                text = "Three quick steps and this screen starts showing where your money really goes.",
                fontSize = 12.sp,
                color = NeoTextSecondary
            )
            GettingStartedStep("1", "Record this month's income — salary, freelance, AdSense or crypto", onAddIncome)
            GettingStartedStep("2", "Log today's spending — presets make it one tap", onLogExpense)
            if (!hasGoal) GettingStartedStep("3", "Set the goal you're saving for", onSetGoal)
        }
    }
}

@Composable
private fun GettingStartedStep(number: String, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NeoCardLow)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(NeoGreen),
            contentAlignment = Alignment.Center
        ) {
            Text(text = number, color = OnAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Text(text = text, fontSize = 12.5.sp, color = NeoTextPrimary, modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = NeoTextTertiary,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun SetGoalPromptCard(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = NeoCard,
        border = BorderStroke(1.dp, NeoBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.illustration_piggy_bank),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Set a savings goal", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeoTextPrimary)
                Text(
                    text = "See the exact monthly pace needed and when you'll get there",
                    fontSize = 12.sp,
                    color = NeoTextSecondary
                )
            }
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = NeoTextTertiary)
        }
    }
}

@Composable
fun QuickPresetBadge(
    @DrawableRes illustration: Int,
    label: String,
    /** Null for a starter shortcut: the user enters the amount. */
    amount: String?,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(64.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(NeoCardLow),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(illustration),
                contentDescription = null,
                modifier = Modifier.size(46.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = NeoTextPrimary,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (amount != null) Text(
            text = amount,
            fontFamily = Numeric,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = NeoTextTertiary
        )
    }
}

@Composable
fun TransactionSquareCard(
    currency: String,
    amount: String,
    title: String,
    @DrawableRes illustration: Int,
    isIncoming: Boolean
) {
    Surface(
        modifier = Modifier
            .size(width = 136.dp, height = 136.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = Color(0x1F0F172A),
                spotColor = Color(0x1A0F172A)
            ),
        shape = RoundedCornerShape(18.dp),
        color = NeoCard,
        border = BorderStroke(1.dp, NeoBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(13.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row: Amount & Arrow Glyph
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = currency,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = NeoTextSecondary
                    )
                    Text(
                        text = amount,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeoTextPrimary
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = NeoCardLow,
                    modifier = Modifier.size(26.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isIncoming)
                                Icons.Default.SouthWest
                            else
                                Icons.Default.NorthEast,
                            contentDescription = null,
                            tint = if (isIncoming) NeoGreenDark else NeoTextPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            // Bottom Row: Category Icon & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Image(
                    painter = painterResource(illustration),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = title,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = NeoTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * The first thing Kavindu sees: one number he can act on today. Net cash flow for the month
 * sits underneath as context rather than as the headline.
 */
@Composable
private fun SafeToSpendHero(
    dailyAllowanceLkr: Double,
    daysRemaining: Int,
    monthLabel: String,
    netLkr: Double,
    inLkr: Double,
    outLkr: Double,
    incomeBasisNote: String?,
    masked: Boolean,
    isLoading: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(28.dp), ambientColor = NeoGreenGlow, spotColor = NeoGreenGlow),
        shape = RoundedCornerShape(28.dp),
        color = NeoGreen
    ) {
        Box(
            modifier = Modifier.background(
                Brush.linearGradient(listOf(NeoGreen, Color(0xFF00A844)))
            )
        ) {
            // A rising sun of saffron arcs in the corner: decoration only.
            Canvas(
                modifier = Modifier
                    .size(180.dp)
                    .align(Alignment.TopEnd)
            ) {
                val c = Offset(size.width * 0.85f, size.height * 0.15f)
                for (i in 1..4) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.22f - i * 0.04f),
                        radius = size.minDimension * 0.18f * i,
                        center = c,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Color.White.copy(alpha = 0.55f), Color.White.copy(alpha = 0.18f), Color.White.copy(alpha = 0f)),
                        center = c,
                        radius = size.minDimension * 0.16f
                    ),
                    radius = size.minDimension * 0.16f,
                    center = c
                )
            }

            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "SAFE TO SPEND TODAY",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.4.sp
                )
                Text(
                    text = when {
                        isLoading -> "LKR —"
                        masked -> "LKR ••••"
                        else -> lkr(dailyAllowanceLkr)
                    },
                    fontFamily = Numeric,
                    fontWeight = FontWeight.Bold,
                    fontSize = 42.sp,
                    letterSpacing = (-1).sp,
                    color = Color.White
                )
                Text(
                    text = if (isLoading) "Working out today's number…"
                    else "after rent, bills and your MacBook saving · $daysRemaining days left in $monthLabel",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
                incomeBasisNote?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = it, fontSize = 11.5.sp, color = Color.White.copy(alpha = 0.75f))
                }
                if (!isLoading) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        HeroStat("This month", if (masked) "••••" else signedLkr(netLkr), Color.White)
                        HeroStat("In", if (masked) "••••" else compactAmount(inLkr), Color.White)
                        HeroStat("Out", if (masked) "••••" else compactAmount(outLkr), Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroStat(label: String, value: String, color: Color) {
    Column {
        Text(text = label, fontSize = 11.sp, color = Color.White.copy(alpha = 0.75f))
        Text(text = value, fontFamily = Numeric, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = color)
    }
}

/** Unpaid invoices are money owed to Kavindu, so they are framed as an opportunity, not a warning. */
@Composable
private fun MoneyWaitingCard(amount: String, detail: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = WarmAmber.copy(alpha = 0.10f),
        border = BorderStroke(1.dp, WarmAmber.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(WarmAmber.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.HourglassTop, contentDescription = null, tint = WarmAmber, modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$amount is waiting for you",
                    fontFamily = Display,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = NeoTextPrimary
                )
                Text(text = detail, fontSize = 12.sp, color = NeoTextSecondary)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = "Follow up", tint = WarmAmber)
        }
    }
}

@Composable
private fun AccountCardItem(
    account: Account,
    isBalanceMasked: Boolean,
    isRevealed: Boolean,
    onRevealClick: () -> Unit,
    onCopyClick: () -> Unit,
    onFreezeToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(260.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color(0x080F172A),
                spotColor = Color(0x080F172A)
            ),
        shape = RoundedCornerShape(20.dp),
        color = if (account.isFrozen) NeoCardLow else NeoCard,
        border = BorderStroke(1.dp, if (account.isFrozen) NeoRose.copy(alpha = 0.5f) else NeoBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (account.isFrozen) NeoErrorContainer else NeoCardLow),
                        contentAlignment = Alignment.Center
                    ) {
                        // A frozen account shows the lock, its status; otherwise what kind of account it is.
                        if (account.isFrozen) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Frozen",
                                tint = NeoRose,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Image(
                                painter = painterResource(accountIllustration(account.type)),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = account.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeoTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = account.type.displayName,
                            fontSize = 11.sp,
                            color = NeoTextSecondary
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = if (account.isFrozen) NeoRose else NeoContainer,
                    modifier = Modifier.clickable(onClick = onFreezeToggle)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (account.isFrozen) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = null,
                            tint = if (account.isFrozen) OnAccent else NeoTextSecondary,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = if (account.isFrozen) "Frozen" else "Freeze",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (account.isFrozen) OnAccent else NeoTextSecondary
                        )
                    }
                }
            }

            // Balance
            Text(
                text = if (isBalanceMasked) "••••••" else lkr(account.balanceLkr),
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NeoTextPrimary
            )

            // Sensitive Account Number Row (FR-ACC-6, FR-ACC-7)
            if (account.accountNumber.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = NeoCardLow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isRevealed) account.accountNumber else account.maskedAccountNumber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeoTextPrimary
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (!isRevealed) {
                                IconButton(onClick = onRevealClick, modifier = Modifier.size(24.dp)) {
                                    Icon(
                                        imageVector = Icons.Default.Visibility,
                                        contentDescription = "Reveal with biometric",
                                        tint = NeoGreenDark,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else {
                                IconButton(onClick = onCopyClick, modifier = Modifier.size(24.dp)) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy number (60s auto-wipe)",
                                        tint = NeoGreenDark,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

