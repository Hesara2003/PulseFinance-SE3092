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

/** FR-INC-11: offering Kavindu's monthly salary again, without ever adding it for him. */
class RecurringIncomeSuggestionsTest {

    private fun at(year: Int, month: Int, day: Int) =
        Calendar.getInstance().apply { clear(); set(year, month, day, 10, 0) }.timeInMillis

    private val now = at(2026, Calendar.NOVEMBER, 5)

    private fun salary(date: Long, amount: Double = 185_000.0, id: String = "s$date") = IncomeStream(
        id = id, userId = "uid", source = IncomeSource.SALARY, title = "Monthly Salary",
        clientOrPlatform = "Employer", originalAmount = amount, currency = Currency.LKR,
        amountInLkr = amount, status = InvoiceStatus.RECEIVED, dateReceivedOrExpected = date,
        isRecurringMonthly = true
    )

    @Test
    fun `last month's salary is offered when this month's hasn't been recorded`() {
        val due = recurringIncomeDue(listOf(salary(at(2026, Calendar.OCTOBER, 25))), now)

        assertEquals(1, due.size)
        assertEquals(185_000.0, due.single().amountInLkr, 0.0)
    }

    @Test
    fun `nothing is offered once this month's has been recorded`() {
        val incomes = listOf(salary(at(2026, Calendar.OCTOBER, 25)), salary(at(2026, Calendar.NOVEMBER, 1)))

        assertTrue(recurringIncomeDue(incomes, now).isEmpty())
    }

    @Test
    fun `a series is offered once, from its most recent entry`() {
        val incomes = listOf(
            salary(at(2026, Calendar.AUGUST, 25), amount = 170_000.0),
            salary(at(2026, Calendar.OCTOBER, 25), amount = 185_000.0),
            salary(at(2026, Calendar.SEPTEMBER, 25), amount = 175_000.0)
        )

        val due = recurringIncomeDue(incomes, now)

        assertEquals(1, due.size)
        assertEquals(185_000.0, due.single().amountInLkr, 0.0)
    }

    @Test
    fun `income not marked monthly, or paid in dollars, is never suggested`() {
        val oneOff = salary(at(2026, Calendar.OCTOBER, 25)).copy(isRecurringMonthly = false)
        val adsense = salary(at(2026, Calendar.OCTOBER, 20)).copy(
            source = IncomeSource.ADSENSE, title = "AdSense", currency = Currency.USD, exchangeRateToLkr = 301.0
        )

        assertTrue(recurringIncomeDue(listOf(oneOff, adsense), now).isEmpty())
    }

    @Test
    fun `an unpaid invoice or a loss does not start a habit`() {
        val pending = salary(at(2026, Calendar.OCTOBER, 25)).copy(status = InvoiceStatus.PENDING_INVOICE)
        val loss = salary(at(2026, Calendar.OCTOBER, 24), amount = -5_000.0).copy(title = "Trade")

        assertTrue(recurringIncomeDue(listOf(pending, loss), now).isEmpty())
    }

    @Test
    fun `adding it makes a new received record dated today with the same amount`() {
        val last = salary(at(2026, Calendar.OCTOBER, 25))

        val added = last.asThisMonth(now)

        assertEquals("", added.id)
        assertEquals(now, added.dateReceivedOrExpected)
        assertEquals(InvoiceStatus.RECEIVED, added.status)
        assertEquals(last.amountInLkr, added.amountInLkr, 0.0)
        assertTrue(added.dateReceivedOrExpected in Periods.month(now))
    }
}
