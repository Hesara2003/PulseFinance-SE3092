package com.se3092.pulsefinance.ui.screens.dashboard

import com.se3092.pulsefinance.domain.model.Account
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.FinancialSummary
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.SavingsGoal

data class DashboardUiState(
    val isLoading: Boolean = true,
    val summary: FinancialSummary = FinancialSummary(),
    val primaryGoal: SavingsGoal? = null,
    val recentExpenses: List<Expense> = emptyList(),
    val incomeStreams: List<IncomeStream> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val overdueInvoicesCount: Int = 0,
    val overdueInvoicesTotalLkr: Double = 0.0,
    val errorMessage: String? = null
)
