package com.se3092.pulsefinance.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** FR-AUTH-6: Kavindu's emergency buffer, sized from his fixed costs. */
class EmergencyRunwayTest {

    private val rent = FixedCommitment("Rent", 34_000.0, ExpenseCategory.RENT)
    private val broadband = FixedCommitment("Fiber Broadband", 4_800.0, ExpenseCategory.UTILITIES)

    private fun profile(months: Int?, saved: Double = 0.0, commitments: List<FixedCommitment> = listOf(rent, broadband)) =
        OnboardingProfile(
            monthlySalaryLkr = 126_400.0,
            incomeSources = emptyMap(),
            fixedCommitments = commitments,
            completedAt = 0L,
            runwayMonths = months,
            runwaySavedLkr = saved
        )

    @Test
    fun `three months of rent and broadband is LKR 116,400`() {
        val runway = EmergencyRunway.of(profile(months = 3), committedThisMonthLkr = 0.0)!!

        assertEquals(38_800.0, runway.monthlyCommittedLkr, 0.01)
        assertEquals(116_400.0, runway.targetLkr, 0.01)
    }

    @Test
    fun `progress and months covered follow what he has set aside`() {
        val runway = EmergencyRunway.of(profile(months = 3, saved = 58_200.0), 0.0)!!

        assertEquals(0.5f, runway.progress, 0.001f)
        assertEquals(1.5, runway.monthsCovered, 0.001)
        assertEquals(58_200.0, runway.remainingLkr, 0.01)
        assertFalse(runway.isFunded)
    }

    @Test
    fun `a fully funded buffer stops at 100 percent and has nothing remaining`() {
        val runway = EmergencyRunway.of(profile(months = 2, saved = 200_000.0), 0.0)!!

        assertTrue(runway.isFunded)
        assertEquals(1f, runway.progress, 0f)
        assertEquals(0.0, runway.remainingLkr, 0.0)
    }

    @Test
    fun `committed spending logged this month raises the size when it exceeds his fixed costs`() {
        // He also pays a LKR 8,500 gym that he didn't list in setup.
        val runway = EmergencyRunway.of(profile(months = 3), committedThisMonthLkr = 47_300.0)!!

        assertEquals(47_300.0, runway.monthlyCommittedLkr, 0.01)
        assertEquals(141_900.0, runway.targetLkr, 0.01)
    }

    @Test
    fun `his fixed costs are the floor early in the month`() {
        val runway = EmergencyRunway.of(profile(months = 3), committedThisMonthLkr = 12_000.0)!!

        assertEquals(38_800.0, runway.monthlyCommittedLkr, 0.01)
    }

    @Test
    fun `there is no runway until he chooses months or has committed costs to size it from`() {
        assertNull(EmergencyRunway.of(null, 0.0))
        assertNull(EmergencyRunway.of(profile(months = null), 0.0))
        assertNull(EmergencyRunway.of(profile(months = 3, commitments = emptyList()), committedThisMonthLkr = 0.0))
        assertNotNull(EmergencyRunway.of(profile(months = 3, commitments = emptyList()), committedThisMonthLkr = 20_000.0))
    }

    @Test
    fun `months are kept between one and six`() {
        assertEquals(1, EmergencyRunway.clampMonths(0))
        assertEquals(4, EmergencyRunway.clampMonths(4))
        assertEquals(6, EmergencyRunway.clampMonths(12))
    }
}
