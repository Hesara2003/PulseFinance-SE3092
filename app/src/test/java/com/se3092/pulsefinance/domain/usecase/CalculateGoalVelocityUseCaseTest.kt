package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.DAY_MILLIS
import com.se3092.pulsefinance.domain.model.SavingsGoal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculateGoalVelocityUseCaseTest {

    private val useCase = CalculateGoalVelocityUseCase()

    @Test
    fun `marks pacing on track when actual monthly savings exceeds required savings`() {
        val now = 1700000000000L
        val goal = SavingsGoal(
            id = "g1",
            title = "MacBook Air",
            targetAmountLkr = 300_000.0,
            currentSavedLkr = 100_000.0,
            // 200k remaining over 10 months (300 days) -> 20,000 / month
            targetDateMillis = now + 300 * DAY_MILLIS
        )

        val report = useCase(goal, actualMonthlySavingsRateLkr = 25_000.0, now = now)

        assertTrue(report.isPacingOnTrack)
        assertEquals(0.0, report.projectedShortfallLkr, 0.01)
        assertNotNull(report.projectedArrivalMillis)
        assertTrue(report.projectedArrivalMillis!! <= goal.targetDateMillis)
        assertTrue(report.behavioralInsight.contains("On track", ignoreCase = true))
    }

    @Test
    fun `calculates shortfall when actual savings is below required pace`() {
        val now = 1700000000000L
        val goal = SavingsGoal(
            id = "g1",
            title = "MacBook Pro",
            targetAmountLkr = 300_000.0,
            currentSavedLkr = 100_000.0,
            // 200k remaining over 10 months -> 20k / month required
            targetDateMillis = now + 300 * DAY_MILLIS
        )

        // Saving only 12,000 / month -> behind by 8,000 / month -> 80,000 shortfall over 10 months
        val report = useCase(goal, actualMonthlySavingsRateLkr = 12_000.0, now = now)

        assertFalse(report.isPacingOnTrack)
        assertEquals(80_000.0, report.projectedShortfallLkr, 1000.0)
        assertTrue(report.projectedArrivalMillis!! > goal.targetDateMillis)
        assertTrue(report.behavioralInsight.contains("Behind pace", ignoreCase = true))
    }

    @Test
    fun `handles zero or negative savings gracefully without projected arrival`() {
        val now = 1700000000000L
        val goal = SavingsGoal(
            id = "g1",
            title = "Emergency Fund",
            targetAmountLkr = 100_000.0,
            currentSavedLkr = 10_000.0,
            targetDateMillis = now + 90 * DAY_MILLIS
        )

        val report = useCase(goal, actualMonthlySavingsRateLkr = 0.0, now = now)

        assertFalse(report.isPacingOnTrack)
        assertNull(report.projectedArrivalMillis)
        assertTrue(report.behavioralInsight.contains("spending everything", ignoreCase = true))
    }
}
