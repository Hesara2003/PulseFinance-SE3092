package com.se3092.pulsefinance.ui.screens.analytics

import com.se3092.pulsefinance.domain.model.DAY_MILLIS
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.Period
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TimelineBarsGapTest {

    private val start = 1_700_000_000_000L

    private fun days(n: Int) = (0 until n).map { i ->
        Triple("D$i", "", Period(start + i * DAY_MILLIS, start + (i + 1) * DAY_MILLIS))
    }

    private fun expense(day: Int, amount: Double) = Expense(
        id = "e$day",
        title = "Lunch",
        amountLkr = amount,
        category = ExpenseCategory.FOOD_DINING,
        timestamp = start + day * DAY_MILLIS + 1000
    )

    @Test
    fun `days without records are flagged as gaps rather than zero spending`() {
        val bars = timelineBars(days(3), listOf(expense(0, 500.0), expense(2, 300.0)))

        assertTrue(bars[0].hasRecords)
        assertFalse(bars[1].hasRecords)
        assertTrue(bars[2].hasRecords)
    }

    @Test
    fun `a gap does not become the peak and keeps a zero fill`() {
        val bars = timelineBars(days(3), listOf(expense(0, 500.0), expense(2, 300.0)))

        assertTrue(bars[0].isPeak)
        assertFalse(bars[1].isPeak)
        assertEquals(0f, bars[1].fillRatio, 0f)
    }

    @Test
    fun `a period with no records at all marks every bar as a gap`() {
        val bars = timelineBars(days(7), emptyList())

        assertTrue(bars.none { it.hasRecords })
        assertTrue(bars.none { it.isPeak })
    }
}
