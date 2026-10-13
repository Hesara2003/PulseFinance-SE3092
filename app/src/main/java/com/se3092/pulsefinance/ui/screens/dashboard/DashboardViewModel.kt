package com.se3092.pulsefinance.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se3092.pulsefinance.domain.model.Account
import com.se3092.pulsefinance.domain.model.DAY_MILLIS
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import com.se3092.pulsefinance.domain.model.Periods
import com.se3092.pulsefinance.domain.model.SavingsGoal
import com.se3092.pulsefinance.domain.repository.AccountRepository
import com.se3092.pulsefinance.domain.repository.AuthRepository
import com.se3092.pulsefinance.domain.repository.ExpenseRepository
import com.se3092.pulsefinance.domain.repository.GoalRepository
import com.se3092.pulsefinance.domain.repository.IncomeRepository
import com.se3092.pulsefinance.domain.usecase.CalculateFinancialSummaryUseCase
import com.se3092.pulsefinance.ui.common.buildLedger
import com.se3092.pulsefinance.ui.common.displayNameFor
import com.se3092.pulsefinance.ui.common.formatDate
import com.se3092.pulsefinance.ui.common.greetingFor
import com.se3092.pulsefinance.ui.common.initialsFor
import com.se3092.pulsefinance.ui.common.lkr
import com.se3092.pulsefinance.ui.common.whenSignedIn
import com.se3092.pulsefinance.ui.screens.expense.AddExpenseFormState
import com.se3092.pulsefinance.ui.screens.expense.ExpensePreset
import com.se3092.pulsefinance.ui.screens.expense.deriveExpensePresets
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

