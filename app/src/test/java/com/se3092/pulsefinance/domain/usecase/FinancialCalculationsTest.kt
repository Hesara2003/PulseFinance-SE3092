package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.Currency
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class FinancialCalculationsTest {

    private val day = 24L * 60 * 60 * 1000

    /** 15th of a month at noon, so "this month" and "last month" are unambiguous. */
    private val now = Calendar.getInstance().apply {
        set(2026, Calendar.OCTOBER, 15, 12, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private fun expense(amount: Double, committed: Boolean, daysAgo: Long) = Expense(
        id = "e$amount$daysAgo",
        title = "x",
        amountLkr = amount,
        category = if (committed) ExpenseCategory.RENT else ExpenseCategory.FOOD_DINING,
        isCommitted = committed,
        paymentAccountId = "a",
        paymentAccountName = "a",
        timestamp = now - daysAgo * day
    )

    private val salary = IncomeStream(
        id = "salary", source = IncomeSource.SALARY, title = "Salary", clientOrPlatform = "Startup",
        originalAmount = 130000.0, currency = Currency.LKR, status = InvoiceStatus.RECEIVED,
        isRecurringMonthly = true, dateReceivedOrExpected = now - 40 * day
    )
    private val overdueMilestone = IncomeStream(
        id = "m2", source = IncomeSource.FREELANCE, title = "Milestone 2", clientOrPlatform = "SME",
        originalAmount = 45000.0, currency = Currency.LKR, status = InvoiceStatus.OVERDUE,
        dateReceivedOrExpected = now - 4 * day
    )

    @Test
    fun `summary only counts this month's expenses`() {
        val expenses = listOf(
            expense(34000.0, committed = true, daysAgo = 5),
            expense(24000.0, committed = false, daysAgo = 2),
            expense(8500.0, committed = true, daysAgo = 35) // last month
        )

        val summary = CalculateFinancialSummaryUseCase()(
            incomes = listOf(salary, overdueMilestone),
            expenses = expenses,
            accounts = emptyList(),
            monthlyGoalAllocationLkr = 39900.0,
            nowMillis = now
        )

        assertEquals(58000.0, summary.totalExpensesLkr, 0.01)
        assertEquals(34000.0, summary.committedExpensesLkr, 0.01)
        // Recurring salary counts even though it was first logged last month
        assertEquals(130000.0, summary.totalRealizedIncomeLkr, 0.01)
        assertEquals(45000.0, summary.overdueIncomeLkr, 0.01)
        // 130,000 - 34,000 - 39,900 - 24,000 = 32,100 over 17 remaining October days
        assertEquals(32100.0, summary.discretionaryPoolRemainingLkr, 0.01)
        assertEquals(17, summary.daysRemainingInMonth)
        assertEquals(32100.0 / 17, summary.safeToSpendDailyLkr, 0.01)
    }

    @Test
    fun `savings rate reflects this month's net cash`() {
        val report = CalculateSavingsRateUseCase()(
            incomes = listOf(salary, overdueMilestone),
            expenses = listOf(expense(34000.0, true, 5), expense(24000.0, false, 2)),
            nowMillis = now
        )

        assertEquals(130000.0, report.monthlyIncomeLkr, 0.01)
        assertEquals(72000.0, report.monthlyNetSavingsLkr, 0.01)
        assertEquals(55, report.savingsRatePercent)
    }

    @Test
    fun `savings rate is zero without income`() {
        val report = CalculateSavingsRateUseCase()(emptyList(), listOf(expense(850.0, false, 1)), now)

        assertEquals(0f, report.savingsRate, 0f)
        assertEquals(-850.0, report.monthlyNetSavingsLkr, 0.01)
    }

    @Test
    fun `opportunity cost translator matches the documented pacing figures`() {
        // Persona pacing of LKR 1,330/day: a LKR 2,500 dinner funds 1.88 days of the MacBook
        val dailyPace = 1330.0
        assertEquals(1.88, 2500.0 / dailyPace, 0.005)
        assertEquals(0.64, 850.0 / dailyPace, 0.005)
        assertEquals(1.09, 1450.0 / dailyPace, 0.005)
    }
}
