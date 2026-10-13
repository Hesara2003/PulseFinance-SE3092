package com.se3092.pulsefinance.ui.screens.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se3092.pulsefinance.domain.model.DAY_MILLIS
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.SavingsGoal
import com.se3092.pulsefinance.domain.repository.AuthRepository
import com.se3092.pulsefinance.domain.repository.ExpenseRepository
import com.se3092.pulsefinance.domain.repository.GoalRepository
import com.se3092.pulsefinance.domain.repository.IncomeRepository
import com.se3092.pulsefinance.domain.usecase.CalculateGoalVelocityUseCase
import com.se3092.pulsefinance.domain.usecase.CalculateSavingsRateUseCase
import com.se3092.pulsefinance.domain.usecase.GoalVelocityReport
import com.se3092.pulsefinance.ui.common.lkr
import com.se3092.pulsefinance.ui.common.whenSignedIn
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

/** "Cut this habit by a quarter" suggestion, priced in days of goal progress. */
data class OpportunityCost(
    val category: ExpenseCategory,
    val monthlySpendLkr: Double,
    val monthlySavingLkr: Double,
    val daysOfGoalPace: Float
)

data class GoalEditorState(
    val isVisible: Boolean = false,
    val isNew: Boolean = true,
    val titleInput: String = "",
    val targetInput: String = "",
    val savedInput: String = "",
    val monthsInput: String = "12",
    val notesInput: String = "",
    val errorMessage: String? = null
)

data class GoalScreenUiState(
    val isLoading: Boolean = true,
    val goal: SavingsGoal? = null,
    val velocityReport: GoalVelocityReport? = null,
    val opportunities: List<OpportunityCost> = emptyList(),
    val depositAmountInput: String = "",
    val isUpdating: Boolean = false,
    val showAllocateSheet: Boolean = false,
    val editor: GoalEditorState = GoalEditorState(),
    val toastMessage: String? = null,
    val celebration: Celebration? = null
)

/** Shown after money is set aside, so progress feels like an event rather than a number change. */
data class Celebration(val title: String, val message: String)

private data class GoalLocalState(
    val depositAmountInput: String = "",
    val isUpdating: Boolean = false,
    val showAllocateSheet: Boolean = false,
    val editor: GoalEditorState = GoalEditorState(),
    val toastMessage: String? = null,
    val celebration: Celebration? = null
)

