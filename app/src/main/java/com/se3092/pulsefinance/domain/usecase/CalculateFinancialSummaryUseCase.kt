package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.FinancialSummary
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import com.se3092.pulsefinance.domain.model.Periods
import com.se3092.pulsefinance.domain.model.SavingsGoal
import com.se3092.pulsefinance.domain.model.SeasonalReserve
import javax.inject.Inject

class CalculateFinancialSummaryUseCase @Inject constructor() {

    operator fun invoke(
        incomes: List<IncomeStream>,
        expenses: List<Expense>,
        goal: SavingsGoal?,
        now: Long = System.currentTimeMillis(),
        seasonalReserves: List<SeasonalReserve> = emptyList()
    ): FinancialSummary {
        val month = Periods.month(now)

        // 1. Realised income is counted in the month it actually arrived (crypto losses net off).
        val realizedThisMonth = incomes
            .filter { it.isRealized && it.dateReceivedOrExpected in month }
            .sumOf { it.amountInLkr }

        // 2. Unpaid invoices are tracked separately and never treated as spendable.
        val unpaid = incomes.filter { !it.isRealized }
        val pendingIncome = unpaid.sumOf { it.amountInLkr }
        val overdueIncome = unpaid
            .filter { it.effectiveStatus(now) == InvoiceStatus.OVERDUE }
            .sumOf { it.amountInLkr }

        // 3. Committed vs discretionary for this month only.
        val monthExpenses = expenses.filter { it.timestamp in month }
        val committed = monthExpenses.filter { it.isCommitted }.sumOf { it.amountLkr }
        val discretionary = monthExpenses.filter { !it.isCommitted }.sumOf { it.amountLkr }

        // 4. Salary lands on the 25th, so early in the month "this month's income" is near zero.
        //    Until it arrives, base the allowance on the average of the previous three months.
        //    Per FR-DASH-3, leave out crypto profits from the 3-month average but keep crypto losses
        //    so one lucky trade does not inflate the Safe-to-Spend allowance of an impulsive spender.
        //    A month with no realised income at all is a Logging Gap and is skipped; a month whose
        //    crypto losses push it below zero is kept, so the loss still lowers the average.
        val recentAverage = (1..3)
            .map { offset -> Periods.month(now, -offset) }
            .mapNotNull { period ->
                val counted = incomes.filter { income ->
                    income.isRealized &&
                        income.dateReceivedOrExpected in period &&
                        !(income.source == IncomeSource.CRYPTO && income.amountInLkr > 0)
                }
                counted.takeIf { it.isNotEmpty() }?.sumOf { it.amountInLkr }
            }
            .let { if (it.isEmpty()) 0.0 else it.average() }
        val useAverage = recentAverage > realizedThisMonth
        val incomeBasis = if (useAverage) recentAverage else realizedThisMonth

        // 5. Safe-to-spend per day = (income − committed − goal contribution − seasonal reserve − discretionary so far) / days left.
        val goalAllocation = goal?.takeUnless { it.isComplete }?.requiredMonthlySavingsLkr ?: 0.0
        val reserved = seasonalReserves.filter { it.isActive(now) }.sumOf { it.amountLkr }
        val daysRemaining = Periods.daysRemainingInMonth(now)
        val pool = incomeBasis - committed - goalAllocation - reserved - discretionary
        val safeToSpendDaily = (pool / daysRemaining).coerceAtLeast(0.0)

        return FinancialSummary(
            totalRealizedIncomeLkr = realizedThisMonth,
            pendingIncomeLkr = pendingIncome,
            overdueIncomeLkr = overdueIncome,
            totalExpensesLkr = committed + discretionary,
            committedExpensesLkr = committed,
            discretionaryExpensesLkr = discretionary,
            incomeBasisLkr = incomeBasis,
            incomeBasisIsAverage = useAverage,
            goalAllocationLkr = goalAllocation,
            seasonalReserveLkr = reserved,
            safeToSpendDailyLkr = safeToSpendDaily,
            daysRemainingInMonth = daysRemaining,
            hasAnyData = incomes.isNotEmpty() || expenses.isNotEmpty()
        )
    }
}
