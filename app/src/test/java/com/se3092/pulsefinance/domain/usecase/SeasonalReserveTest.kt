package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.Currency
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.SeasonalReserve
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

/** FR-LK-5: the one-tap set-aside from a Seasonal Peak alert. */
class SeasonalReserveTest {

    private fun at(year: Int, month: Int, day: Int): Long =
        Calendar.getInstance().apply { set(year, month, day, 12, 0, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis

    private val vesak = at(2027, Calendar.MAY, 15)

    @Test
    fun `alert carries a stable key and the peak date to store a reserve against`() {
        val alert = SeasonalPeakChecker()(emptyList(), now = at(2027, Calendar.APRIL, 25))
        assertNotNull(alert)
        assertEquals("vesak_festival_2027", alert!!.peakKey)
        val peakDay = Calendar.getInstance().apply { timeInMillis = alert.peakDateMillis }
        assertEquals(Calendar.MAY, peakDay.get(Calendar.MONTH))
        assertEquals(15, peakDay.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun `reserve is active from the month it was set aside until the peak day`() {
        val reserve = SeasonalReserve("vesak_festival_2027", 20_000.0, setAsideAt = at(2027, Calendar.MAY, 2), peakDateMillis = vesak)
        assertTrue(reserve.isActive(at(2027, Calendar.MAY, 10)))
        assertTrue(reserve.isActive(at(2027, Calendar.MAY, 15)))
        assertFalse(reserve.isActive(at(2027, Calendar.MAY, 16)))
    }

    @Test
    fun `reserve set aside last month no longer counts, so festival spending isn't counted twice`() {
        val reserve = SeasonalReserve("vesak_festival_2027", 20_000.0, setAsideAt = at(2027, Calendar.APRIL, 25), peakDateMillis = vesak)
        assertFalse(reserve.isActive(at(2027, Calendar.MAY, 3)))
    }

    @Test
    fun `active reserve lowers Safe-to-Spend and is reported in the summary`() {
        val now = at(2027, Calendar.MAY, 2)
        val salary = IncomeStream(
            source = IncomeSource.SALARY,
            title = "Salary",
            clientOrPlatform = "TechCorp",
            originalAmount = 300_000.0,
            currency = Currency.LKR,
            dateReceivedOrExpected = now - 1000
        )
        val reserve = SeasonalReserve("vesak_festival_2027", 31_000.0, setAsideAt = now, peakDateMillis = vesak)
        val useCase = CalculateFinancialSummaryUseCase()

        val without = useCase(listOf(salary), emptyList(), goal = null, now = now)
        val with = useCase(listOf(salary), emptyList(), goal = null, now = now, seasonalReserves = listOf(reserve))

        assertEquals(31_000.0, with.seasonalReserveLkr, 0.01)
        assertEquals(31_000.0 / with.daysRemainingInMonth, without.safeToSpendDailyLkr - with.safeToSpendDailyLkr, 0.01)
    }
}
