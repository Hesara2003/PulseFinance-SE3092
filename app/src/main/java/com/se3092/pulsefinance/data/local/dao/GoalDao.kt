package com.se3092.pulsefinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.se3092.pulsefinance.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class GoalDao {
    @Query("SELECT * FROM savings_goals WHERE userId = :userId ORDER BY createdDateMillis ASC LIMIT 1")
    abstract fun getPrimaryGoal(userId: String): Flow<GoalEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertGoal(goal: GoalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertGoals(goals: List<GoalEntity>)

    @Query("UPDATE savings_goals SET currentSavedLkr = :newAmount WHERE id = :goalId AND userId = :userId")
    abstract suspend fun updateSavedAmount(goalId: String, userId: String, newAmount: Double)

    @Query("DELETE FROM savings_goals WHERE userId = :userId")
    abstract suspend fun clearUserGoals(userId: String)

    /** Mirrors a Firestore snapshot so remote deletions also leave the cache. */
    @Transaction
    open suspend fun replaceAll(userId: String, goals: List<GoalEntity>) {
        clearUserGoals(userId)
        insertGoals(goals)
    }
}
