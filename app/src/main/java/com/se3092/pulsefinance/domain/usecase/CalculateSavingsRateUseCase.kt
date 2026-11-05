package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.DAY_MILLIS
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.IncomeStream
import javax.inject.Inject

/**
 * The user's actual monthly savings rate: realised income minus spending per 30-day block over
 * the last 90 days, averaged over the blocks that have any records. A block with nothing logged
 * is a Logging Gap — missing data, not a month of zero saving (NFR-2) — so it is left out
 * instead of dragging the average down. This is the number Kavindu never knew, and it drives
 * the goal projection.
 */
class CalculateSavingsRateUseCase @Inject constructor() {

    operator fun invoke(
        incomes: List<IncomeStream>,
        expenses: List<Expense>,
        now: Long = System.currentTimeMillis()
    ): Double {
        val realised = incomes.filter { it.isRealized }
        val monthlyNets = (0 until BLOCKS).mapNotNull { block ->
            val end = now - block * BLOCK_DAYS * DAY_MILLIS
            val start = end - BLOCK_DAYS * DAY_MILLIS
            val blockIncomes = realised.filter { it.dateReceivedOrExpected in start..end }
            val blockExpenses = expenses.filter { it.timestamp in start..end }
            if (blockIncomes.isEmpty() && blockExpenses.isEmpty()) null // Logging Gap: no data
            else blockIncomes.sumOf { it.amountInLkr } - blockExpenses.sumOf { it.amountLkr }
        }
        return if (monthlyNets.isEmpty()) 0.0 else monthlyNets.average()
    }

    private companion object {
        const val BLOCKS = 3
        const val BLOCK_DAYS = 30L
    }
}
