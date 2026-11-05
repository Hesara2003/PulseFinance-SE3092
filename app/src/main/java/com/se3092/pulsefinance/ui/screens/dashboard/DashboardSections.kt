package com.se3092.pulsefinance.ui.screens.dashboard

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import java.util.Locale
import com.se3092.pulsefinance.ui.screens.expense.ExpensePreset
import com.se3092.pulsefinance.domain.model.MAX_RUNWAY_MONTHS
import com.se3092.pulsefinance.domain.model.MIN_RUNWAY_MONTHS
import com.se3092.pulsefinance.domain.model.SeasonalReserve
import com.se3092.pulsefinance.domain.usecase.SeasonalAlert
import com.se3092.pulsefinance.ui.theme.pulseColors
import com.se3092.pulsefinance.ui.common.TransactionItem
import com.se3092.pulsefinance.ui.common.presetIllustration
import com.se3092.pulsefinance.ui.common.transactionIllustration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.se3092.pulsefinance.domain.model.Account
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.se3092.pulsefinance.ui.common.compactAmount
import com.se3092.pulsefinance.ui.common.lkr
import com.se3092.pulsefinance.ui.components.ProfileAvatar
import com.se3092.pulsefinance.ui.theme.Display

// Sections of the Today screen (DashboardScreen), one composable each. They take plain values and
// callbacks; the screen owns the ViewModel, the biometric prompt and the dialogs.

/** Avatar and greeting, with hide-amounts, CSV export and sign-out. */
@Composable
internal fun DashboardGreeting(
    initials: String,
    greeting: String,
    firstName: String,
    isBalanceMasked: Boolean,
    onToggleMask: () -> Unit,
    onExportCsv: () -> Unit,
    isEmailVerified: Boolean,
    onOpenAccountMenu: () -> Unit,
    onVerifyEmail: () -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ProfileAvatar(initials = initials, size = 48.dp)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = greeting,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = firstName,
                fontFamily = Display,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        IconButton(onClick = onToggleMask) {
            Icon(
                imageVector = if (isBalanceMasked) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                contentDescription = "Hide amounts",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = onExportCsv) {
            Icon(
                imageVector = Icons.Default.FileDownload,
                contentDescription = "Export CSV",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Box {
            IconButton(onClick = {
                onOpenAccountMenu()
                menuOpen = true
            }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Account options",
                    tint = MaterialTheme.pulseColors.textTertiary
                )
            }
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                if (!isEmailVerified) {
                    DropdownMenuItem(
                        text = { Text("Verify my email") },
                        leadingIcon = { Icon(Icons.Default.MarkEmailRead, contentDescription = null) },
                        onClick = {
                            menuOpen = false
                            onVerifyEmail()
                        }
                    )
                }
                DropdownMenuItem(
                    text = { Text("Sign out") },
                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) },
                    onClick = {
                        menuOpen = false
                        onSignOut()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Delete my account", color = MaterialTheme.colorScheme.error) },
                    leadingIcon = { Icon(Icons.Default.DeleteForever, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    onClick = {
                        menuOpen = false
                        onDeleteAccount()
                    }
                )
            }
        }
    }
}

/** A Seasonal Peak 30 days out (FR-LK-4), with one-tap set-aside or its undo (FR-LK-5). */
@Composable
internal fun SeasonalPeakCard(
    alert: SeasonalAlert,
    reserve: SeasonalReserve?,
    onSetAside: () -> Unit,
    onUndoSetAside: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
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
                        .background(MaterialTheme.pulseColors.primaryLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AcUnit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = alert.festivalName,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Peak in ${alert.daysUntilPeak} days · Set aside ${lkr(alert.suggestedSetAsideLkr)}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Text(
                text = alert.message,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 17.sp
            )
            // One-tap set-aside: holds the amount back from Safe-to-Spend until the peak (FR-LK-5)
            if (reserve != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.pulseColors.primaryLight)
                        .padding(start = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${lkr(reserve.amountLkr)} set aside · held back from Safe-to-Spend",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    )
                    TextButton(onClick = onUndoSetAside) {
                        Text("Undo", color = MaterialTheme.colorScheme.primaryContainer, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            } else if (alert.suggestedSetAsideLkr > 0) {
                Button(
                    onClick = onSetAside,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 44.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
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

/**
 * Emergency Runway Buffer (FR-AUTH-6): how many months of his fixed costs he wants kept aside in
 * case income stops, and how far along he is. It is a goal to grow into, so it uses the calm
 * accent rather than an attention colour. Until he picks a number of months it just asks.
 */
@Composable
internal fun SafetyBufferCard(
    state: DashboardUiState,
    masked: Boolean,
    onMonths: (Int) -> Unit,
    onAdd: (Double) -> Unit
) {
    val runway = state.runway
    fun money(amount: Double) = if (masked) "••••" else lkr(amount)
    fun months(count: Int) = if (count == 1) "1 month" else "$count months"

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.pulseColors.primaryLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Savings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Safety buffer",
                        fontFamily = Display,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (runway != null) {
                            "${months(runway.months)} of your fixed costs · ${money(runway.targetLkr)}"
                        } else {
                            "Months of your fixed costs to keep aside, in case income stops"
                        },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (runway != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.pulseColors.track)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(runway.progress)
                            .height(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    )
                }
                Text(
                    text = if (runway.isFunded) {
                        "Fully funded · ${money(runway.savedLkr)} covers ${months(runway.months)}"
                    } else {
                        "${money(runway.savedLkr)} set aside · covers ${"%.1f".format(Locale.US, runway.monthsCovered)} of ${runway.months} months"
                    },
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(text = "Months to keep aside", fontSize = 11.sp, color = MaterialTheme.pulseColors.textTertiary)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                (MIN_RUNWAY_MONTHS..MAX_RUNWAY_MONTHS).forEach { count ->
                    BufferChip(label = "$count", selected = state.runwayMonths == count, onClick = { onMonths(count) })
                }
            }

            if (runway != null && !runway.isFunded) {
                Text(text = "Add to it", fontSize = 11.sp, color = MaterialTheme.pulseColors.textTertiary)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(5_000.0, 10_000.0, 25_000.0).forEach { amount ->
                        BufferChip(label = "+${"%,.0f".format(Locale.US, amount)}", selected = false, onClick = { onAdd(amount) })
                    }
                }
            }
        }
    }
}

@Composable
private fun BufferChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.5.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Combined balance, Freeze Breaks and one card per account (FR-ACC-1…7). */
@Composable
internal fun AccountsSection(
    accounts: List<Account>,
    combinedBalanceLkr: Double,
    totalFreezeBreaks: Int,
    revealedAccountIds: Set<String>,
    isBalanceMasked: Boolean,
    onRevealAccount: (Account) -> Unit,
    onCopyAccountNumber: (Account) -> Unit,
    onToggleFreeze: (Account) -> Unit,
    onAddAccount: () -> Unit,
    onEditAccount: (Account) -> Unit
) {
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
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                Text(
                    text = "Combined balance: ${if (isBalanceMasked) "••••••" else lkr(combinedBalanceLkr)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (totalFreezeBreaks > 0) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(horizontal = 9.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${totalFreezeBreaks} Freeze Break${if (totalFreezeBreaks > 1) "s" else ""}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                TextButton(onClick = onAddAccount) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            accounts.forEach { account ->
                AccountCardItem(
                    account = account,
                    isBalanceMasked = isBalanceMasked,
                    isRevealed = account.id in revealedAccountIds,
                    onRevealClick = { onRevealAccount(account) },
                    onCopyClick = { onCopyAccountNumber(account) },
                    onFreezeToggle = { onToggleFreeze(account) },
                    onEditClick = { onEditAccount(account) }
                )
            }
        }
    }
}

/** "Log it in one tap": Add, then the user's own repeated spends and the starter shortcuts (FR-EXP-3). */
@Composable
internal fun QuickPresetsSection(
    presets: List<ExpensePreset>,
    onAddExpense: () -> Unit,
    onPreset: (ExpensePreset) -> Unit
) {
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
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
            Text(
                text = "Your usual spends",
                fontSize = 11.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                modifier = Modifier.clickable(onClick = onAddExpense)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Custom",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Add",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            }

            // The user's own repeated spends (starter templates until habits emerge)
            presets.forEach { preset ->
                QuickPresetBadge(
                    illustration = presetIllustration(preset.title, preset.category),
                    label = preset.title,
                    amount = preset.amountLkr?.let(::compactAmount)
                ) {
                    onPreset(preset)
                }
            }
        }
    }
}

