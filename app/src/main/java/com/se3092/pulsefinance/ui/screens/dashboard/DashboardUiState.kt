package com.se3092.pulsefinance.ui.screens.dashboard

import com.se3092.pulsefinance.ui.components.UiNotice
import com.se3092.pulsefinance.domain.model.Account
import com.se3092.pulsefinance.domain.model.EmergencyRunway
import com.se3092.pulsefinance.domain.model.FinancialSummary
import com.se3092.pulsefinance.domain.model.GoalQuest
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.QuestProgress
import com.se3092.pulsefinance.domain.model.SavingsGoal
import com.se3092.pulsefinance.domain.model.SeasonalReserve
import com.se3092.pulsefinance.domain.usecase.SeasonalAlert
import com.se3092.pulsefinance.ui.common.TransactionItem
import com.se3092.pulsefinance.ui.screens.expense.AddExpenseFormState
import com.se3092.pulsefinance.ui.screens.expense.ExpensePreset

data class DayBar(
    val label: String,
    val amountLkr: Double,
    /** 0..1 relative to the busiest day of the week. */
    val fillRatio: Float,
    val isToday: Boolean
)

data class FrozenExpenseWarning(
    val account: Account,
    val amountLkr: Double,
    val title: String,
    val goalDaysDelayed: Int
)

data class DashboardUiState(
    val isLoading: Boolean = true,
    val firstName: String = "",
    val initials: String = "",
    val greeting: String = "",
    val monthLabel: String = "",
    val summary: FinancialSummary = FinancialSummary(),
    val primaryGoal: SavingsGoal? = null,
    val recentTransactions: List<TransactionItem> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val presets: List<ExpensePreset> = emptyList(),
    val weekBars: List<DayBar> = emptyList(),
    val weekDiscretionaryLkr: Double = 0.0,
    /** % change of discretionary spend vs the same days last week; null when there is no baseline. */
    val weekChangePct: Int? = null,
    val unpaidInvoices: List<IncomeStream> = emptyList(),
    val overdueInvoiceCount: Int = 0,
    val addExpense: AddExpenseFormState = AddExpenseFormState(),
    val notice: UiNotice? = null,
    val seasonalAlert: SeasonalAlert? = null,
    /** What he already set aside for [seasonalAlert]'s peak, if anything. */
    val seasonalReserve: SeasonalReserve? = null,
    val frozenWarning: FrozenExpenseWarning? = null,
    val revealedAccountIds: Set<String> = emptySet(),
    /** What his fixed costs come to in a month; 0 until he has fixed costs or committed spending (FR-AUTH-6). */
    val runwayMonthlyCommittedLkr: Double = 0.0,
    /** Months he chose for his emergency buffer; null until he picks. */
    val runwayMonths: Int? = null,
    /** What he has recorded as set aside for it. */
    val runwaySavedLkr: Double = 0.0
) {
    /** The goal as a level-by-level journey, derived from [primaryGoal]. */
    val quest: QuestProgress? = primaryGoal?.let(GoalQuest::of)

    /** The buffer card needs a monthly cost to size the buffer from. */
    val showRunwayCard: Boolean get() = runwayMonthlyCommittedLkr > 0

    /** Null while he has not chosen a number of months (the card then asks him to). */
    val runway: EmergencyRunway?
        get() = runwayMonths?.takeIf { showRunwayCard }?.let { EmergencyRunway(it, runwayMonthlyCommittedLkr, runwaySavedLkr) }

    val combinedBalanceLkr: Double get() = accounts.sumOf { it.balanceLkr }
    val totalFreezeBreaks: Int get() = accounts.sumOf { it.freezeBreakCount }
}
