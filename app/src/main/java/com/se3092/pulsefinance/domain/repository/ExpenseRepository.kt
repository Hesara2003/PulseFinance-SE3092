package com.se3092.pulsefinance.domain.repository

import com.se3092.pulsefinance.domain.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getExpenses(userId: String): Flow<List<Expense>>
    /** Inserts, or replaces when [Expense.id] already exists. */
    suspend fun saveExpense(expense: Expense): Result<Unit>
    suspend fun deleteExpense(expenseId: String, userId: String): Result<Unit>
}
