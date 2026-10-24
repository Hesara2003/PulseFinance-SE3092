package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.ExpenseCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BankSmsParserTest {

    private val parser = BankSmsParser()

    @Test
    fun `parses Commercial Bank Sri Lanka SMS format correctly`() {
        val sms = "COMBANK: Txn of LKR 1450.00 at PickMe on 24-Oct 11:20 using card *1234. Avail Bal: LKR 45200.00"
        val parsed = parser.parse(sms)

        assertNotNull(parsed)
        parsed!!
        assertEquals("Commercial Bank", parsed.bankName)
        assertEquals(1450.0, parsed.amountLkr, 0.01)
        assertEquals("PickMe", parsed.merchant)
        assertEquals(ExpenseCategory.TRANSPORT, parsed.suggestedCategory)
    }

    @Test
    fun `parses Sampath Bank SMS format correctly`() {
        val sms = "Sampath Bank: Purchased for LKR 2400.00 at UberEats Colombo with card 4567. Bal LKR 125000.00"
        val parsed = parser.parse(sms)

        assertNotNull(parsed)
        parsed!!
        assertEquals("Sampath Bank", parsed.bankName)
        assertEquals(2400.0, parsed.amountLkr, 0.01)
        assertEquals("UberEats Colombo", parsed.merchant)
        assertEquals(ExpenseCategory.FOOD_DINING, parsed.suggestedCategory)
    }

    @Test
    fun `parses Hatton National Bank HNB SMS format correctly`() {
        val sms = "Your HNB account has been debited LKR 4800.00 at SLT Broadband on 2026-10-20. Card ending 8899"
        val parsed = parser.parse(sms)

        assertNotNull(parsed)
        parsed!!
        assertEquals("Hatton National Bank", parsed.bankName)
        assertEquals(4800.0, parsed.amountLkr, 0.01)
        assertEquals("SLT Broadband", parsed.merchant)
        assertEquals(ExpenseCategory.UTILITIES, parsed.suggestedCategory)
        assertTrue(parsed.isCommitted)
    }

    @Test
    fun `returns null for non-transactional SMS`() {
        val nonBankingSms = "Hey dude, are we meeting up for dinner tonight at 7?"
        val parsed = parser.parse(nonBankingSms)
        assertNull(parsed)
    }
}
