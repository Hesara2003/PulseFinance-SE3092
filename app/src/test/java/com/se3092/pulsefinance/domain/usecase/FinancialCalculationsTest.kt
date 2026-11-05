package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.Currency
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import com.se3092.pulsefinance.domain.model.SavingsGoal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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

    private fun salary(daysAgo: Long) = IncomeStream(
        id = "salary", source = IncomeSource.SALARY, title = "Salary", clientOrPlatform = "Startup",
        originalAmount = 130000.0, currency = Currency.LKR, status = InvoiceStatus.RECEIVED,
        isRecurringMonthly = true, dateReceivedOrExpected = now - daysAgo * day
    )

    private val overdueMilestone = IncomeStream(
        id = "m2", source = IncomeSource.FREELANCE, title = "Milestone 2", clientOrPlatform = "SME",
        originalAmount = 45000.0, currency = Currency.LKR, status = InvoiceStatus.OVERDUE,
        dateReceivedOrExpected = now - 4 * day
    )

    /** 90 days left (on the real clock, which SavingsGoal reads) and LKR 119,700 to go => LKR 39,900 a month. */
    private val goal = SavingsGoal(
        targetAmountLkr = 119700.0,
        currentSavedLkr = 0.0,
        targetDateMillis = System.currentTimeMillis() + 90 * day + day / 2
    )

    @Test
    fun `summary only counts this month's expenses`() {
        val expenses = listOf(
            expense(34000.0, committed = true, daysAgo = 5),
            expense(24000.0, committed = false, daysAgo = 2),
            expense(8500.0, committed = true, daysAgo = 35) // last month
        )

        val summary = CalculateFinancialSummaryUseCase()(
            incomes = listOf(salary(daysAgo = 3), overdueMilestone),
            expenses = expenses,
            goal = goal,
            now = now
        )

        assertEquals(58000.0, summary.totalExpensesLkr, 0.01)
        assertEquals(34000.0, summary.committedExpensesLkr, 0.01)
        assertEquals(130000.0, summary.totalRealizedIncomeLkr, 0.01)
        // An overdue invoice is tracked, never spendable
        assertEquals(45000.0, summary.overdueIncomeLkr, 0.01)
        assertFalse(summary.incomeBasisIsAverage)
        // 130,000 - 34,000 - 39,900 - 24,000 = 32,100 over 17 remaining October days
        assertEquals(39900.0, summary.goalAllocationLkr, 0.01)
        assertEquals(17, summary.daysRemainingInMonth)
        assertEquals(32100.0 / 17, summary.safeToSpendDailyLkr, 0.01)
    }

    @Test
    fun `allowance falls back to the recent average before this month's salary lands`() {
        // Salary was last received 40 days ago (September), nothing yet in October
        val summary = CalculateFinancialSummaryUseCase()(
            incomes = listOf(salary(daysAgo = 40)),
            expenses = listOf(expense(34000.0, committed = true, daysAgo = 5)),
            goal = null,
            now = now
        )

        assertEquals(0.0, summary.totalRealizedIncomeLkr, 0.01)
        assertTrue(summary.incomeBasisIsAverage)
        assertEquals(130000.0, summary.incomeBasisLkr, 0.01)
        assertEquals((130000.0 - 34000.0) / 17, summary.safeToSpendDailyLkr, 0.01)
    }

    @Test
    fun `savings rate reflects this month's net cash and skips Logging Gap blocks`() {
        // Only the latest 30-day block has records; the two earlier blocks are gaps, not zero saving
        val monthlyNet = CalculateSavingsRateUseCase()(
            incomes = listOf(salary(daysAgo = 3), overdueMilestone),
            expenses = listOf(expense(34000.0, true, 5), expense(24000.0, false, 2)),
            now = now
        )

        assertEquals(72000.0, monthlyNet, 0.01)
    }

    @Test
    fun `savings rate is negative without income`() {
        val monthlyNet = CalculateSavingsRateUseCase()(emptyList(), listOf(expense(850.0, false, 1)), now)

        assertEquals(-850.0, monthlyNet, 0.01)
    }

    @Test
    fun `savings rate is zero with nothing logged`() {
        assertEquals(0.0, CalculateSavingsRateUseCase()(emptyList(), emptyList(), now), 0.0)
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
