package com.se3092.pulsefinance.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se3092.pulsefinance.domain.model.Currency
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import com.se3092.pulsefinance.domain.model.SavingsGoal
import com.se3092.pulsefinance.domain.repository.AccountRepository
import com.se3092.pulsefinance.domain.repository.AuthRepository
import com.se3092.pulsefinance.domain.repository.ExpenseRepository
import com.se3092.pulsefinance.domain.repository.GoalRepository
import com.se3092.pulsefinance.domain.repository.IncomeRepository
import com.se3092.pulsefinance.domain.usecase.CalculateFinancialSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val expenseRepository: ExpenseRepository,
    private val incomeRepository: IncomeRepository,
    private val goalRepository: GoalRepository,
    private val accountRepository: AccountRepository,
    private val calculateFinancialSummaryUseCase: CalculateFinancialSummaryUseCase
) : ViewModel() {

    private val userId: String
        get() = authRepository.currentUserId ?: "guest_kavindu"

    val uiState: StateFlow<DashboardUiState> = combine(
        expenseRepository.getExpenses(userId),
        incomeRepository.getIncomeStreams(userId),
        goalRepository.getPrimaryGoal(userId),
        accountRepository.getAccounts(userId)
    ) { expenses, incomes, goal, accounts ->
        val overdue = incomes.filter { it.status == InvoiceStatus.OVERDUE }
        val summary = calculateFinancialSummaryUseCase(
            incomes = incomes,
            expenses = expenses,
            accounts = accounts,
            monthlyGoalAllocationLkr = goal?.requiredMonthlySavingsLkr ?: 39900.0
        )

        DashboardUiState(
            isLoading = false,
            summary = summary,
            primaryGoal = goal,
            recentExpenses = expenses.take(5),
            incomeStreams = incomes,
            accounts = accounts,
            overdueInvoicesCount = overdue.size,
            overdueInvoicesTotalLkr = overdue.sumOf { it.amountInLkr }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    init {
        seedScenarioDataIfEmpty()
    }

    private fun seedScenarioDataIfEmpty() {
        viewModelScope.launch {
            // Seed Kavindu's initial scenario data so the app demonstrates the exact problem state
            accountRepository.initializeDefaultAccountsForKavindu(userId)

            val currentGoal = goalRepository.getPrimaryGoal(userId)
            goalRepository.saveGoal(
                SavingsGoal(
                    id = "macbook_pro_m4_primary",
                    userId = userId,
                    title = "MacBook Pro M4",
                    targetAmountLkr = 490000.0,
                    currentSavedLkr = 11200.0
                )
            )

            // Seed typical scenario expenses if none exist
            expenseRepository.addExpense(
                Expense(
                    id = "exp_rent_initial",
                    userId = userId,
                    title = "Apartment Rent",
                    amountLkr = 34000.0,
                    category = ExpenseCategory.RENT,
                    isCommitted = true,
                    paymentAccountId = "acc_primary_bank",
                    paymentAccountName = "Commercial Bank"
                )
            )
            expenseRepository.addExpense(
                Expense(
                    id = "exp_dining_initial",
                    userId = userId,
                    title = "Coffee & Casual Dining (Month)",
                    amountLkr = 24000.0,
                    category = ExpenseCategory.FOOD_DINING,
                    isCommitted = false,
                    paymentAccountId = "acc_primary_bank",
                    paymentAccountName = "Commercial Bank"
                )
            )
            expenseRepository.addExpense(
                Expense(
                    id = "exp_pickme_initial",
                    userId = userId,
                    title = "PickMe Rides",
                    amountLkr = 8500.0,
                    category = ExpenseCategory.TRANSPORT,
                    isCommitted = false,
                    paymentAccountId = "acc_primary_bank",
                    paymentAccountName = "Commercial Bank"
                )
            )

            // Seed Kavindu's 4 income sources
            incomeRepository.addIncomeStream(
                IncomeStream(
                    id = "inc_salary_initial",
                    userId = userId,
                    source = IncomeSource.SALARY,
                    title = "Monthly Software Engineer Salary",
                    clientOrPlatform = "Startup Colombo (Base + Bonus)",
                    originalAmount = 130000.0,
                    currency = Currency.LKR,
                    status = InvoiceStatus.RECEIVED,
                    isRecurringMonthly = true
                )
            )
            incomeRepository.addIncomeStream(
                IncomeStream(
                    id = "inc_freelance_overdue",
                    userId = userId,
                    source = IncomeSource.FREELANCE,
                    title = "React & WP Build Milestone 2",
                    clientOrPlatform = "Local SME Retail",
                    originalAmount = 45000.0,
                    currency = Currency.LKR,
                    status = InvoiceStatus.OVERDUE,
                    isRecurringMonthly = false
                )
            )
            incomeRepository.addIncomeStream(
                IncomeStream(
                    id = "inc_adsense_initial",
                    userId = userId,
                    source = IncomeSource.ADSENSE,
                    title = "Tech Blog AdSense Revenue",
                    clientOrPlatform = "Google AdSense",
                    originalAmount = 45.0,
                    currency = Currency.USD,
                    exchangeRateToLkr = 305.0,
                    amountInLkr = 13725.0,
                    status = InvoiceStatus.RECEIVED
                )
            )
        }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(expenseId, userId)
        }
    }

    fun markInvoicePaid(incomeId: String) {
        viewModelScope.launch {
            incomeRepository.updateInvoiceStatus(incomeId, userId, InvoiceStatus.RECEIVED)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}
