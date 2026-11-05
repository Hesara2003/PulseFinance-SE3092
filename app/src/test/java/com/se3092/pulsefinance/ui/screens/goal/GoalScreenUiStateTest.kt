package com.se3092.pulsefinance.ui.screens.goal

import com.se3092.pulsefinance.domain.model.SavingsGoal
import org.junit.Assert.assertEquals
import org.junit.Test

class GoalScreenUiStateTest {

    private val day = 24L * 60 * 60 * 1000

    private fun stateWithPace() = GoalScreenUiState(
        goal = SavingsGoal(
            targetAmountLkr = 490000.0,
            currentSavedLkr = 11200.0,
            targetDateMillis = System.currentTimeMillis() + 360 * day + day / 2
        )
    )

    @Test
    fun `daysFunded matches the persona examples`() {
        val state = stateWithPace()

        assertEquals(0.64f, state.daysFunded(850.0), 0.01f)
        assertEquals(1.09f, state.daysFunded(1450.0), 0.01f)
        assertEquals(1.88f, state.daysFunded(2500.0), 0.01f)
    }

    @Test
    fun `daysFunded is zero without a goal`() {
        assertEquals(0f, GoalScreenUiState().daysFunded(2500.0), 0f)
    }

    @Test
    fun `daysFunded is zero for a fully funded goal`() {
        val funded = GoalScreenUiState(goal = SavingsGoal(currentSavedLkr = 490000.0))

        assertEquals(0f, funded.daysFunded(2500.0), 0f)
    }
}
