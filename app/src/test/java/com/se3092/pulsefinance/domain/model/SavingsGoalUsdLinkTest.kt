package com.se3092.pulsefinance.domain.model

import com.se3092.pulsefinance.data.local.entity.GoalEntity
import com.se3092.pulsefinance.data.remote.dto.GoalDto
import com.se3092.pulsefinance.ui.components.usdTargetNote
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class SavingsGoalUsdLinkTest {

    /** MacBook Pro M4 at LKR 490,000 from the reseller, set when USD→LKR was 300. */
    private val linked = SavingsGoal(
        title = "MacBook Pro M4",
        targetAmountLkr = 490_000.0,
        currentSavedLkr = 11_200.0,
        usdReferencePrice = 1599.0,
        usdRateAtSetLkr = 300.0,
        usdRateSetAtMillis = 0L,
        targetAtSetLkr = 490_000.0
    )

    @Test
    fun `target follows the rate and keeps the reseller markup`() {
        val repriced = linked.atUsdRate(306.0)

        assertEquals(499_800.0, repriced.targetAmountLkr, 0.01) // 490,000 x 306 / 300
        assertEquals(9_800.0, repriced.usdTargetChangeLkr!!, 0.01)
    }

    @Test
    fun `saved amount never changes with the rate`() {
        assertEquals(11_200.0, linked.atUsdRate(250.0).currentSavedLkr, 0.0)
    }

    @Test
    fun `goal without a stored rate is not re-priced`() {
        val unlinked = linked.copy(usdRateAtSetLkr = null, targetAtSetLkr = null)

        assertFalse(unlinked.isUsdLinked)
        assertSame(unlinked, unlinked.atUsdRate(306.0))
        assertNull(unlinked.usdTargetChangeLkr)
    }

    @Test
    fun `persisting a re-priced goal stores the entered target, not the re-priced one`() {
        val repriced = linked.atUsdRate(306.0)

        val viaDto = GoalDto.fromDomain(repriced).toDomain().atUsdRate(306.0)
        val viaEntity = GoalEntity.fromDomain(repriced).toDomain().atUsdRate(306.0)

        assertEquals(490_000.0, GoalDto.fromDomain(repriced).targetAmountLkr, 0.01)
        assertEquals(499_800.0, viaDto.targetAmountLkr, 0.01)
        assertEquals(499_800.0, viaEntity.targetAmountLkr, 0.01)
        assertTrue(viaDto.isUsdLinked)
    }

    @Test
    fun `goal card describes the target change`() {
        assertTrue(usdTargetNote(linked.atUsdRate(306.0)).startsWith("Target + LKR 9,800 since"))
        assertTrue(usdTargetNote(linked.atUsdRate(300.0)).startsWith("Target follows the daily USD rate"))
        assertEquals(
            "Reseller LKR price floats with daily FX rate",
            usdTargetNote(linked.copy(usdRateAtSetLkr = null, targetAtSetLkr = null))
        )
    }
}