private data class DashboardData(
    val expenses: List<Expense>,
    val incomes: List<IncomeStream>,
    val goal: SavingsGoal?,
    val accounts: List<Account>
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val expenseRepository: ExpenseRepository,
    incomeRepository: IncomeRepository,
    goalRepository: GoalRepository,
    accountRepository: AccountRepository,
    private val calculateFinancialSummaryUseCase: CalculateFinancialSummaryUseCase
) : ViewModel() {

    private val form = MutableStateFlow(AddExpenseFormState())
    private val message = MutableStateFlow<String?>(null)

    private val data = authRepository.whenSignedIn<DashboardData?>(signedOut = null) { userId ->
        combine(
            expenseRepository.getExpenses(userId),
            incomeRepository.getIncomeStreams(userId),
            goalRepository.getPrimaryGoal(userId),
            accountRepository.getAccounts(userId)
        ) { expenses, incomes, goal, accounts -> DashboardData(expenses, incomes, goal, accounts) }
    }

    val uiState: StateFlow<DashboardUiState> = combine(data, form, message) { data, form, message ->
        buildState(data, form, message)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    private fun buildState(data: DashboardData?, form: AddExpenseFormState, message: String?): DashboardUiState {
        val now = System.currentTimeMillis()
        val email = authRepository.userEmail
        val displayName = authRepository.userDisplayName
        val header = DashboardUiState(
            isLoading = data == null,
            firstName = displayNameFor(displayName, email),
            initials = initialsFor(displayName, email),
            greeting = greetingFor(now),
            monthLabel = formatDate(now, "MMMM yyyy"),
            addExpense = form,
            message = message
        )
        if (data == null) return header

        val unpaid = data.incomes
            .filter { !it.isRealized }
            .sortedWith(compareByDescending<IncomeStream> { it.effectiveStatus(now) == InvoiceStatus.OVERDUE }.thenBy { it.dateReceivedOrExpected })
        val (weekBars, weekTotal, weekChange) = weeklyDiscretionary(data.expenses, now)

        return header.copy(
            summary = calculateFinancialSummaryUseCase(data.incomes, data.expenses, data.goal, now),
            primaryGoal = data.goal,
            recentTransactions = buildLedger(data.incomes, data.expenses).take(6),
            recentExpenses = data.expenses.take(5),
            accounts = data.accounts,
            presets = deriveExpensePresets(data.expenses, now),
            weekBars = weekBars,
            weekDiscretionaryLkr = weekTotal,
            weekChangePct = weekChange,
            unpaidInvoices = unpaid,
            overdueInvoiceCount = unpaid.count { it.effectiveStatus(now) == InvoiceStatus.OVERDUE },
            addExpense = form.copy(accountId = form.accountId.ifBlank { data.accounts.firstOrNull()?.id.orEmpty() })
        )
    }

    /** Discretionary spend per day this week, and the change vs the same weekdays last week. */
    private fun weeklyDiscretionary(expenses: List<Expense>, now: Long): Triple<List<DayBar>, Double, Int?> {
        val discretionary = expenses.filter { !it.isCommitted }
        val week = Periods.week(now)
        val today = Periods.startOfDay(now)
        val dayTotals = (0 until 7).map { i ->
            val start = week.startMillis + i * DAY_MILLIS
            start to discretionary.filter { it.timestamp in start until start + DAY_MILLIS }.sumOf { it.amountLkr }
        }
        val peak = dayTotals.maxOf { it.second }.coerceAtLeast(1.0)
        val bars = dayTotals.mapIndexed { i, (start, total) ->
            DayBar(
                label = WEEKDAY_LETTERS[i],
                amountLkr = total,
                fillRatio = (total / peak).toFloat(),
                isToday = start == today
            )
        }

        val elapsed = (today + DAY_MILLIS) - week.startMillis
        val thisWeek = discretionary.filter { it.timestamp in week.startMillis until week.startMillis + elapsed }.sumOf { it.amountLkr }
        val lastWeekStart = week.startMillis - 7 * DAY_MILLIS
        val lastWeek = discretionary.filter { it.timestamp in lastWeekStart until lastWeekStart + elapsed }.sumOf { it.amountLkr }
        val change = if (lastWeek > 0) ((thisWeek - lastWeek) / lastWeek * 100).roundToInt() else null
        return Triple(bars, thisWeek, change)
    }

    // ---------------- Add expense sheet ----------------

    fun openAddExpense(preset: ExpensePreset? = null) {
        form.update {
            AddExpenseFormState(
                isVisible = true,
                accountId = it.accountId,
                titleInput = preset?.title.orEmpty(),
                amountInput = preset?.amountLkr?.let { amount -> "%.0f".format(amount) }.orEmpty(),
                category = preset?.category ?: ExpenseCategory.FOOD_DINING,
                isCommitted = preset?.isCommitted ?: false
            )
        }
    }

    fun dismissAddExpense() = form.update { it.copy(isVisible = false, errorMessage = null) }
    fun onTitleChanged(value: String) = form.update { it.copy(titleInput = value) }
    fun onAmountChanged(value: String) = form.update { it.copy(amountInput = value, errorMessage = null) }
    fun onCommittedToggled(value: Boolean) = form.update { it.copy(isCommitted = value) }
    fun onAccountSelected(accountId: String) = form.update { it.copy(accountId = accountId) }
    fun onDaysAgoSelected(days: Int) = form.update { it.copy(daysAgo = days) }

    fun onCategoryChanged(category: ExpenseCategory) = form.update {
        it.copy(category = category, isCommitted = category.isTypicallyCommitted)
    }

    fun onPresetSelected(preset: ExpensePreset) = form.update {
        it.copy(
            titleInput = preset.title,
            amountInput = "%.0f".format(preset.amountLkr),
            category = preset.category,
            isCommitted = preset.isCommitted,
            errorMessage = null
        )
    }

    fun submitExpense() {
        val state = uiState.value
        val current = state.addExpense
        val amount = current.amountInput.replace(",", "").trim().toDoubleOrNull()
        if (amount == null || amount <= 0) {
            form.update { it.copy(errorMessage = "Enter an amount greater than zero") }
            return
        }
        val userId = authRepository.currentUserId ?: run {
            form.update { it.copy(errorMessage = "You're signed out. Sign in again to save.") }
            return
        }
        val account = state.accounts.find { it.id == current.accountId }
        val title = current.titleInput.trim().ifBlank { current.category.displayName }

        viewModelScope.launch {
            form.update { it.copy(isSubmitting = true) }
            val result = expenseRepository.saveExpense(
                Expense(
                    userId = userId,
                    title = title,
                    amountLkr = amount,
                    category = current.category,
                    isCommitted = current.isCommitted,
                    paymentAccountId = account?.id.orEmpty(),
                    paymentAccountName = account?.name.orEmpty(),
                    timestamp = System.currentTimeMillis() - current.daysAgo * DAY_MILLIS
                )
            )
            result.fold(
                onSuccess = {
                    form.update { AddExpenseFormState(accountId = it.accountId) }
                    message.value = "Logged ${lkr(amount)} · $title"
                },
                onFailure = { e ->
                    form.update { it.copy(isSubmitting = false, errorMessage = e.localizedMessage ?: "Couldn't save this expense") }
                }
            )
        }
    }

    // ---------------- Other actions ----------------

    fun deleteExpense(expenseId: String) {
        val userId = authRepository.currentUserId ?: return
        viewModelScope.launch {
            expenseRepository.deleteExpense(expenseId, userId)
                .onSuccess { message.value = "Expense deleted" }
                .onFailure { message.value = "Couldn't delete: ${it.localizedMessage}" }
        }
    }

    fun dismissMessage() {
        message.value = null
    }

    fun signOut() {
        viewModelScope.launch { authRepository.signOut() }
    }

    private companion object {
        val WEEKDAY_LETTERS = listOf("M", "T", "W", "T", "F", "S", "S")
    }
}
