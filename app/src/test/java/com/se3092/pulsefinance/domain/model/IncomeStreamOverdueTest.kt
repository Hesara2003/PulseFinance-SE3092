package com.se3092.pulsefinance.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class IncomeStreamOverdueTest {

    private val now = 1_800_000_000_000L

    private fun invoice(dueDaysAgo: Int, status: InvoiceStatus = InvoiceStatus.PENDING_INVOICE) = IncomeStream(
        source = IncomeSource.FREELANCE,
        title = "Milestone 2",
        clientOrPlatform = "Client",
        originalAmount = 50_000.0,
        currency = Currency.LKR,
        status = status,
        dateReceivedOrExpected = now - dueDaysAgo * DAY_MILLIS
    )

    @Test
    fun `an invoice ten days past due is ten days overdue`() {
        assertEquals(10, invoice(dueDaysAgo = 10).daysOverdue(now))
    }

    @Test
    fun `an invoice not yet due is not overdue`() {
        assertEquals(0, invoice(dueDaysAgo = -3).daysOverdue(now))
    }

    @Test
    fun `received income is never overdue`() {
        assertEquals(0, invoice(dueDaysAgo = 10, status = InvoiceStatus.RECEIVED).daysOverdue(now))
    }
}