/** "Lately": newest income and expenses; long-press an expense to delete it. */
@Composable
internal fun LatelySection(
    transactions: List<TransactionItem>,
    onViewAll: () -> Unit,
    onLongPressExpense: (TransactionItem) -> Unit
) {
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
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
            Text(
                text = "View all",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onViewAll)
            )
        }

        if (transactions.isEmpty()) {
            Text(
                text = "Nothing logged yet. Tap + to record your first expense, or add income from the Income hub.",
                fontSize = 12.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                transactions.forEach { tx ->
                    TransactionSquareCard(
                        currency = tx.originalAmountLabel?.substringBefore(' ') ?: "LKR",
                        amount = tx.originalAmountLabel?.substringAfter(' ')
                            ?: "%,.0f".format(Locale.US, kotlin.math.abs(tx.amountLkr)),
                        title = tx.title,
                        illustration = transactionIllustration(tx),
                        isIncoming = tx.isIncoming,
                        // Income is edited on the Income screen; expenses can be deleted here.
                        onLongClick = tx.expenseId?.let { { onLongPressExpense(tx) } }
                    )
                }
            }
        }
    }
}

/**
 * The weekly summary's Freeze Break line (FR-ACC-5), or null when he has never broken a freeze.
 * The count is a running total: a break isn't dated, so it can't honestly be said to be this week's.
 */
internal fun freezeBreakLabel(totalFreezeBreaks: Int): String? = when {
    totalFreezeBreaks <= 0 -> null
    totalFreezeBreaks == 1 -> "1 Freeze Break so far"
    else -> "$totalFreezeBreaks Freeze Breaks so far"
}

/** Discretionary spending this week, against last week, with a Mon–Sun bar for each day. */
@Composable
internal fun WeeklyDiscretionaryCard(
    weekDiscretionaryLkr: Double,
    weekChangePct: Int?,
    weekBars: List<DayBar>,
    totalFreezeBreaks: Int = 0
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
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
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    )
                    Column {
                        Text(
                            text = "Discretionary this week",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = lkr(weekDiscretionaryLkr),
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                // Fewer discretionary rupees than last week is the win worth showing.
                weekChangePct?.let { change ->
                    Text(
                        text = if (change <= 0) "${-change}% less than last week" else "+$change% vs last week",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (change <= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
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
                weekBars.forEach { bar ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(10.dp)
                                .height(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            if (bar.amountLkr > 0) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height((40 * bar.fillRatio).coerceAtLeast(4f).dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                )
                            }
                        }
                        Text(
                            text = bar.label,
                            fontSize = 10.sp,
                            color = if (bar.isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (bar.isToday) FontWeight.ExtraBold else FontWeight.Medium
                        )
                    }
                }
            }

            freezeBreakLabel(totalFreezeBreaks)?.let { label ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.AcUnit, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(14.dp))
                    Text(
                        text = "$label · spent from a frozen account",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
