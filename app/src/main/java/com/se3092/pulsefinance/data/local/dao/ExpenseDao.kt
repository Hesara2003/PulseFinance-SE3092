package com.se3092.pulsefinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.se3092.pulsefinance.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ExpenseDao {
    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY timestamp DESC")
    abstract fun getExpenses(userId: String): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertExpense(expense: ExpenseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertExpenses(expenses: List<ExpenseEntity>)

    @Query("DELETE FROM expenses WHERE id = :expenseId AND userId = :userId")
    abstract suspend fun deleteExpense(expenseId: String, userId: String)

    @Query("DELETE FROM expenses WHERE userId = :userId")
    abstract suspend fun clearUserExpenses(userId: String)

    /** Mirrors a Firestore snapshot so remote deletions also leave the cache. */
    @Transaction
    open suspend fun replaceAll(userId: String, expenses: List<ExpenseEntity>) {
        clearUserExpenses(userId)
        insertExpenses(expenses)
    }
}
