package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.DAY_MILLIS
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class SeasonalPeakCheckerTest {

    private val checker = SeasonalPeakChecker()

    @Test
    fun `triggers Avurudu warning 20 days prior to April 14`() {
        val cal = Calendar.getInstance()
        val currentYear = cal.get(Calendar.YEAR)
        // Set date to March 25 (20 days before April 14)
        cal.set(currentYear, Calendar.MARCH, 25, 12, 0, 0)
        val now = cal.timeInMillis

        val alert = checker(emptyList(), now)

        assertNotNull(alert)
        alert!!
        assertEquals("Sinhala & Tamil New Year (Avurudu)", alert.festivalName)
        assertEquals(20, alert.daysUntilPeak)
        assertEquals(35_000.0, alert.suggestedSetAsideLkr, 0.01) // Default buffer
        assertTrue(alert.message.contains("Avurudu"))
    }

    @Test
    fun `triggers December holiday warning 10 days prior to Christmas`() {
        val cal = Calendar.getInstance()
        val currentYear = cal.get(Calendar.YEAR)
        // Set date to Dec 15 (10 days before Dec 25)
        cal.set(currentYear, Calendar.DECEMBER, 15, 12, 0, 0)
        val now = cal.timeInMillis

        val alert = checker(emptyList(), now)

        assertNotNull(alert)
        alert!!
        assertEquals("December Holiday Season", alert.festivalName)
        assertEquals(10, alert.daysUntilPeak)
        assertEquals(45_000.0, alert.suggestedSetAsideLkr, 0.01)
    }

    @Test
    fun `returns null when no seasonal peaks within 30 days`() {
        val cal = Calendar.getInstance()
        val currentYear = cal.get(Calendar.YEAR)
        // Set date to August 15 (no major peaks within 30 days)
        cal.set(currentYear, Calendar.AUGUST, 15, 12, 0, 0)
        val now = cal.timeInMillis

        val alert = checker(emptyList(), now)
        assertNull(alert)
    }
}
