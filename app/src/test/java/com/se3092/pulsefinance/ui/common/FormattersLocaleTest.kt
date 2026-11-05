package com.se3092.pulsefinance.ui.common

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Locale

/** Amounts read 1,234,567 whatever language the phone is set to. */
class FormattersLocaleTest {

    private lateinit var original: Locale

    @Before
    fun useAGermanPhone() {
        original = Locale.getDefault()
        Locale.setDefault(Locale.GERMANY) // groups with '.', decimals with ','
    }

    @After
    fun restore() = Locale.setDefault(original)

    @Test
    fun `rupee amounts keep comma grouping`() {
        assertEquals("LKR 1,234,567", lkr(1_234_567.0))
        assertEquals("LKR 1,234.50", lkr(1_234.5, decimals = 2))
    }

    @Test
    fun `compact amounts keep a decimal point`() {
        assertEquals("1.4k", compactAmount(1_400.0))
        assertEquals("1.2M", compactAmount(1_200_000.0))
    }
}
