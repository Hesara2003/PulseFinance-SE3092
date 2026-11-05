package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.Currency
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import com.se3092.pulsefinance.domain.model.Periods
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

/** NFR-2: months with no records are skipped by the income basis, never averaged in as zero. */
class SummaryLoggingGapTest {

    private val useCase = CalculateFinancialSummaryUseCase()
    private val now = Calendar.getInstance().apply { set(2026, Calendar.MAY, 10, 12, 0, 0) }.timeInMillis

    private fun salary(monthOffset: Int, amount: Double) = IncomeStream(
        id = "s$monthOffset",
        source = IncomeSource.SALARY,
        title = "Salary",
        clientOrPlatform = "Employer",
        originalAmount = amount,
        currency = Currency.LKR,
        amountInLkr = amount,
        status = InvoiceStatus.RECEIVED,
        dateReceivedOrExpected = Periods.month(now, monthOffset).startMillis + 1000
    )

    @Test
    fun `income basis averages only months that have records`() {
        // Only 2 of the last 3 months were logged; the empty one must not drag the average to 100,000.
        val summary = useCase(listOf(salary(-1, 150_000.0), salary(-3, 150_000.0)), emptyList(), null, now)

        assertEquals(150_000.0, summary.incomeBasisLkr, 0.01)
        assertTrue(summary.incomeBasisIsAverage)
    }

    @Test
    fun `no history at all gives a zero basis without failing`() {
        val summary = useCase(emptyList(), emptyList(), null, now)

        assertEquals(0.0, summary.incomeBasisLkr, 0.0)
        assertEquals(0.0, summary.safeToSpendDailyLkr, 0.0)
    }
}
