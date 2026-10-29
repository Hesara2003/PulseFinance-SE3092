package com.se3092.pulsefinance.ui.screens.setup

import com.se3092.pulsefinance.domain.model.AccountType
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.FixedCommitment
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeVariability
import com.se3092.pulsefinance.domain.model.OnboardingProfile

/** One editable row on the commitments step. */
data class CommitmentDraft(
    val id: Int,
    val title: String,
    val amountInput: String,
    val category: ExpenseCategory
) {
    val amountLkr: Double? get() = amountInput.replace(",", "").toDoubleOrNull()?.takeIf { it > 0 }
    val isComplete: Boolean get() = title.isNotBlank() && amountLkr != null
}

/** Quick-add suggestions for the commitments step, with the category each is logged under. */
enum class CommitmentSuggestion(val title: String, val category: ExpenseCategory) {
    RENT("Rent", ExpenseCategory.RENT),
    GYM("Gym membership", ExpenseCategory.SUBSCRIPTION),
    INTERNET("Home internet", ExpenseCategory.UTILITIES),
    PHONE("Phone bill", ExpenseCategory.UTILITIES),
    ELECTRICITY("Electricity & water", ExpenseCategory.UTILITIES)
}

data class SetupUiState(
    /** 0 = salary, 1 = income sources, 2 = fixed commitments. */
    val step: Int = 0,
    val salaryInput: String = "",
    /** Active sources and how variable each one is (FR-AUTH-5). */
    val sources: Map<IncomeSource, IncomeVariability> = mapOf(IncomeSource.SALARY to IncomeVariability.LOW),
    val commitments: List<CommitmentDraft> = listOf(CommitmentDraft(0, "Rent", "", ExpenseCategory.RENT)),
    val isSaving: Boolean = false,
    val isDone: Boolean = false,
    val errorMessage: String? = null
) {
    val salaryLkr: Double? get() = salaryInput.replace(",", "").toDoubleOrNull()?.takeIf { it > 0 }
    val completeCommitments: List<CommitmentDraft> get() = commitments.filter { it.isComplete }
    val commitmentsTotalLkr: Double get() = completeCommitments.sumOf { it.amountLkr ?: 0.0 }
    val isLastStep: Boolean get() = step == LAST_STEP

    /** Why he can't move past the current step, or null when he can. */
    val stepError: String?
        get() = when (step) {
            0 -> if (salaryInput.isNotBlank() && salaryLkr == null) "Enter your salary as a number, or leave it blank" else null
            1 -> if (sources.isEmpty()) "Pick at least one way you earn money" else null
            else -> null
        }

    /** What setup captured. A salary he entered always counts as an active, steady-by-default source. */
    fun toProfile(now: Long): OnboardingProfile = OnboardingProfile(
        monthlySalaryLkr = salaryLkr,
        incomeSources = if (salaryLkr != null && IncomeSource.SALARY !in sources) {
            sources + (IncomeSource.SALARY to OnboardingProfile.defaultVariability(IncomeSource.SALARY))
        } else {
            sources
        },
        fixedCommitments = completeCommitments.map { FixedCommitment(it.title.trim(), it.amountLkr!!, it.category) },
        completedAt = now
    )

    companion object {
        const val LAST_STEP = 2
    }
}

/**
 * Logs each fixed commitment as this month's committed expense, paid from the primary bank
 * account, so Safe-to-Spend reflects rent and bills from the first day.
 */
fun OnboardingProfile.commitmentExpenses(userId: String, now: Long): List<Expense> =
    fixedCommitments.map { commitment ->
        Expense(
            userId = userId,
            title = commitment.title,
            amountLkr = commitment.amountLkr,
            category = commitment.category,
            isCommitted = true,
            paymentAccountId = PRIMARY_ACCOUNT_ID,
            paymentAccountName = AccountType.BANK_PRIMARY.displayName,
            timestamp = now,
            notes = "Fixed monthly commitment · added during setup"
        )
    }

/** Matches the default primary account created on first sign-in (AccountRepositoryImpl). */
private const val PRIMARY_ACCOUNT_ID = "acc_primary_bank"
