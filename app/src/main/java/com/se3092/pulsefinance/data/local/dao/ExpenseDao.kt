package com.se3092.pulsefinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.se3092.pulsefinance.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY timestamp DESC")
    fun getExpenses(userId: String): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenses(expenses: List<ExpenseEntity>)

    @Query("DELETE FROM expenses WHERE id = :expenseId AND userId = :userId")
    suspend fun deleteExpense(expenseId: String, userId: String)

    @Query("DELETE FROM expenses WHERE userId = :userId")
    suspend fun clearUserExpenses(userId: String)
}
