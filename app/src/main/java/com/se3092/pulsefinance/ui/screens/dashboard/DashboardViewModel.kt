package com.se3092.pulsefinance.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import com.se3092.pulsefinance.domain.model.SavingsGoal
import com.se3092.pulsefinance.domain.repository.AccountRepository
import com.se3092.pulsefinance.domain.repository.AuthRepository
import com.se3092.pulsefinance.domain.repository.ExpenseRepository
import com.se3092.pulsefinance.domain.repository.GoalRepository
import com.se3092.pulsefinance.domain.repository.IncomeRepository
import com.se3092.pulsefinance.domain.usecase.CalculateFinancialSummaryUseCase
import com.se3092.pulsefinance.domain.usecase.CalculateSavingsRateUseCase
import com.se3092.pulsefinance.ui.util.TransactionVisuals
import com.se3092.pulsefinance.ui.util.userScopedFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val expenseRepository: ExpenseRepository,
    private val incomeRepository: IncomeRepository,
    private val goalRepository: GoalRepository,
    private val accountRepository: AccountRepository,
    private val calculateFinancialSummaryUseCase: CalculateFinancialSummaryUseCase,
    private val calculateSavingsRateUseCase: CalculateSavingsRateUseCase
) : ViewModel() {

    private val expensesFlow = authRepository.userScopedFlow(emptyList()) { expenseRepository.getExpenses(it) }
    private val incomeFlow = authRepository.userScopedFlow(emptyList()) { incomeRepository.getIncomeStreams(it) }
    private val goalFlow = authRepository.userScopedFlow<SavingsGoal?>(null) { goalRepository.getPrimaryGoal(it) }
    private val accountsFlow = authRepository.userScopedFlow(emptyList()) { accountRepository.getAccounts(it) }

    val uiState: StateFlow<DashboardUiState> = combine(
        expensesFlow,
        incomeFlow,
        goalFlow,
        accountsFlow,
        authRepository.observeCurrentUser()
    ) { expenses, incomes, goal, accounts, user ->
        val summary = calculateFinancialSummaryUseCase(
            incomes = incomes,
            expenses = expenses,
            accounts = accounts,
            monthlyGoalAllocationLkr = goal?.requiredMonthlySavingsLkr ?: 0.0
        )

        DashboardUiState(
            isLoading = false,
            user = user,
            summary = summary,
            savingsRate = calculateSavingsRateUseCase(incomes, expenses),
            primaryGoal = goal,
            recentExpenses = expenses.take(5),
            recentActivity = buildRecentActivity(expenses, incomes),
            weeklyDiscretionary = buildWeeklyDiscretionary(expenses),
            incomeStreams = incomes,
            accounts = accounts,
            overdueInvoices = incomes
                .filter { it.status == InvoiceStatus.OVERDUE }
                .sortedBy { it.dateReceivedOrExpected }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    private fun buildRecentActivity(expenses: List<Expense>, incomes: List<IncomeStream>): List<ActivityItem> {
        val expenseItems = expenses.map { expense ->
            ActivityItem(
                id = "exp_${expense.id}",
                title = expense.title,
                currencyLabel = "LKR",
                amountText = String.format(Locale.US, "%,.2f", expense.amountLkr),
                isIncoming = false,
                visual = TransactionVisuals.forCategory(expense.category),
                timestamp = expense.timestamp
            )
        }
        val incomeItems = incomes.filter { it.isRealized }.map { income ->
            ActivityItem(
                id = "inc_${income.id}",
                title = income.clientOrPlatform.ifBlank { income.title },
                currencyLabel = income.currency.name,
                amountText = String.format(Locale.US, "%,.2f", income.originalAmount),
                isIncoming = true,
                visual = TransactionVisuals.forIncome(income.source),
                timestamp = income.dateReceivedOrExpected
            )
        }
        val now = System.currentTimeMillis()
        return (expenseItems + incomeItems)
            .filter { it.timestamp <= now }
            .sortedByDescending { it.timestamp }
            .take(6)
    }

    /** Discretionary spend per day for the last 7 days, oldest first, scaled to the peak day. */
    private fun buildWeeklyDiscretionary(expenses: List<Expense>): List<DayBar> {
        val dayFormat = SimpleDateFormat("EEEEE", Locale.US)
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val days = (6 downTo 0).map { daysAgo ->
            val start = (todayStart.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -daysAgo) }.timeInMillis
            val end = (todayStart.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -daysAgo + 1) }.timeInMillis
            val amount = expenses
                .filter { !it.isCommitted && it.timestamp in start until end }
                .sumOf { it.amountLkr }
            Triple(dayFormat.format(Date(start)), amount, daysAgo == 0)
        }
        val peak = days.maxOf { it.second }.coerceAtLeast(1.0)
        return days.map { (label, amount, isToday) ->
            DayBar(label, amount, (amount / peak).toFloat().coerceIn(0f, 1f), isToday)
        }
    }

    fun deleteExpense(expenseId: String) {
        val uid = authRepository.currentUserId ?: return
        viewModelScope.launch {
            expenseRepository.deleteExpense(expenseId, uid)
        }
    }

    fun markInvoicePaid(incomeId: String) {
        val uid = authRepository.currentUserId ?: return
        viewModelScope.launch {
            incomeRepository.updateInvoiceStatus(incomeId, uid, InvoiceStatus.RECEIVED)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}
