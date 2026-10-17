package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.Account
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.FinancialSummary
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import java.util.Calendar
import javax.inject.Inject

class CalculateFinancialSummaryUseCase @Inject constructor() {

    operator fun invoke(
        incomes: List<IncomeStream>,
        expenses: List<Expense>,
        accounts: List<Account>,
        monthlyGoalAllocationLkr: Double = 39900.0,
        nowMillis: Long = System.currentTimeMillis()
    ): FinancialSummary {
        val calendar = Calendar.getInstance().apply { timeInMillis = nowMillis }
        val monthStart = (calendar.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        // The cockpit reflects the current month: older expenses (e.g. last month's
        // subscription charges) stay in the ledger but don't eat into this month's budget.
        val monthExpenses = expenses.filter { it.timestamp >= monthStart }

        // 1. Separate realized income from pending freelance invoices
        val realizedIncome = incomes
            .filter { it.status == InvoiceStatus.RECEIVED }
            .filter { it.isRecurringMonthly || it.dateReceivedOrExpected >= monthStart }
            .sumOf { it.amountInLkr }

        val pendingIncome = incomes
            .filter { it.status == InvoiceStatus.PENDING_INVOICE || it.status == InvoiceStatus.OVERDUE }
            .sumOf { it.amountInLkr }

        val overdueIncome = incomes
            .filter { it.status == InvoiceStatus.OVERDUE }
            .sumOf { it.amountInLkr }

        // 2. Segment expenses into committed vs discretionary
        val totalExpenses = monthExpenses.sumOf { it.amountLkr }
        val committedExpenses = monthExpenses
            .filter { it.isCommitted }
            .sumOf { it.amountLkr }
        val discretionaryExpenses = monthExpenses
            .filter { !it.isCommitted }
            .sumOf { it.amountLkr }

        // 3. Liquid total across all tracked accounts
        val totalLiquid = accounts.sumOf { it.balanceLkr }

        // 4. Calculate days remaining in current month
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val daysRemaining = (maxDays - currentDay + 1).coerceAtLeast(1)

        // 5. Safe-to-spend daily allowance:
        // (Safe Realized Income - Fixed Committed Bills - Goal Allocation - Discretionary Spent So Far) / Remaining Days
        val availableDiscretionaryPool = (realizedIncome - committedExpenses - monthlyGoalAllocationLkr - discretionaryExpenses)
        val safeToSpendDaily = (availableDiscretionaryPool / daysRemaining).coerceAtLeast(0.0)

        return FinancialSummary(
            totalRealizedIncomeLkr = realizedIncome,
            pendingIncomeLkr = pendingIncome,
            overdueIncomeLkr = overdueIncome,
            totalExpensesLkr = totalExpenses,
            committedExpensesLkr = committedExpenses,
            discretionaryExpensesLkr = discretionaryExpenses,
            totalLiquidBalanceLkr = totalLiquid,
            monthlyGoalAllocationLkr = monthlyGoalAllocationLkr,
            discretionaryPoolRemainingLkr = availableDiscretionaryPool,
            safeToSpendDailyLkr = safeToSpendDaily,
            daysRemainingInMonth = daysRemaining
        )
    }
}
