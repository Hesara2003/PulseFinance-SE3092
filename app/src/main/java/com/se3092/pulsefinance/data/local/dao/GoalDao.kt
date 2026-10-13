package com.se3092.pulsefinance.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.se3092.pulsefinance.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class GoalDao {
    @Query("SELECT * FROM savings_goals WHERE userId = :userId ORDER BY createdDateMillis ASC LIMIT 1")
    abstract fun getPrimaryGoal(userId: String): Flow<GoalEntity?>

    @Upsert
    abstract suspend fun upsertGoal(goal: GoalEntity)

    @Upsert
    abstract suspend fun upsertGoals(goals: List<GoalEntity>)

    @Query("UPDATE savings_goals SET currentSavedLkr = :newAmount WHERE id = :goalId AND userId = :userId")
    abstract suspend fun updateSavedAmount(goalId: String, userId: String, newAmount: Double)

    @Query("DELETE FROM savings_goals WHERE userId = :userId")
    abstract suspend fun clearUserGoals(userId: String)

    @Transaction
    open suspend fun replaceUserGoals(userId: String, goals: List<GoalEntity>) {
        clearUserGoals(userId)
        upsertGoals(goals)
    }
}
