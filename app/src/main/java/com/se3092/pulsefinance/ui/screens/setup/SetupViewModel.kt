package com.se3092.pulsefinance.ui.screens.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se3092.pulsefinance.domain.model.EmergencyRunway
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeVariability
import com.se3092.pulsefinance.domain.model.OnboardingProfile
import com.se3092.pulsefinance.domain.repository.AuthRepository
import com.se3092.pulsefinance.domain.repository.ExpenseRepository
import com.se3092.pulsefinance.domain.repository.OnboardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** First-run setup for a new account: salary, income sources, fixed commitments, emergency buffer (FR-AUTH-4, 5, 6). */
@HiltViewModel
class SetupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val onboardingRepository: OnboardingRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

    private var nextCommitmentId = 1

    fun onSalaryChanged(input: String) {
        _uiState.update { state ->
            val salary = input.replace(",", "").toDoubleOrNull()?.takeIf { it > 0 }
            // Entering a salary switches the Salary source on; it stays editable on the next step.
            val sources = if (salary != null && IncomeSource.SALARY !in state.sources) {
                state.sources + (IncomeSource.SALARY to OnboardingProfile.defaultVariability(IncomeSource.SALARY))
            } else {
                state.sources
            }
            state.copy(salaryInput = input, sources = sources, errorMessage = null)
        }
    }

    fun toggleSource(source: IncomeSource) {
        _uiState.update { state ->
            val sources = if (source in state.sources) state.sources - source
            else state.sources + (source to OnboardingProfile.defaultVariability(source))
            state.copy(sources = sources, errorMessage = null)
        }
    }

    fun setVariability(source: IncomeSource, variability: IncomeVariability) {
        _uiState.update { it.copy(sources = it.sources + (source to variability)) }
    }

    fun addCommitment(suggestion: CommitmentSuggestion? = null) {
        _uiState.update {
            it.copy(
                commitments = it.commitments + CommitmentDraft(
                    id = nextCommitmentId++,
                    title = suggestion?.title.orEmpty(),
                    amountInput = "",
                    category = suggestion?.category ?: ExpenseCategory.UTILITIES
                )
            )
        }
    }

    fun onCommitmentTitleChanged(id: Int, title: String) = updateCommitment(id) { it.copy(title = title) }

    fun onCommitmentAmountChanged(id: Int, amount: String) = updateCommitment(id) { it.copy(amountInput = amount) }

    fun onRunwayMonthsChanged(months: Int) {
        _uiState.update { it.copy(runwayMonths = EmergencyRunway.clampMonths(months), errorMessage = null) }
    }

    fun onRunwaySavedChanged(input: String) {
        _uiState.update { it.copy(runwaySavedInput = input, errorMessage = null) }
    }

    fun removeCommitment(id: Int) {
        _uiState.update { state -> state.copy(commitments = state.commitments.filterNot { it.id == id }) }
    }

    private fun updateCommitment(id: Int, change: (CommitmentDraft) -> CommitmentDraft) {
        _uiState.update { state -> state.copy(commitments = state.commitments.map { if (it.id == id) change(it) else it }) }
    }

    fun next() {
        val state = _uiState.value
        state.stepError?.let { error ->
            _uiState.update { it.copy(errorMessage = error) }
            return
        }
        if (state.isLastStep) finish() else _uiState.update { it.copy(step = it.step + 1, errorMessage = null) }
    }

    fun back() {
        _uiState.update { it.copy(step = (it.step - 1).coerceAtLeast(0), errorMessage = null) }
    }

    /** Leaves setup without saving anything; he can still record everything from the app. */
    fun skip() {
        _uiState.update { it.copy(isDone = true) }
    }

    private fun finish() {
        val userId = authRepository.currentUserId ?: run {
            _uiState.update { it.copy(errorMessage = "You're signed out. Sign in again to finish setup.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val now = System.currentTimeMillis()
            val profile = _uiState.value.toProfile(now)
            onboardingRepository.saveProfile(userId, profile)
            // Room first, so these appear at once and sync later even with no signal (NFR-3).
            val failed = profile.commitmentExpenses(userId, now).count { expenseRepository.saveExpense(it).isFailure }
            _uiState.update {
                if (failed == 0) it.copy(isSaving = false, isDone = true)
                else it.copy(isSaving = false, errorMessage = "Couldn't save $failed commitment(s). Try again.")
            }
        }
    }
}
