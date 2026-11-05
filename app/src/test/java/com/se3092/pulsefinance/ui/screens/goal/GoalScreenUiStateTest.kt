package com.se3092.pulsefinance.ui.screens.goal

import com.se3092.pulsefinance.domain.model.GoalQuest
import com.se3092.pulsefinance.domain.model.SavingsGoal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** The goal screen derives its level map from the goal; this pins what the state exposes. */
class GoalScreenUiStateTest {

    private val day = 24L * 60 * 60 * 1000

    private fun macBook(saved: Double) = SavingsGoal(
        title = "MacBook Pro M4",
        targetAmountLkr = 490000.0,
        currentSavedLkr = saved,
        targetDateMillis = System.currentTimeMillis() + 360 * day + day / 2
    )

    @Test
    fun `quest is derived from the persona goal`() {
        val quest = GoalScreenUiState(goal = macBook(saved = 11200.0)).quest

        assertNotNull(quest)
        assertEquals(GoalQuest.LEVEL_COUNT, quest!!.levelCount)
        assertEquals("MacBook Pro M4", quest.levels.last().name)
        // LKR 11,200 is below the first 49,000 level, so nothing is cleared yet
        assertEquals(0, quest.clearedLevels)
        assertEquals(1, quest.currentLevel?.number)
        assertEquals(37800.0, quest.toClearCurrentLkr, 0.01)
        assertFalse(quest.isComplete)
    }

    @Test
    fun `quest is absent without a goal`() {
        assertNull(GoalScreenUiState().quest)
    }

    @Test
    fun `fully funded goal completes the quest`() {
        val quest = GoalScreenUiState(goal = macBook(saved = 490000.0)).quest

        assertTrue(quest!!.isComplete)
        assertEquals(GoalQuest.LEVEL_COUNT, quest.clearedLevels)
        assertEquals(0.0, quest.toClearCurrentLkr, 0.0)
    }
}
