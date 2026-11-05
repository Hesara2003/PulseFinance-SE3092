package com.se3092.pulsefinance.ui.screens.income

import com.se3092.pulsefinance.domain.model.Currency
import com.se3092.pulsefinance.domain.model.DAY_MILLIS
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** FR-INC-10: correcting an income record without re-entering it or losing the rate he received. */
class IncomeFormStateTest {

    private val adsense = IncomeStream(
        id = "inc_1", userId = "uid", source = IncomeSource.ADSENSE, title = "AdSense Sept",
        clientOrPlatform = "Google AdSense", originalAmount = 412.5, currency = Currency.USD,
        exchangeRateToLkr = 301.25, amountInLkr = 412.5 * 301.25, status = InvoiceStatus.RECEIVED,
        dateReceivedOrExpected = 1_760_000_000_000L, notes = "paid to Payoneer"
    )

    @Test
    fun `editing shows the stored rate, not today's`() {
        val form = IncomeFormState.editing(adsense)

        assertTrue(form.isVisible)
        assertTrue(form.isEditing)
        assertEquals("412.5", form.amountInput)
        assertEquals("301.25", form.exchangeRateInput)
        assertEquals(Currency.USD, form.selectedCurrency)
        assertEquals("AdSense Sept", form.titleInput)
    }

    @Test
    fun `a new form is not editing and takes today plus the chosen offset`() {
        val now = 1_770_000_000_000L
        val form = IncomeFormState(dayOffset = -7)

        assertFalse(form.isEditing)
        assertNull(form.editing)
        assertEquals(now - 7 * DAY_MILLIS, form.dateMillis(now))
    }

    @Test
    fun `the stored date is kept until a date is picked`() {
        val untouched = IncomeFormState.editing(adsense)
        assertTrue(untouched.usesOriginalDate)
        assertEquals(adsense.dateReceivedOrExpected, untouched.dateMillis(now = 1_770_000_000_000L))

        val picked = untouched.copy(dayOffset = -1, dateChanged = true)
        assertFalse(picked.usesOriginalDate)
        assertEquals(1_770_000_000_000L - DAY_MILLIS, picked.dateMillis(1_770_000_000_000L))
    }

    @Test
    fun `a crypto loss is shown as a positive amount with the loss toggle on`() {
        val loss = adsense.copy(source = IncomeSource.CRYPTO, currency = Currency.USDT, originalAmount = -1_200.0)

        val form = IncomeFormState.editing(loss)

        assertEquals("1200", form.amountInput)
        assertTrue(form.isCryptoLoss)
    }

    @Test
    fun `an overdue invoice is edited as an invoice still waiting`() {
        val overdue = adsense.copy(status = InvoiceStatus.OVERDUE)

        assertEquals(InvoiceStatus.PENDING_INVOICE, IncomeFormState.editing(overdue).selectedStatus)
    }

    @Test
    fun `a rupee record has no foreign rate note`() {
        val salary = adsense.copy(source = IncomeSource.SALARY, currency = Currency.LKR, exchangeRateToLkr = 1.0, originalAmount = 150_000.0)

        val form = IncomeFormState.editing(salary)

        assertEquals("", form.rateSourceInfo)
        assertEquals("150000", form.amountInput)
        assertEquals("1", form.exchangeRateInput)
    }
}
