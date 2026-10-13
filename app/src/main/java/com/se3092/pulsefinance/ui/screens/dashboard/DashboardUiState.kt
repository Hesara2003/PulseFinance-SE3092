package com.se3092.pulsefinance.ui.screens.dashboard

import com.se3092.pulsefinance.domain.model.Account
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.FinancialSummary
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.SavingsGoal
import com.se3092.pulsefinance.domain.model.UserProfile
import com.se3092.pulsefinance.domain.usecase.SavingsRateReport
import com.se3092.pulsefinance.ui.util.TransactionVisual

/** One square card in the "Recent Transactions" slider (income or expense). */
data class ActivityItem(
    val id: String,
    val title: String,
    val currencyLabel: String,
    val amountText: String,
    val isIncoming: Boolean,
    val visual: TransactionVisual,
    val timestamp: Long
)

/** One capsule in the 7-day discretionary velocity chart. */
data class DayBar(
    val label: String,
    val amountLkr: Double,
    val fillRatio: Float,
    val isToday: Boolean
)

data class DashboardUiState(
    val isLoading: Boolean = true,
    val user: UserProfile? = null,
    val summary: FinancialSummary = FinancialSummary(),
    val savingsRate: SavingsRateReport? = null,
    val primaryGoal: SavingsGoal? = null,
    val recentExpenses: List<Expense> = emptyList(),
    val recentActivity: List<ActivityItem> = emptyList(),
    val weeklyDiscretionary: List<DayBar> = emptyList(),
    val incomeStreams: List<IncomeStream> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val overdueInvoices: List<IncomeStream> = emptyList(),
    val errorMessage: String? = null
) {
    val overdueInvoicesCount: Int get() = overdueInvoices.size
    val overdueInvoicesTotalLkr: Double get() = overdueInvoices.sumOf { it.amountInLkr }
    val primaryAccountLabel: String get() =
        accounts.firstOrNull()?.name?.substringBefore(" (")?.uppercase() ?: "PULSEFINANCE"
    val weeklyDiscretionaryTotalLkr: Double get() = weeklyDiscretionary.sumOf { it.amountLkr }
}
