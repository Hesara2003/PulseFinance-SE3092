package com.se3092.pulsefinance.ui.screens.goal

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CancelPresentation
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.SavingsGoal
import com.se3092.pulsefinance.domain.repository.AccountRepository
import com.se3092.pulsefinance.domain.repository.AuthRepository
import com.se3092.pulsefinance.domain.repository.ExpenseRepository
import com.se3092.pulsefinance.domain.repository.GoalRepository
import com.se3092.pulsefinance.domain.repository.IncomeRepository
import com.se3092.pulsefinance.domain.repository.SubscriptionReviewRepository
import com.se3092.pulsefinance.domain.usecase.CalculateGoalVelocityUseCase
import com.se3092.pulsefinance.domain.usecase.CalculateSavingsRateUseCase
import com.se3092.pulsefinance.domain.usecase.DetectSubscriptionsUseCase
import com.se3092.pulsefinance.domain.usecase.GoalVelocityReport
import com.se3092.pulsefinance.ui.screens.expense.ExpensePresets
import com.se3092.pulsefinance.ui.util.Formatters
import com.se3092.pulsefinance.ui.util.TransactionVisuals
import com.se3092.pulsefinance.ui.util.userScopedFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

/** One "skip this, fund that" card in the Opportunity Cost Translator. */
data class OpportunitySuggestion(
    val id: String,
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val badgeText: String,
    val amountLkr: Double
)

