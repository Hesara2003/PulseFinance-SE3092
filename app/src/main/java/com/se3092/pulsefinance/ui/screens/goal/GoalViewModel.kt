package com.se3092.pulsefinance.ui.screens.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se3092.pulsefinance.domain.model.SavingsGoal
import com.se3092.pulsefinance.domain.repository.AuthRepository
import com.se3092.pulsefinance.domain.repository.GoalRepository
import com.se3092.pulsefinance.domain.usecase.CalculateGoalVelocityUseCase
import com.se3092.pulsefinance.domain.usecase.GoalVelocityReport
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GoalScreenUiState(
    val goal: SavingsGoal? = null,
    val velocityReport: GoalVelocityReport? = null,
    val depositAmountInput: String = "",
    val opportunityCostInput: String = "2500",
    val opportunityCostDaysSaved: Float = 1.9f,
    val isUpdating: Boolean = false,
    val showAllocateSheet: Boolean = false,
    val toastMessage: String? = null
)

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val goalRepository: GoalRepository,
    private val calculateGoalVelocityUseCase: CalculateGoalVelocityUseCase
) : ViewModel() {

    private val userId: String
        get() = authRepository.currentUserId ?: "guest_kavindu"

    private val _screenState = MutableStateFlow(GoalScreenUiState())

    val uiState: StateFlow<GoalScreenUiState> = combine(
        goalRepository.getPrimaryGoal(userId),
        _screenState
    ) { goal, state ->
        val activeGoal = goal ?: SavingsGoal(
            id = "macbook_pro_m4_primary",
            userId = userId,
            title = "MacBook Pro M4",
            targetAmountLkr = 490000.0,
            currentSavedLkr = 11200.0
        )
        // Assume current monthly savings velocity is roughly the monthly net savings or baseline
        val report = calculateGoalVelocityUseCase(activeGoal, 39900.0)

        state.copy(
            goal = activeGoal,
            velocityReport = report
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GoalScreenUiState()
    )

    fun onDepositAmountChanged(amount: String) {
        _screenState.update { it.copy(depositAmountInput = amount) }
    }

    fun onOpportunityCostInputChanged(amountStr: String) {
        val amount = amountStr.toDoubleOrNull() ?: 0.0
        val dailyPacing = uiState.value.goal?.requiredDailySavingsLkr ?: 1330.0
        val daysSaved = if (dailyPacing > 0) (amount / dailyPacing).toFloat() else 0f
        _screenState.update {
            it.copy(
                opportunityCostInput = amountStr,
                opportunityCostDaysSaved = daysSaved
            )
        }
    }

    fun addSavingsToGoal() {
        val deposit = _screenState.value.depositAmountInput.toDoubleOrNull() ?: return
        val currentGoal = uiState.value.goal ?: return
        val newTotal = currentGoal.currentSavedLkr + deposit

        viewModelScope.launch {
            _screenState.update { it.copy(isUpdating = true) }
            goalRepository.updateSavedAmount(currentGoal.id, userId, newTotal)
            _screenState.update {
                it.copy(
                    depositAmountInput = "",
                    isUpdating = false,
                    showAllocateSheet = false,
                    toastMessage = "LKR ${String.format("%,.0f", deposit)} allocated to goal"
                )
            }
        }
    }

    fun setAllocateSheetVisible(visible: Boolean) {
        _screenState.update { it.copy(showAllocateSheet = visible) }
    }

    fun allocateAmount(amount: Double) {
        val currentGoal = uiState.value.goal ?: return
        val newTotal = currentGoal.currentSavedLkr + amount
        viewModelScope.launch {
            _screenState.update { it.copy(isUpdating = true) }
            goalRepository.updateSavedAmount(currentGoal.id, userId, newTotal)
            _screenState.update {
                it.copy(
                    isUpdating = false,
                    showAllocateSheet = false,
                    toastMessage = "LKR ${String.format("%,.0f", amount)} allocated to goal"
                )
            }
        }
    }

    fun dismissToast() {
        _screenState.update { it.copy(toastMessage = null) }
    }
}
