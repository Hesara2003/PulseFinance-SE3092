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
        monthlyGoalAllocationLkr: Double = 39900.0
    ): FinancialSummary {
        // 1. Separate realized income from pending freelance invoices
        val realizedIncome = incomes
            .filter { it.status == InvoiceStatus.RECEIVED }
            .sumOf { it.amountInLkr }

        val pendingIncome = incomes
            .filter { it.status == InvoiceStatus.PENDING_INVOICE || it.status == InvoiceStatus.OVERDUE }
            .sumOf { it.amountInLkr }

        // 2. Segment expenses into committed vs discretionary
        val totalExpenses = expenses.sumOf { it.amountLkr }
        val committedExpenses = expenses
            .filter { it.isCommitted }
            .sumOf { it.amountLkr }
        val discretionaryExpenses = expenses
            .filter { !it.isCommitted }
            .sumOf { it.amountLkr }

        // 3. Liquid total across all tracked accounts
        val totalLiquid = accounts.sumOf { it.balanceLkr }

        // 4. Calculate days remaining in current month
        val calendar = Calendar.getInstance()
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
            totalExpensesLkr = totalExpenses,
            committedExpensesLkr = committedExpenses,
            discretionaryExpensesLkr = discretionaryExpenses,
            totalLiquidBalanceLkr = totalLiquid,
            safeToSpendDailyLkr = safeToSpendDaily,
            daysRemainingInMonth = daysRemaining
        )
    }
}
