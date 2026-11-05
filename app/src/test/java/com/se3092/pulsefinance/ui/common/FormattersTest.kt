package com.se3092.pulsefinance.ui.common

import org.junit.Assert.assertEquals
import org.junit.Test

class FormattersTest {

    @Test
    fun `lkr amounts use grouping and optional decimals`() {
        assertEquals("LKR 34,000", lkr(34000.0))
        assertEquals("LKR 13,725.00", lkr(13725.0, decimals = 2))
        assertEquals("- LKR 8,500", signedLkr(-8500.0))
        assertEquals("+ LKR 1,200", signedLkr(1200.0))
    }

    @Test
    fun `compact amounts abbreviate thousands and millions`() {
        assertEquals("850", compactAmount(850.0))
        assertEquals("1.4k", compactAmount(1400.0))
        assertEquals("34k", compactAmount(34000.0))
        assertEquals("16k", compactAmount(16000.0))
        assertEquals("1.2M", compactAmount(1_200_000.0))
    }

    @Test
    fun `names fall back from display name to email`() {
        assertEquals("Kavindu", displayNameFor("Kavindu Silva", "k@x.io"))
        assertEquals("KS", initialsFor("Kavindu Silva", "k@x.io"))
        assertEquals("Kavindu", displayNameFor(null, "kavindu.silva@pulsemail.io"))
        assertEquals("K", initialsFor(null, "kavindu.silva@pulsemail.io"))
        assertEquals("there", displayNameFor(null, null))
        assertEquals("?", initialsFor(null, null))
    }

    @Test
    fun `a blank display name is treated as missing`() {
        assertEquals("Kavindu", displayNameFor("   ", "kavindu@pulsemail.io"))
    }
}
