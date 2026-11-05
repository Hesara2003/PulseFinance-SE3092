package com.se3092.pulsefinance.notifications

import com.se3092.pulsefinance.domain.model.Currency
import com.se3092.pulsefinance.domain.model.DAY_MILLIS
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import com.se3092.pulsefinance.domain.model.Periods
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** FR-NTF-1: what the daily reminder says, and when it stays quiet. */
class OverdueReminderTest {

    private val now = 1_790_000_000_000L

    private fun invoice(title: String, amount: Double, dueDaysAgo: Long, status: InvoiceStatus = InvoiceStatus.PENDING_INVOICE) = IncomeStream(
        id = title, userId = "u", source = IncomeSource.FREELANCE, title = title, clientOrPlatform = "Client",
        originalAmount = amount, currency = Currency.LKR, amountInLkr = amount, status = status,
        dateReceivedOrExpected = now - dueDaysAgo * DAY_MILLIS
    )

    @Test
    fun `nothing overdue means no reminder`() {
        val incomes = listOf(
            invoice("Not due yet", 90_000.0, dueDaysAgo = -5),
            invoice("Paid", 50_000.0, dueDaysAgo = 10, status = InvoiceStatus.RECEIVED)
        )

        assertNull(overdueReminder(incomes, now))
    }

    @Test
    fun `one late invoice names the work, the amount and how late it is`() {
        val reminder = overdueReminder(listOf(invoice("Milestone 2", 150_000.0, dueDaysAgo = 9)), now)!!

        assertEquals("An invoice is overdue", reminder.title)
        assertEquals("Milestone 2 · LKR 150,000 is 9 days late. Time to follow up.", reminder.text)
    }

    @Test
    fun `several late invoices are totalled and the oldest is reported`() {
        val reminder = overdueReminder(
            listOf(invoice("A", 80_000.0, dueDaysAgo = 3), invoice("B", 150_000.0, dueDaysAgo = 21), invoice("Paid", 10.0, 5, InvoiceStatus.RECEIVED)),
            now
        )!!

        assertEquals(2, reminder.count)
        assertEquals(230_000.0, reminder.totalLkr, 0.0)
        assertEquals(21, reminder.daysLateOldest)
        assertEquals("2 invoices are overdue", reminder.title)
        assertTrue(reminder.text.contains("LKR 230,000"))
    }

    @Test
    fun `a single day late reads in the singular`() {
        val reminder = overdueReminder(listOf(invoice("Logo", 25_000.0, dueDaysAgo = 1)), now)!!

        assertTrue(reminder.text.contains("1 day late"))
    }

    @Test
    fun `at most one reminder a day`() {
        val todayStart = Periods.startOfDay(now)

        assertTrue(shouldNotifyToday(lastNotifiedDayStart = 0L, now = now))
        assertTrue(shouldNotifyToday(lastNotifiedDayStart = todayStart - DAY_MILLIS, now = now))
        assertFalse(shouldNotifyToday(lastNotifiedDayStart = todayStart, now = now))
    }
}
