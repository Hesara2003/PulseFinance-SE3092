package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.Currency
import com.se3092.pulsefinance.domain.model.DAY_MILLIS
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateSavingsRateUseCaseTest {

    private val useCase = CalculateSavingsRateUseCase()

    @Test
    fun `calculates 90-day averaged net monthly savings accurately`() {
        val now = 1700000000000L

        // Incomes: 3 months of 180,000 = 540,000
        val incomes = listOf(
            IncomeStream(
                id = "i1",
                source = IncomeSource.SALARY,
                title = "Month 1 Salary",
                clientOrPlatform = "Employer",
                originalAmount = 180_000.0,
                currency = Currency.LKR,
                amountInLkr = 180_000.0,
                status = InvoiceStatus.RECEIVED,
                dateReceivedOrExpected = now - 75 * DAY_MILLIS
            ),
            IncomeStream(
                id = "i2",
                source = IncomeSource.SALARY,
                title = "Month 2 Salary",
                clientOrPlatform = "Employer",
                originalAmount = 180_000.0,
                currency = Currency.LKR,
                amountInLkr = 180_000.0,
                status = InvoiceStatus.RECEIVED,
                dateReceivedOrExpected = now - 45 * DAY_MILLIS
            ),
            IncomeStream(
                id = "i3",
                source = IncomeSource.SALARY,
                title = "Month 3 Salary",
                clientOrPlatform = "Employer",
                originalAmount = 180_000.0,
                currency = Currency.LKR,
                amountInLkr = 180_000.0,
                status = InvoiceStatus.RECEIVED,
                dateReceivedOrExpected = now - 15 * DAY_MILLIS
            )
        )

        // Expenses: 3 months of 100,000 = 300,000
        val expenses = listOf(
            Expense(id = "e1", title = "Rent 1", amountLkr = 100_000.0, category = ExpenseCategory.RENT, timestamp = now - 75 * DAY_MILLIS),
            Expense(id = "e2", title = "Rent 2", amountLkr = 100_000.0, category = ExpenseCategory.RENT, timestamp = now - 45 * DAY_MILLIS),
            Expense(id = "e3", title = "Rent 3", amountLkr = 100_000.0, category = ExpenseCategory.RENT, timestamp = now - 15 * DAY_MILLIS)
        )

        // Each 30-day block: 180,000 in − 100,000 out = 80,000 saved, so the rate is 80,000 / month.
        // (The old span-based average divided 240,000 by 2.5 "months" and overstated it as 96,000.)
        val rate = useCase(incomes, expenses, now)
        assertEquals(80_000.0, rate, 0.01)
    }

    @Test
    fun `a month with nothing logged is a gap, not a month of zero saving (NFR-2)`() {
        val now = 1700000000000L
        fun salary(daysAgo: Int) = IncomeStream(
            source = IncomeSource.SALARY, title = "Salary", clientOrPlatform = "Employer",
            originalAmount = 180_000.0, currency = Currency.LKR, amountInLkr = 180_000.0,
            status = InvoiceStatus.RECEIVED, dateReceivedOrExpected = now - daysAgo * DAY_MILLIS
        )
        fun rent(daysAgo: Int) = Expense(
            title = "Rent", amountLkr = 100_000.0, category = ExpenseCategory.RENT, timestamp = now - daysAgo * DAY_MILLIS
        )
        // Logged 75 and 15 days ago; nothing at all in the middle block (a three-week gap).
        val rate = useCase(listOf(salary(75), salary(15)), listOf(rent(75), rent(15)), now)
        assertEquals(80_000.0, rate, 0.01)
    }

    @Test
    fun `returns zero when no transactions exist`() {
        val now = 1700000000000L
        val rate = useCase(emptyList(), emptyList(), now)
        assertEquals(0.0, rate, 0.01)
    }
}