data class GoalScreenUiState(
    val goal: SavingsGoal? = null,
    val velocityReport: GoalVelocityReport? = null,
    val monthlyNetSavingsLkr: Double = 0.0,
    val suggestions: List<OpportunitySuggestion> = emptyList(),
    /** Projected completion at the current savings velocity, or null if it never gets there. */
    val projectedArrivalMillis: Long? = null,
    val userInitials: String = "",
    val depositAmountInput: String = "",
    val opportunityCostInput: String = "2500",
    val opportunityCostDaysSaved: Float = 0f,
    val isUpdating: Boolean = false,
    val showAllocateSheet: Boolean = false,
    val toastMessage: String? = null
) {
    val dailyPaceLkr: Double get() = goal?.requiredDailySavingsLkr ?: 0.0

    fun daysFunded(amountLkr: Double): Float =
        if (dailyPaceLkr > 0) (amountLkr / dailyPaceLkr).toFloat() else 0f
}

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val goalRepository: GoalRepository,
    expenseRepository: ExpenseRepository,
    incomeRepository: IncomeRepository,
    accountRepository: AccountRepository,
    subscriptionReviewRepository: SubscriptionReviewRepository,
    private val calculateGoalVelocityUseCase: CalculateGoalVelocityUseCase,
    private val calculateSavingsRateUseCase: CalculateSavingsRateUseCase,
    private val detectSubscriptions: DetectSubscriptionsUseCase
) : ViewModel() {

    private val _screenState = MutableStateFlow(GoalScreenUiState())

    private val goalFlow = authRepository.userScopedFlow<SavingsGoal?>(null) { goalRepository.getPrimaryGoal(it) }
    private val expensesFlow = authRepository.userScopedFlow(emptyList()) { expenseRepository.getExpenses(it) }
    private val incomeFlow = authRepository.userScopedFlow(emptyList()) { incomeRepository.getIncomeStreams(it) }

    /** Recurring debits the user hasn't reviewed yet: candidates for "kill this sub". */
    private val flaggedSubscriptionsFlow = combine(
        expensesFlow,
        authRepository.userScopedFlow(emptyList()) { accountRepository.getAccounts(it) },
        authRepository.userScopedFlow(emptyMap()) { subscriptionReviewRepository.observeReviews(it) }
    ) { expenses, accounts, reviews ->
        detectSubscriptions(expenses, accounts, reviews).filter { it.isFlagged }
    }

    val uiState: StateFlow<GoalScreenUiState> = combine(
        goalFlow,
        expensesFlow,
        incomeFlow,
        flaggedSubscriptionsFlow,
        _screenState
    ) { goal, expenses, incomes, flaggedSubs, state ->
        val savings = calculateSavingsRateUseCase(incomes, expenses)
        val velocity = savings.monthlyNetSavingsLkr.coerceAtLeast(0.0)
        val report = goal?.let { calculateGoalVelocityUseCase(it, velocity) }

        val withGoal = state.copy(goal = goal)
        val suggestions = if (goal == null) emptyList() else buildList {
            addAll(discretionarySuggestions(expenses, withGoal))
            flaggedSubs.firstOrNull()?.let { sub ->
                val boost = if (goal.requiredMonthlySavingsLkr > 0) {
                    (sub.monthlyCostLkr / goal.requiredMonthlySavingsLkr * 100).toInt()
                } else 0
                add(
                    OpportunitySuggestion(
                        id = "sub_${sub.key}",
                        icon = Icons.Default.CancelPresentation,
                        title = "Kill ${sub.title}",
                        subtitle = "Save ${Formatters.lkr(sub.monthlyCostLkr)}/mo • Compounding speed",
                        badgeText = "+$boost% Boost",
                        amountLkr = sub.monthlyCostLkr
                    )
                )
            }
        }

        val arrival = goal?.let { g ->
            when {
                g.remainingAmountLkr <= 0 -> System.currentTimeMillis()
                velocity <= 0 -> null
                else -> System.currentTimeMillis() + (g.remainingAmountLkr / velocity * 30 * DAY_MILLIS).toLong()
            }
        }

        withGoal.copy(
            velocityReport = report,
            monthlyNetSavingsLkr = savings.monthlyNetSavingsLkr,
            suggestions = suggestions,
            projectedArrivalMillis = arrival,
            userInitials = Formatters.initials(authRepository.currentUser),
            opportunityCostDaysSaved = withGoal.daysFunded(state.opportunityCostInput.toDoubleOrNull() ?: 0.0)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GoalScreenUiState()
    )

    /**
     * Picks the user's most frequent discretionary purchases (by title) and frames skipping
     * one (or a few) of them as days of goal funding. Falls back to the speed-logger presets
     * for new users with no history yet.
     */
    private fun discretionarySuggestions(
        expenses: List<Expense>,
        state: GoalScreenUiState
    ): List<OpportunitySuggestion> {
        val habits = expenses
            .filter { !it.isCommitted }
            .groupBy { it.title.trim().lowercase(Locale.US) }
            .values
            .sortedByDescending { group -> group.size * group.sumOf { it.amountLkr } }
            .take(2)

        if (habits.isEmpty()) {
            return ExpensePresets.defaults.filter { !it.isCommitted }.take(2).map { preset ->
                val days = state.daysFunded(preset.amountLkr)
                OpportunitySuggestion(
                    id = "preset_${preset.id}",
                    icon = TransactionVisuals.forCategory(preset.category).icon,
                    title = "Skip 1 ${preset.label}",
                    subtitle = "Save ${Formatters.lkr(preset.amountLkr)} • ${Formatters.days(days)} days funded",
                    badgeText = "+${String.format(Locale.US, "%.1f", days)} Days",
                    amountLkr = preset.amountLkr
                )
            }
        }

        return habits.mapIndexed { index, group ->
            val sample = group.maxBy { it.timestamp }
            val typical = group.map { it.amountLkr }.average()
            // Second card asks for a few skips so the impact is visible for cheap habits
            val count = if (index == 0) 1 else 3
            val amount = typical * count
            val days = state.daysFunded(amount)
            OpportunitySuggestion(
                id = "habit_${sample.id}",
                icon = TransactionVisuals.forCategory(sample.category).icon,
                title = "Skip $count× ${sample.title}",
                subtitle = "Save ${Formatters.lkr(amount)} • ${Formatters.days(days)} days funded",
                badgeText = "+${String.format(Locale.US, "%.1f", days)} Days",
                amountLkr = amount
            )
        }
    }

    fun onDepositAmountChanged(amount: String) {
        _screenState.update { it.copy(depositAmountInput = amount.filter { c -> c.isDigit() || c == '.' }) }
    }

    fun onOpportunityCostInputChanged(amountStr: String) {
        _screenState.update { it.copy(opportunityCostInput = amountStr) }
    }

    fun applySuggestion(suggestion: OpportunitySuggestion) {
        _screenState.update {
            it.copy(
                depositAmountInput = suggestion.amountLkr.toLong().toString(),
                showAllocateSheet = true
            )
        }
    }

    fun addSavingsToGoal() {
        val deposit = _screenState.value.depositAmountInput.toDoubleOrNull()?.takeIf { it > 0 } ?: return
        allocateAmount(deposit)
    }

    fun setAllocateSheetVisible(visible: Boolean) {
        _screenState.update { it.copy(showAllocateSheet = visible) }
    }

    fun allocateAmount(amount: Double) {
        val uid = authRepository.currentUserId ?: return
        val currentGoal = uiState.value.goal ?: return
        val newTotal = currentGoal.currentSavedLkr + amount
        viewModelScope.launch {
            _screenState.update { it.copy(isUpdating = true) }
            goalRepository.updateSavedAmount(currentGoal.id, uid, newTotal)
            _screenState.update {
                it.copy(
                    depositAmountInput = "",
                    isUpdating = false,
                    showAllocateSheet = false,
                    toastMessage = "${Formatters.lkr(amount)} allocated to goal"
                )
            }
        }
    }

    /** New users without a seeded scenario can start the flagship goal themselves. */
    fun createDefaultGoal() {
        val uid = authRepository.currentUserId ?: return
        viewModelScope.launch {
            goalRepository.saveGoal(SavingsGoal(id = "", userId = uid, currentSavedLkr = 0.0))
            _screenState.update { it.copy(toastMessage = "MacBook Pro M4 goal created") }
        }
    }

    fun dismissToast() {
        _screenState.update { it.copy(toastMessage = null) }
    }

    private companion object {
        const val DAY_MILLIS = 24L * 60 * 60 * 1000
    }
}
