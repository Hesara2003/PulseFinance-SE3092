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

    @Test
    fun `adds a personal peak month after 12 months of history`() {
        // July 2025 - June 2026 at LKR 50,000/month, except August 2025 at LKR 80,000.
        val history = (0 until 12).map { i ->
            val month = Calendar.JULY + i
            spend(2025 + month / 12, month % 12, if (month % 12 == Calendar.AUGUST) 80_000.0 else 50_000.0)
        }
        val now = at(2026, Calendar.JULY, 10)

        val alert = checker(history, now)

        assertNotNull(alert)
        alert!!
        assertTrue(alert.isPersonalPeak)
        assertEquals("Your August spending peak", alert.festivalName)
        assertEquals(22, alert.daysUntilPeak)
        // Average = (11 x 50,000 + 80,000) / 12 = 52,500; excess = 27,500
        assertEquals(27_500.0, alert.suggestedSetAsideLkr, 0.01)
        assertEquals(27_500.0, alert.lastYearExcessLkr!!, 0.01)
    }

    @Test
    fun `no personal peaks before 12 months of history`() {
        // Same spike, but records only start in August 2025: 11 months of history.
        val history = (1 until 12).map { i ->
            val month = Calendar.JULY + i
            spend(2025 + month / 12, month % 12, if (month % 12 == Calendar.AUGUST) 80_000.0 else 50_000.0)
        }

        assertNull(checker(history, at(2026, Calendar.JULY, 10)))
    }

    @Test
    fun `historical excess averages only months that have records`() {
        // April 2025 at 70,000 and September 2025 - February 2026 at 40,000; nothing else logged.
        val history = listOf(spend(2025, Calendar.APRIL, 70_000.0)) +
            (Calendar.SEPTEMBER..Calendar.DECEMBER).map { spend(2025, it, 40_000.0) } +
            (Calendar.JANUARY..Calendar.FEBRUARY).map { spend(2026, it, 40_000.0) }

        val alert = checker(history, at(2026, Calendar.MARCH, 25))

        assertNotNull(alert)
        alert!!
        assertEquals("Sinhala & Tamil New Year (Avurudu)", alert.festivalName)
        // Average over the 7 recorded months = 310,000 / 7; dividing by 12 would overstate the excess.
        val expectedExcess = 70_000.0 - 310_000.0 / 7
        assertEquals(expectedExcess, alert.suggestedSetAsideLkr, 0.01)
        assertEquals(expectedExcess, alert.lastYearExcessLkr!!, 0.01)
        assertTrue(alert.message.contains("Last year you spent"))
    }

    private fun at(year: Int, month: Int, day: Int): Long =
        Calendar.getInstance().apply { set(year, month, day, 12, 0, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis

    private fun spend(year: Int, month: Int, amountLkr: Double) = Expense(
        title = "Spending",
        amountLkr = amountLkr,
        category = ExpenseCategory.FOOD_DINING,
        timestamp = at(year, month, 15)
    )
}