private data class GoalData(val goal: SavingsGoal?, val savingsRate: Double, val expenses: List<Expense>)

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val goalRepository: GoalRepository,
    expenseRepository: ExpenseRepository,
    incomeRepository: IncomeRepository,
    calculateSavingsRateUseCase: CalculateSavingsRateUseCase,
    private val calculateGoalVelocityUseCase: CalculateGoalVelocityUseCase
) : ViewModel() {

    private val local = MutableStateFlow(GoalLocalState())

    private val data = authRepository.whenSignedIn<GoalData?>(signedOut = null) { userId ->
        combine(
            goalRepository.getPrimaryGoal(userId),
            expenseRepository.getExpenses(userId),
            incomeRepository.getIncomeStreams(userId)
        ) { goal, expenses, incomes ->
            GoalData(goal, calculateSavingsRateUseCase(incomes, expenses), expenses)
        }
    }

    val uiState: StateFlow<GoalScreenUiState> = combine(data, local) { data, local ->
        val goal = data?.goal
        GoalScreenUiState(
            isLoading = data == null,
            goal = goal,
            velocityReport = goal?.let { calculateGoalVelocityUseCase(it, data.savingsRate) },
            opportunities = if (goal != null && data != null) opportunities(data.expenses, goal) else emptyList(),
            depositAmountInput = local.depositAmountInput,
            isUpdating = local.isUpdating,
            showAllocateSheet = local.showAllocateSheet,
            editor = local.editor,
            toastMessage = local.toastMessage,
            celebration = local.celebration
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GoalScreenUiState()
    )

    /** Top three discretionary categories of the last 30 days, each trimmed by 25%. */
    private fun opportunities(expenses: List<Expense>, goal: SavingsGoal): List<OpportunityCost> {
        val since = System.currentTimeMillis() - 30 * DAY_MILLIS
        val dailyPace = goal.requiredDailySavingsLkr.takeIf { it > 0 } ?: return emptyList()
        return expenses
            .filter { !it.isCommitted && it.timestamp >= since }
            .groupBy { it.category }
            .map { (category, items) ->
                val spend = items.sumOf { it.amountLkr }
                val saving = spend * 0.25
                OpportunityCost(category, spend, saving, (saving / dailyPace).toFloat())
            }
            .filter { it.monthlySavingLkr >= 100 }
            .sortedByDescending { it.monthlySpendLkr }
            .take(3)
    }

    // ---------------- Allocation ----------------

    fun onDepositAmountChanged(amount: String) = local.update { it.copy(depositAmountInput = amount) }

    fun setAllocateSheetVisible(visible: Boolean) = local.update { it.copy(showAllocateSheet = visible) }

    fun openAllocateWith(amountLkr: Double) = local.update {
        it.copy(showAllocateSheet = true, depositAmountInput = amountLkr.roundToInt().toString())
    }

    fun addSavingsToGoal() {
        val deposit = local.value.depositAmountInput.replace(",", "").trim().toDoubleOrNull()
        val goal = uiState.value.goal
        val userId = authRepository.currentUserId
        if (deposit == null || deposit <= 0 || goal == null || userId == null) {
            local.update { it.copy(toastMessage = "Enter an amount greater than zero") }
            return
        }
        viewModelScope.launch {
            local.update { it.copy(isUpdating = true) }
            goalRepository.updateSavedAmount(goal.id, userId, goal.currentSavedLkr + deposit).fold(
                onSuccess = {
                    val newTotal = goal.currentSavedLkr + deposit
                    val celebration = if (newTotal >= goal.targetAmountLkr) {
                        Celebration("Goal reached!", "${goal.title} is fully funded. Every rupee you tracked got you here.")
                    } else {
                        val days = goal.requiredDailySavingsLkr.takeIf { it > 0 }?.let { deposit / it } ?: 0.0
                        Celebration(
                            "${lkr(deposit)} set aside",
                            "That covers ${"%.1f".format(days)} days of your required pace. " +
                                "${lkr(goal.targetAmountLkr - newTotal)} to go for ${goal.title}."
                        )
                    }
                    local.update {
                        it.copy(
                            depositAmountInput = "",
                            isUpdating = false,
                            showAllocateSheet = false,
                            celebration = celebration
                        )
                    }
                },
                onFailure = { e ->
                    local.update { it.copy(isUpdating = false, toastMessage = e.localizedMessage ?: "Couldn't update the goal") }
                }
            )
        }
    }

    // ---------------- Create / edit goal ----------------

    fun openGoalEditor() {
        val goal = uiState.value.goal
        local.update {
            it.copy(
                editor = if (goal == null) GoalEditorState(isVisible = true)
                else GoalEditorState(
                    isVisible = true,
                    isNew = false,
                    titleInput = goal.title,
                    targetInput = goal.targetAmountLkr.roundToInt().toString(),
                    savedInput = goal.currentSavedLkr.roundToInt().toString(),
                    monthsInput = ((goal.daysRemaining + 15) / 30).coerceAtLeast(1).toString(),
                    notesInput = goal.notes
                )
            )
        }
    }

    fun dismissGoalEditor() = local.update { it.copy(editor = it.editor.copy(isVisible = false)) }

    fun onEditorChanged(transform: (GoalEditorState) -> GoalEditorState) =
        local.update { it.copy(editor = transform(it.editor).copy(errorMessage = null)) }

    fun saveGoal() {
        val editor = local.value.editor
        val target = editor.targetInput.replace(",", "").trim().toDoubleOrNull()
        val saved = editor.savedInput.replace(",", "").trim().ifBlank { "0" }.toDoubleOrNull()
        val months = editor.monthsInput.trim().toIntOrNull()
        val error = when {
            editor.titleInput.isBlank() -> "Give the goal a name"
            target == null || target <= 0 -> "Enter the target amount"
            saved == null || saved < 0 -> "Saved so far can't be negative"
            months == null || months !in 1..120 -> "Deadline must be 1–120 months away"
            else -> null
        }
        val userId = authRepository.currentUserId
        if (error != null || userId == null) {
            local.update { it.copy(editor = it.editor.copy(errorMessage = error ?: "You're signed out")) }
            return
        }
        val existing = uiState.value.goal
        val goal = (existing ?: SavingsGoal(userId = userId)).copy(
            userId = userId,
            title = editor.titleInput.trim(),
            targetAmountLkr = target!!,
            currentSavedLkr = saved!!,
            targetDateMillis = System.currentTimeMillis() + months!! * 30L * DAY_MILLIS,
            notes = editor.notesInput.trim()
        )
        viewModelScope.launch {
            goalRepository.saveGoal(goal).fold(
                onSuccess = {
                    local.update {
                        it.copy(editor = GoalEditorState(), toastMessage = if (existing == null) "Goal created" else "Goal updated")
                    }
                },
                onFailure = { e ->
                    local.update { it.copy(editor = it.editor.copy(errorMessage = e.localizedMessage ?: "Couldn't save")) }
                }
            )
        }
    }

    fun dismissToast() = local.update { it.copy(toastMessage = null) }

    fun dismissCelebration() = local.update { it.copy(celebration = null) }
}
