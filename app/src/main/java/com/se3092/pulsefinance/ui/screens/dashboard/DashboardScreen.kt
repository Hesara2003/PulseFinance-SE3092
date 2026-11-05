package com.se3092.pulsefinance.ui.screens.dashboard

import com.se3092.pulsefinance.ui.theme.pulseColors
import com.se3092.pulsefinance.ui.common.shareCsv
import androidx.compose.foundation.ExperimentalFoundationApi
import com.se3092.pulsefinance.ui.components.DeleteTransactionDialog
import com.se3092.pulsefinance.ui.common.TransactionItem
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.foundation.combinedClickable
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.AlertDialog
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
import com.se3092.pulsefinance.ui.components.QuestCard
import com.se3092.pulsefinance.ui.screens.expense.AddExpenseBottomSheet
import com.se3092.pulsefinance.ui.theme.Display
import com.se3092.pulsefinance.ui.theme.Numeric
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
    var pendingDelete by remember { mutableStateOf<TransactionItem?>(null) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddExpense() },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 76.dp)
                    .shadow(14.dp, CircleShape, spotColor = MaterialTheme.pulseColors.primaryGlow)
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
            // Greeting, with hide-amounts, CSV export and sign-out
            item {
                Spacer(modifier = Modifier.height(10.dp))
                DashboardGreeting(
                    initials = state.initials,
                    greeting = state.greeting,
                    firstName = state.firstName,
                    isBalanceMasked = isBalanceMasked,
                    onToggleMask = { isBalanceMasked = !isBalanceMasked },
                    onExportCsv = { shareCsv(context, viewModel.exportCsv()) },
                    onSignOut = {
                        viewModel.signOut()
                        onSignOut()
                    }
                )
            }

            // Hero: what Kavindu can spend today, and how the month is going
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

            // Seasonal Peak alert (FR-LK-4, FR-LK-5)
            state.seasonalAlert?.let { alert ->
                item {
                    SeasonalPeakCard(
                        alert = alert,
                        reserve = state.seasonalReserve,
                        onSetAside = viewModel::setAsideForSeasonalPeak,
                        onUndoSetAside = viewModel::undoSeasonalSetAside
                    )
                }
            }

            // Emergency Runway Buffer (FR-AUTH-6)
            if (state.showRunwayCard) {
                item {
                    SafetyBufferCard(
                        state = state,
                        masked = isBalanceMasked,
                        onMonths = viewModel::setRunwayMonths,
                        onAdd = viewModel::addToRunway
                    )
                }
            }

            // Accounts and Spending Freeze (FR-ACC-1…7). Reveal and copy need the Activity, so they stay here.
            item {
                AccountsSection(
                    accounts = state.accounts,
                    combinedBalanceLkr = state.combinedBalanceLkr,
                    totalFreezeBreaks = state.totalFreezeBreaks,
                    revealedAccountIds = state.revealedAccountIds,
                    isBalanceMasked = isBalanceMasked,
                    onRevealAccount = { account ->
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
                    onCopyAccountNumber = { account ->
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
                    onToggleFreeze = { account ->
                        if (account.isFrozen) viewModel.unfreezeAccount(account)
                        else viewModel.freezeAccount(account, 14)
                    }
                )
            }

            // Money waiting: unpaid invoices framed as money owed to him, not an alarm
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

            // Goal quest
            item {
                val quest = state.quest
                if (quest != null) {
                    QuestCard(quest = quest, initials = state.initials, goalTitle = state.primaryGoal?.title.orEmpty(), onClick = onNavigateToGoal)
                } else if (!state.isLoading) {
                    SetGoalPromptCard(onClick = onNavigateToGoal)
                }
            }

            // One-tap presets
            item {
                QuickPresetsSection(
                    presets = state.presets,
                    onAddExpense = { viewModel.openAddExpense() },
                    onPreset = { preset -> viewModel.openAddExpense(preset) }
                )
            }

            // Lately: newest income and expenses
            item {
                LatelySection(
                    transactions = state.recentTransactions,
                    onViewAll = onNavigateToAnalytics,
                    onLongPressExpense = { tx -> pendingDelete = tx }
                )
            }

            // Discretionary spending this week
            item {
                WeeklyDiscretionaryCard(
                    weekDiscretionaryLkr = state.weekDiscretionaryLkr,
                    weekChangePct = state.weekChangePct,
                    weekBars = state.weekBars,
                    totalFreezeBreaks = state.totalFreezeBreaks
                )
            }

            // Getting started (no data yet) and committed vs discretionary
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

            item {
                Spacer(modifier = Modifier.height(90.dp)) // Clearance for bottom navigation dock
            }
        }

        // Scaffold stacks its content from the top, so this floats over the list.
        PulseNoticeHost(notice = state.notice, onDismiss = viewModel::dismissNotice)

        pendingDelete?.let { tx ->
            DeleteTransactionDialog(
                transaction = tx,
                onConfirm = {
                    tx.expenseId?.let(viewModel::deleteExpense)
                    pendingDelete = null
                },
                onDismiss = { pendingDelete = null }
            )
        }

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
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Text("Spending Freeze Active", fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "You placed a Spending Freeze on ${warning.account.name} to curb impulsive spending.",
                            fontSize = 13.5.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Spending ${lkr(warning.amountLkr)} on '${warning.title}' will delay your savings goal by ~${warning.goalDaysDelayed} day${if (warning.goalDaysDelayed > 1) "s" else ""}.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "We will never block your expense, but this will be recorded as a Freeze Break.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.confirmFrozenExpenseBreak() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                    ) {
                        Text("Log anyway", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissFrozenWarning() }) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
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
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Get your first clear picture",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Three quick steps and this screen starts showing where your money really goes.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(text = number, color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Text(text = text, fontSize = 12.5.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.pulseColors.textTertiary,
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
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
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
                Text(text = "Set a savings goal", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    text = "See the exact monthly pace needed and when you'll get there",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.pulseColors.textTertiary)
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
                .background(MaterialTheme.colorScheme.surfaceContainerLow),
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
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (amount != null) Text(
            text = amount,
            fontFamily = Numeric,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.pulseColors.textTertiary
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionSquareCard(
    currency: String,
    amount: String,
    title: String,
    @DrawableRes illustration: Int,
    isIncoming: Boolean,
    /** Long-press action, for expenses (delete); null for income. */
    onLongClick: (() -> Unit)? = null
) {
    val haptics = LocalHapticFeedback.current
    Surface(
        modifier = Modifier
            .size(width = 136.dp, height = 136.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = MaterialTheme.pulseColors.shadow.copy(alpha = 0.12f),
                spotColor = MaterialTheme.pulseColors.shadow.copy(alpha = 0.10f)
            )
            .clip(RoundedCornerShape(18.dp))
            .then(
                if (onLongClick != null) Modifier.combinedClickable(
                    onClickLabel = null,
                    onLongClickLabel = "Delete $title",
                    onClick = {},
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLongClick()
                    }
                ) else Modifier
            ),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = amount,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier.size(26.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isIncoming)
                                Icons.Default.SouthWest
                            else
                                Icons.Default.NorthEast,
                            contentDescription = null,
                            tint = if (isIncoming) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
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
                    color = MaterialTheme.colorScheme.onSurface,
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
            .shadow(16.dp, RoundedCornerShape(28.dp), ambientColor = MaterialTheme.pulseColors.primaryGlow, spotColor = MaterialTheme.pulseColors.primaryGlow),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Box(
            modifier = Modifier.background(
                Brush.linearGradient(listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.pulseColors.primaryDeep))
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
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
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
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = if (isLoading) "Working out today's number…"
                    else "after rent, bills and your goal saving · $daysRemaining days left in $monthLabel",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                )
                incomeBasisNote?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = it, fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f))
                }
                if (!isLoading) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        HeroStat("This month", if (masked) "••••" else signedLkr(netLkr), MaterialTheme.colorScheme.onPrimaryContainer)
                        HeroStat("In", if (masked) "••••" else compactAmount(inLkr), MaterialTheme.colorScheme.onPrimaryContainer)
                        HeroStat("Out", if (masked) "••••" else compactAmount(outLkr), MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroStat(label: String, value: String, color: Color) {
    Column {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f))
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
        color = MaterialTheme.pulseColors.warning.copy(alpha = 0.10f),
        border = BorderStroke(1.dp, MaterialTheme.pulseColors.warning.copy(alpha = 0.35f))
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
                    .background(MaterialTheme.pulseColors.warning.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.HourglassTop, contentDescription = null, tint = MaterialTheme.pulseColors.warning, modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$amount is waiting for you",
                    fontFamily = Display,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(text = detail, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = "Follow up", tint = MaterialTheme.pulseColors.warning)
        }
    }
}

