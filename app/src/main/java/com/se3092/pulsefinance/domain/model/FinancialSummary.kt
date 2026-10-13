package com.se3092.pulsefinance.domain.model

/** Current-month position, normalised to LKR. */
data class FinancialSummary(
    val totalRealizedIncomeLkr: Double = 0.0,
    val pendingIncomeLkr: Double = 0.0,
    val overdueIncomeLkr: Double = 0.0,
    val totalExpensesLkr: Double = 0.0,
    val committedExpensesLkr: Double = 0.0,
    val discretionaryExpensesLkr: Double = 0.0,
    /** Income figure the allowance is based on: this month's, or the recent average if higher. */
    val incomeBasisLkr: Double = 0.0,
    val incomeBasisIsAverage: Boolean = false,
    val goalAllocationLkr: Double = 0.0,
    val safeToSpendDailyLkr: Double = 0.0,
    val daysRemainingInMonth: Int = 30,
    val hasAnyData: Boolean = false
) {
    val discretionaryPercentage: Float
        get() = if (totalExpensesLkr > 0) ((discretionaryExpensesLkr / totalExpensesLkr).toFloat()).coerceIn(0f, 1f) else 0f

    val committedPercentage: Float
        get() = if (totalExpensesLkr > 0) ((committedExpensesLkr / totalExpensesLkr).toFloat()).coerceIn(0f, 1f) else 0f

    val netSavingsLkr: Double
        get() = totalRealizedIncomeLkr - totalExpensesLkr
}
