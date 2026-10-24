package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.Currency
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import com.se3092.pulsefinance.domain.model.SavingsGoal
import org.junit.Assert.assertTrue
import org.junit.Test

class ExportDataToCsvUseCaseTest {

    private val useCase = ExportDataToCsvUseCase()

    @Test
    fun `generates valid CSV containing all financial sections with escaping`() {
        val now = 1700000000000L

        val incomes = listOf(
            IncomeStream(
                id = "inc1",
                source = IncomeSource.FREELANCE,
                title = "Web Development, Client A",
                clientOrPlatform = "Acme, Inc.",
                originalAmount = 500.0,
                currency = Currency.USD,
                exchangeRateToLkr = 310.0,
                amountInLkr = 155_000.0,
                status = InvoiceStatus.RECEIVED,
                dateReceivedOrExpected = now
            )
        )

        val expenses = listOf(
            Expense(
                id = "exp1",
                title = "Dinner at 'Cafe, Colombo'",
                amountLkr = 4500.0,
                category = ExpenseCategory.FOOD_DINING,
                isCommitted = false,
                paymentAccountName = "HNB Primary",
                timestamp = now
            )
        )

        val goals = listOf(
            SavingsGoal(
                id = "goal1",
                title = "MacBook Pro M3 Max",
                targetAmountLkr = 650_000.0,
                currentSavedLkr = 200_000.0,
                targetDateMillis = now + 180L * 24 * 60 * 60 * 1000,
                usdReferencePrice = 1999.0
            )
        )

        val csv = useCase.generateCsv(incomes, expenses, goals)

        // Validate sections
        assertTrue(csv.contains("=== INCOMES & INVOICES ==="))
        assertTrue(csv.contains("=== EXPENSES & TRANSACTIONS ==="))
        assertTrue(csv.contains("=== SAVINGS GOALS ==="))

        // Validate escaped values
        assertTrue(csv.contains("\"Web Development, Client A\""))
        assertTrue(csv.contains("\"Acme, Inc.\""))
        assertTrue(csv.contains("\"Dinner at 'Cafe, Colombo'\""))

        // Validate USD reference price
        assertTrue(csv.contains("1999.0"))
        assertTrue(csv.contains("650000.0"))
        assertTrue(csv.contains("FREELANCE"))
    }
}
