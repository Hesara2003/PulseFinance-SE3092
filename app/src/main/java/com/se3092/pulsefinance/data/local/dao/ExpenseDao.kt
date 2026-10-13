package com.se3092.pulsefinance.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.se3092.pulsefinance.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ExpenseDao {
    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY timestamp DESC")
    abstract fun getExpenses(userId: String): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE userId = :userId AND timestamp >= :fromMillis ORDER BY timestamp DESC")
    abstract fun getExpensesSince(userId: String, fromMillis: Long): Flow<List<ExpenseEntity>>

    @Upsert
    abstract suspend fun upsertExpense(expense: ExpenseEntity)

    @Upsert
    abstract suspend fun upsertExpenses(expenses: List<ExpenseEntity>)

    @Query("DELETE FROM expenses WHERE id = :expenseId AND userId = :userId")
    abstract suspend fun deleteExpense(expenseId: String, userId: String)

    @Query("DELETE FROM expenses WHERE userId = :userId")
    abstract suspend fun clearUserExpenses(userId: String)

    @Transaction
    open suspend fun replaceUserExpenses(userId: String, expenses: List<ExpenseEntity>) {
        clearUserExpenses(userId)
        upsertExpenses(expenses)
    }
}
