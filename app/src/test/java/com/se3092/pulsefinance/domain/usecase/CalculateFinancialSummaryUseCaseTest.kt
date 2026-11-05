package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.Currency
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import com.se3092.pulsefinance.domain.model.Periods
import com.se3092.pulsefinance.domain.model.SavingsGoal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class CalculateFinancialSummaryUseCaseTest {

    private val useCase = CalculateFinancialSummaryUseCase()

    @Test
    fun `excludes crypto profit from 3-month average baseline per FR-DASH-3`() {
        val cal = Calendar.getInstance()
        cal.set(2026, Calendar.MAY, 10, 12, 0, 0)
        val now = cal.timeInMillis

        // Month -1 (April 2026): Salary 150,000 + Crypto Profit 300,000
        val aprilSalary = IncomeStream(
            id = "1",
            source = IncomeSource.SALARY,
            title = "April Salary",
            clientOrPlatform = "TechCorp",
            originalAmount = 150_000.0,
            currency = Currency.LKR,
            amountInLkr = 150_000.0,
            status = InvoiceStatus.RECEIVED,
            dateReceivedOrExpected = Periods.month(now, -1).startMillis + 1000
        )
        val aprilCryptoProfit = IncomeStream(
            id = "2",
            source = IncomeSource.CRYPTO,
            title = "Binance ETH Gain",
            clientOrPlatform = "Binance",
            originalAmount = 300_000.0,
            currency = Currency.LKR,
            amountInLkr = 300_000.0,
            status = InvoiceStatus.RECEIVED,
            dateReceivedOrExpected = Periods.month(now, -1).startMillis + 2000
        )

        // Month -2 (March 2026): Salary 150,000
        val marchSalary = IncomeStream(
            id = "3",
            source = IncomeSource.SALARY,
            title = "March Salary",
            clientOrPlatform = "TechCorp",
            originalAmount = 150_000.0,
            currency = Currency.LKR,
            amountInLkr = 150_000.0,
            status = InvoiceStatus.RECEIVED,
            dateReceivedOrExpected = Periods.month(now, -2).startMillis + 1000
        )

        // Month -3 (February 2026): Salary 150,000
        val febSalary = IncomeStream(
            id = "4",
            source = IncomeSource.SALARY,
            title = "Feb Salary",
            clientOrPlatform = "TechCorp",
            originalAmount = 150_000.0,
            currency = Currency.LKR,
            amountInLkr = 150_000.0,
            status = InvoiceStatus.RECEIVED,
            dateReceivedOrExpected = Periods.month(now, -3).startMillis + 1000
        )

        // Current month (May 2026): Salary has not arrived yet, only freelance 20,000
        val mayFreelance = IncomeStream(
            id = "5",
            source = IncomeSource.FREELANCE,
            title = "Small UI task",
            clientOrPlatform = "Client A",
            originalAmount = 20_000.0,
            currency = Currency.LKR,
            amountInLkr = 20_000.0,
            status = InvoiceStatus.RECEIVED,
            dateReceivedOrExpected = now - 1000
        )

        val incomes = listOf(aprilSalary, aprilCryptoProfit, marchSalary, febSalary, mayFreelance)
        val expenses = emptyList<Expense>()

        val summary = useCase(incomes, expenses, null, now)

        // The 3-month baseline should only average salaries: (150k + 150k + 150k) / 3 = 150,000.
        // If crypto profit were included, April would be 450,000 and average would be 250,000.
        assertEquals(150_000.0, summary.incomeBasisLkr, 0.01)
        assertTrue(summary.incomeBasisIsAverage)
    }

    @Test
    fun `includes crypto loss in 3-month average baseline per FR-DASH-3`() {
        val cal = Calendar.getInstance()
        cal.set(2026, Calendar.MAY, 10, 12, 0, 0)
        val now = cal.timeInMillis

        // Month -1 (April 2026): Salary 150,000 + Crypto Loss -50,000 -> Net 100,000
        val aprilSalary = IncomeStream(
            id = "1",
            source = IncomeSource.SALARY,
            title = "April Salary",
            clientOrPlatform = "TechCorp",
            originalAmount = 150_000.0,
            currency = Currency.LKR,
            amountInLkr = 150_000.0,
            status = InvoiceStatus.RECEIVED,
            dateReceivedOrExpected = Periods.month(now, -1).startMillis + 1000
        )
        val aprilCryptoLoss = IncomeStream(
            id = "2",
            source = IncomeSource.CRYPTO,
            title = "Binance Loss",
            clientOrPlatform = "Binance",
            originalAmount = -50_000.0,
            currency = Currency.LKR,
            amountInLkr = -50_000.0,
            status = InvoiceStatus.RECEIVED,
            dateReceivedOrExpected = Periods.month(now, -1).startMillis + 2000
        )

        // Month -2 (March): 150,000
        val marchSalary = IncomeStream(
            id = "3",
            source = IncomeSource.SALARY,
            title = "March Salary",
            clientOrPlatform = "TechCorp",
            originalAmount = 150_000.0,
            currency = Currency.LKR,
            amountInLkr = 150_000.0,
            status = InvoiceStatus.RECEIVED,
            dateReceivedOrExpected = Periods.month(now, -2).startMillis + 1000
        )

        // Month -3 (Feb): 150,000
        val febSalary = IncomeStream(
            id = "4",
            source = IncomeSource.SALARY,
            title = "Feb Salary",
            clientOrPlatform = "TechCorp",
            originalAmount = 150_000.0,
            currency = Currency.LKR,
            amountInLkr = 150_000.0,
            status = InvoiceStatus.RECEIVED,
            dateReceivedOrExpected = Periods.month(now, -3).startMillis + 1000
        )

        val summary = useCase(
            listOf(aprilSalary, aprilCryptoLoss, marchSalary, febSalary),
            emptyList(),
            null,
            now
        )

        // April net: 100,000. March: 150,000. Feb: 150,000.
        // Average: (100k + 150k + 150k) / 3 = 133,333.33
        assertEquals(133_333.33, summary.incomeBasisLkr, 1.0)
    }

    @Test
    fun `calculates safe-to-spend daily correctly deducting committed expenses and goal allocation`() {
        val cal = Calendar.getInstance()
        cal.set(2026, Calendar.MAY, 15, 12, 0, 0)
        val now = cal.timeInMillis

        val salary = IncomeStream(
            id = "1",
            source = IncomeSource.SALARY,
            title = "May Salary",
            clientOrPlatform = "TechCorp",
            originalAmount = 200_000.0,
            currency = Currency.LKR,
            amountInLkr = 200_000.0,
            status = InvoiceStatus.RECEIVED,
            dateReceivedOrExpected = now - 1000
        )

        val rent = Expense(
            id = "e1",
            title = "Rent",
            amountLkr = 34_000.0,
            category = ExpenseCategory.RENT,
            isCommitted = true,
            timestamp = now - 5000
        )
        val broadband = Expense(
            id = "e2",
            title = "SLT Fiber",
            amountLkr = 6_000.0,
            category = ExpenseCategory.UTILITIES,
            isCommitted = true,
            timestamp = now - 4000
        )
        val food = Expense(
            id = "e3",
            title = "Cafe Dining",
            amountLkr = 10_000.0,
            category = ExpenseCategory.FOOD_DINING,
            isCommitted = false,
            timestamp = now - 2000
        )

        // Goal needing 30,000 per month
        val goal = SavingsGoal(
            id = "g1",
            title = "MacBook Pro",
            targetAmountLkr = 300_000.0,
            currentSavedLkr = 60_000.0,
            targetDateMillis = now + 240L * 24 * 60 * 60 * 1000 // 8 months left -> 240k / 8 = 30k/mo
        )

        val summary = useCase(listOf(salary), listOf(rent, broadband, food), goal, now)

        val daysRemaining = Periods.daysRemainingInMonth(now)
        val pool = 200_000.0 - 40_000.0 - goal.requiredMonthlySavingsLkr - 10_000.0
        val expectedDaily = pool / daysRemaining

        assertEquals(200_000.0, summary.totalRealizedIncomeLkr, 0.01)
        assertEquals(40_000.0, summary.committedExpensesLkr, 0.01)
        assertEquals(10_000.0, summary.discretionaryExpensesLkr, 0.01)
        assertEquals(50_000.0, summary.totalExpensesLkr, 0.01)
        assertEquals(expectedDaily, summary.safeToSpendDailyLkr, 0.01)
        assertFalse(summary.incomeBasisIsAverage)
    }

    @Test
    fun `a month pushed below zero by a crypto loss still counts, and an empty month is skipped`() {
        val cal = Calendar.getInstance()
        cal.set(2026, Calendar.MAY, 10, 12, 0, 0)
        val now = cal.timeInMillis
        fun income(id: String, source: IncomeSource, amount: Double, monthOffset: Int) = IncomeStream(
            id = id, source = source, title = id, clientOrPlatform = "x",
            originalAmount = amount, currency = Currency.LKR, amountInLkr = amount,
            status = InvoiceStatus.RECEIVED,
            dateReceivedOrExpected = Periods.month(now, monthOffset).startMillis + 1000
        )
        // April: salary 100,000 and a 250,000 crypto loss -> -150,000. March: nothing logged.
        // February: 300,000. Basis = (-150,000 + 300,000) / 2 = 75,000.
        // (Before, April was dropped for being below zero, so the loss vanished and the basis was 300,000.)
        val summary = useCase(
            listOf(
                income("april-salary", IncomeSource.SALARY, 100_000.0, -1),
                income("april-loss", IncomeSource.CRYPTO, -250_000.0, -1),
                income("feb-salary", IncomeSource.SALARY, 300_000.0, -3)
            ),
            emptyList(),
            null,
            now
        )
        assertEquals(75_000.0, summary.incomeBasisLkr, 0.01)
    }
}
