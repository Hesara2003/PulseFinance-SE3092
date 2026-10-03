package com.se3092.pulsefinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.se3092.pulsefinance.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM savings_goals WHERE userId = :userId LIMIT 1")
    fun getPrimaryGoal(userId: String): Flow<GoalEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)

    @Query("UPDATE savings_goals SET currentSavedLkr = :newAmount WHERE id = :goalId AND userId = :userId")
    suspend fun updateSavedAmount(goalId: String, userId: String, newAmount: Double)

    @Query("DELETE FROM savings_goals WHERE userId = :userId")
    suspend fun clearUserGoals(userId: String)
}
