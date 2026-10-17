package com.se3092.pulsefinance.domain.model

data class FinancialSummary(
    val totalRealizedIncomeLkr: Double = 0.0,
    val pendingIncomeLkr: Double = 0.0,
    val overdueIncomeLkr: Double = 0.0,
    val totalExpensesLkr: Double = 0.0,
    val committedExpensesLkr: Double = 0.0,
    val discretionaryExpensesLkr: Double = 0.0,
    val totalLiquidBalanceLkr: Double = 0.0,
    val monthlyGoalAllocationLkr: Double = 0.0,
    val discretionaryPoolRemainingLkr: Double = 0.0,
    val safeToSpendDailyLkr: Double = 0.0,
    val daysRemainingInMonth: Int = 30
) {
    val discretionaryPercentage: Float
        get() = if (totalExpensesLkr > 0) ((discretionaryExpensesLkr / totalExpensesLkr).toFloat()).coerceIn(0f, 1f) else 0f

    val committedPercentage: Float
        get() = if (totalExpensesLkr > 0) ((committedExpensesLkr / totalExpensesLkr).toFloat()).coerceIn(0f, 1f) else 0f

    val netSavingsLkr: Double
        get() = totalRealizedIncomeLkr - totalExpensesLkr

    val totalPipelineLkr: Double
        get() = totalRealizedIncomeLkr + pendingIncomeLkr

    val isOverBudget: Boolean
        get() = discretionaryPoolRemainingLkr < 0
}