@Composable
internal fun AccountCardItem(
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
                ambientColor = MaterialTheme.pulseColors.shadow.copy(alpha = 0.03f),
                spotColor = MaterialTheme.pulseColors.shadow.copy(alpha = 0.03f)
            ),
        shape = RoundedCornerShape(20.dp),
        color = if (account.isFrozen) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (account.isFrozen) MaterialTheme.colorScheme.error.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant)
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
                            .background(if (account.isFrozen) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceContainerLow),
                        contentAlignment = Alignment.Center
                    ) {
                        // A frozen account shows the lock, its status; otherwise what kind of account it is.
                        if (account.isFrozen) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Frozen",
                                tint = MaterialTheme.colorScheme.error,
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
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = account.type.displayName,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = if (account.isFrozen) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceContainer,
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
                            tint = if (account.isFrozen) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = if (account.isFrozen) "Frozen" else "Freeze",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (account.isFrozen) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Balance
            Text(
                text = if (isBalanceMasked) "••••••" else lkr(account.balanceLkr),
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Sensitive Account Number Row (FR-ACC-6, FR-ACC-7)
            if (account.accountNumber.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
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
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (!isRevealed) {
                                IconButton(onClick = onRevealClick, modifier = Modifier.size(24.dp)) {
                                    Icon(
                                        imageVector = Icons.Default.Visibility,
                                        contentDescription = "Reveal with biometric",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else {
                                IconButton(onClick = onCopyClick, modifier = Modifier.size(24.dp)) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy number (60s auto-wipe)",
                                        tint = MaterialTheme.colorScheme.primary,
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

