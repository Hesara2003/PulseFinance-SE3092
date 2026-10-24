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

        // Net savings over 90 days = 540,000 - 300,000 = 240,000
        // Window = 75 days (~2.5 months) -> 240,000 / 2.5 = 96,000 / mo
        val rate = useCase(incomes, expenses, now)
        assertEquals(96_000.0, rate, 100.0)
    }

    @Test
    fun `returns zero when no transactions exist`() {
        val now = 1700000000000L
        val rate = useCase(emptyList(), emptyList(), now)
        assertEquals(0.0, rate, 0.01)
    }
}
