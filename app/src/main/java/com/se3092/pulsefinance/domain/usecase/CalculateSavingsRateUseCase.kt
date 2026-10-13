package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import java.util.Calendar
import javax.inject.Inject

data class SavingsRateReport(
    val monthlyIncomeLkr: Double,
    val monthlyExpensesLkr: Double,
    /** Net cash left after this month's spending; negative means the month ran a deficit. */
    val monthlyNetSavingsLkr: Double,
    /** Share of realized income kept this month, 0..1 (0 when there's no income yet). */
    val savingsRate: Float,
    /** Average net savings per month across the months that have any activity. */
    val averageMonthlyNetSavingsLkr: Double
) {
    val savingsRatePercent: Int get() = (savingsRate * 100).toInt()
}

class CalculateSavingsRateUseCase @Inject constructor() {

    operator fun invoke(
        incomes: List<IncomeStream>,
        expenses: List<Expense>,
        nowMillis: Long = System.currentTimeMillis()
    ): SavingsRateReport {
        val currentMonth = monthKey(nowMillis)
        val realized = incomes.filter { it.status == InvoiceStatus.RECEIVED }

        val incomeByMonth = mutableMapOf<Int, Double>()
        realized.forEach { income ->
            // Recurring streams (salary) count towards every month, including the current one.
            val month = if (income.isRecurringMonthly) currentMonth else monthKey(income.dateReceivedOrExpected)
            incomeByMonth[month] = (incomeByMonth[month] ?: 0.0) + income.amountInLkr
        }
        val expensesByMonth = expenses
            .groupBy { monthKey(it.timestamp) }
            .mapValues { (_, list) -> list.sumOf { it.amountLkr } }

        val monthIncome = incomeByMonth[currentMonth] ?: 0.0
        val monthExpenses = expensesByMonth[currentMonth] ?: 0.0
        val net = monthIncome - monthExpenses

        val activeMonths = incomeByMonth.keys + expensesByMonth.keys
        val averageNet = if (activeMonths.isEmpty()) 0.0 else {
            activeMonths.sumOf { (incomeByMonth[it] ?: 0.0) - (expensesByMonth[it] ?: 0.0) } / activeMonths.size
        }

        return SavingsRateReport(
            monthlyIncomeLkr = monthIncome,
            monthlyExpensesLkr = monthExpenses,
            monthlyNetSavingsLkr = net,
            savingsRate = if (monthIncome > 0) (net / monthIncome).toFloat().coerceIn(0f, 1f) else 0f,
            averageMonthlyNetSavingsLkr = averageNet
        )
    }

    private fun monthKey(millis: Long): Int {
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        return cal.get(Calendar.YEAR) * 12 + cal.get(Calendar.MONTH)
    }
}
