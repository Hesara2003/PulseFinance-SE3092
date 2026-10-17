package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.Currency
import com.se3092.pulsefinance.domain.model.SavingsGoal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** QA scenarios for Kavindu Silva's MacBook Pro M4 goal and multi-currency income. */
class GoalAndCurrencyQaTest {

    private val day = 24L * 60 * 60 * 1000

    /** LKR 490,000 target, LKR 11,200 saved, 360 days left => LKR 1,330/day, LKR 39,900/month. */
    private fun macBookGoal(saved: Double = 11200.0) = SavingsGoal(
        targetAmountLkr = 490000.0,
        currentSavedLkr = saved,
        targetDateMillis = System.currentTimeMillis() + 360 * day + day / 2
    )

    private val velocity = CalculateGoalVelocityUseCase()
    private val converter = ConvertCurrencyUseCase()

    @Test
    fun `required pacing matches the persona scenario`() {
        val report = velocity(macBookGoal(), actualMonthlySavingsRateLkr = 0.0)

        assertEquals(1330.0, report.dailyPacingTargetLkr, 0.01)
        assertEquals(39900.0, report.monthlyPacingTargetLkr, 0.01)
    }

    @Test
    fun `saving nothing is off track with full remaining shortfall`() {
        val report = velocity(macBookGoal(), actualMonthlySavingsRateLkr = 0.0)

        assertFalse(report.isPacingOnTrack)
        assertEquals(478800.0, report.projectedShortfallLkr, 1.0)
        assertTrue(report.behavioralInsight.startsWith("Currently saving 0/month"))
    }

    @Test
    fun `saving exactly the required rate is on track`() {
        val report = velocity(macBookGoal(), actualMonthlySavingsRateLkr = 39900.0)

        assertTrue(report.isPacingOnTrack)
        assertEquals(0.0, report.projectedShortfallLkr, 0.0)
        assertTrue(report.behavioralInsight.startsWith("You are on track"))
    }

    @Test
    fun `partial saving reports a pacing warning with a positive shortfall`() {
        val report = velocity(macBookGoal(), actualMonthlySavingsRateLkr = 20000.0)

        assertFalse(report.isPacingOnTrack)
        assertTrue(report.projectedShortfallLkr > 0)
        assertTrue(report.behavioralInsight.startsWith("Pacing warning"))
    }

    @Test
    fun `fully funded goal needs no further savings`() {
        val report = velocity(macBookGoal(saved = 490000.0), actualMonthlySavingsRateLkr = 0.0)

        assertEquals(0.0, report.dailyPacingTargetLkr, 0.0)
        assertTrue(report.isPacingOnTrack)
    }

    @Test
    fun `passed deadline still yields finite pacing`() {
        val overdue = macBookGoal().copy(targetDateMillis = System.currentTimeMillis() - 10 * day)

        assertEquals(1L, overdue.daysRemaining)
        assertEquals(overdue.remainingAmountLkr, overdue.requiredDailySavingsLkr, 0.01)
    }

    @Test
    fun `opportunity cost translates spending into goal days`() {
        val dailyPace = macBookGoal().requiredDailySavingsLkr

        assertEquals(0.64, 850.0 / dailyPace, 0.01)   // cafe
        assertEquals(1.09, 1450.0 / dailyPace, 0.01)  // PickMe
        assertEquals(1.88, 2500.0 / dailyPace, 0.01)  // dinner
    }

    @Test
    fun `lkr passes through unchanged`() {
        assertEquals(45000.0, converter.toLkr(45000.0, Currency.LKR), 0.0)
    }

    @Test
    fun `usd adsense income converts at the benchmark rate`() {
        assertEquals(13725.0, converter.toLkr(45.0, Currency.USD), 0.001)
    }

    @Test
    fun `usdt uses its own benchmark rate`() {
        assertEquals(306.5 * 100, converter.toLkr(100.0, Currency.USDT), 0.001)
        assertEquals(306.5, converter.getBenchmarkRate(Currency.USDT), 0.0)
    }

    @Test
    fun `custom rate overrides the benchmark`() {
        assertEquals(3200.0, converter.toLkr(10.0, Currency.USD, customRate = 320.0), 0.001)
    }

    @Test
    fun `zero amount converts to zero`() {
        Currency.values().forEach {
            assertEquals(0.0, converter.toLkr(0.0, it), 0.0)
        }
    }
}
