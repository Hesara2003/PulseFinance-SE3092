package com.se3092.pulsefinance.domain.repository

import com.se3092.pulsefinance.domain.model.SavingsGoal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    /** Emits null until the user has created (or been seeded with) a goal. */
    fun getPrimaryGoal(userId: String): Flow<SavingsGoal?>
    suspend fun saveGoal(goal: SavingsGoal): Result<Unit>
    suspend fun updateSavedAmount(goalId: String, userId: String, newAmountLkr: Double): Result<Unit>
}
