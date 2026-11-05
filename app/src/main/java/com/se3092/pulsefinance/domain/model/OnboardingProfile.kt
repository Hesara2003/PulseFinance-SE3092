package com.se3092.pulsefinance.domain.model

/** How predictable an Income Source is (FR-AUTH-5). */
enum class IncomeVariability(val displayName: String, val description: String) {
    LOW("Steady", "About the same every month"),
    MODERATE("Varies", "Changes from month to month"),
    VOLATILE("Highly volatile", "Can swing a lot, even negative")
}

/** A fixed monthly cost he pays regardless of behaviour, e.g. rent LKR 34,000. */
data class FixedCommitment(
    val title: String,
    val amountLkr: Double,
    val category: ExpenseCategory
)

/** What first-run setup captured (FR-AUTH-4, FR-AUTH-5). */
data class OnboardingProfile(
    /** Monthly take-home salary; null when he has no salaried job. */
    val monthlySalaryLkr: Double?,
    val incomeSources: Map<IncomeSource, IncomeVariability>,
    val fixedCommitments: List<FixedCommitment>,
    val completedAt: Long
) {
    companion object {
        /** Starting guess per source; he can change each one during setup. */
        fun defaultVariability(source: IncomeSource): IncomeVariability = when (source) {
            IncomeSource.SALARY -> IncomeVariability.LOW
            IncomeSource.FREELANCE, IncomeSource.ADSENSE -> IncomeVariability.MODERATE
            IncomeSource.CRYPTO -> IncomeVariability.VOLATILE
        }
    }
}
