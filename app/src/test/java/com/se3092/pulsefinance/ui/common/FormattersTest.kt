package com.se3092.pulsefinance.ui.common

import com.se3092.pulsefinance.domain.model.Currency
import com.se3092.pulsefinance.domain.model.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Test

class FormattersTest {

    @Test
    fun `lkr amounts use grouping and optional decimals`() {
        assertEquals("LKR 34,000", Formatters.lkr(34000.0))
        assertEquals("LKR 13,725.00", Formatters.lkr(13725.0, decimals = 2))
        assertEquals("- LKR 8,500", Formatters.signedLkr(8500.0, isPositive = false))
    }

    @Test
    fun `compact amounts trim trailing zeros`() {
        assertEquals("850", Formatters.compact(850.0))
        assertEquals("1.5k", Formatters.compact(1450.0))
        assertEquals("34k", Formatters.compact(34000.0))
        assertEquals("16.5k", Formatters.compact(16500.0))
        assertEquals("1.25M", Formatters.compact(1_250_000.0))
    }

    @Test
    fun `original currency formatting`() {
        assertEquals("$45.00", Formatters.original(45.0, Currency.USD))
        assertEquals("USDT 20.00", Formatters.original(20.0, Currency.USDT))
    }

    @Test
    fun `names fall back from display name to email`() {
        val google = UserProfile(uid = "1", email = "k@x.io", displayName = "Kavindu Silva")
        val email = UserProfile(uid = "2", email = "kavindu.silva@pulsemail.io", displayName = null)

        assertEquals("Kavindu", Formatters.firstName(google))
        assertEquals("KS", Formatters.initials(google))
        assertEquals("Kavindu", Formatters.firstName(email))
        assertEquals("there", Formatters.firstName(null))
    }
}
