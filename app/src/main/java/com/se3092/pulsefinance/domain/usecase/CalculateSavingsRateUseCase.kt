package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.DAY_MILLIS
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.IncomeStream
import javax.inject.Inject

/**
 * The user's actual monthly savings rate: realised income minus spending, averaged over
 * the last 90 days (or since the first record if the history is shorter than that).
 * This is the number Kavindu never knew, and it drives the goal projection.
 */
class CalculateSavingsRateUseCase @Inject constructor() {

    operator fun invoke(
        incomes: List<IncomeStream>,
        expenses: List<Expense>,
        now: Long = System.currentTimeMillis()
    ): Double {
        val windowStart = now - WINDOW_DAYS * DAY_MILLIS
        val windowIncomes = incomes.filter { it.isRealized && it.dateReceivedOrExpected in windowStart..now }
        val windowExpenses = expenses.filter { it.timestamp in windowStart..now }

        val earliest = (windowIncomes.map { it.dateReceivedOrExpected } + windowExpenses.map { it.timestamp }).minOrNull()
            ?: return 0.0

        val months = ((now - earliest).toDouble() / (30 * DAY_MILLIS)).coerceIn(1.0, WINDOW_DAYS / 30.0)
        val net = windowIncomes.sumOf { it.amountInLkr } - windowExpenses.sumOf { it.amountLkr }
        return net / months
    }

    private companion object {
        const val WINDOW_DAYS = 90L
    }
}
