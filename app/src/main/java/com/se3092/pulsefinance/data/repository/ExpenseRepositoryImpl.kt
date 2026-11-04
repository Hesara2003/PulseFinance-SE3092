package com.se3092.pulsefinance.data.repository

import com.se3092.pulsefinance.data.local.dao.ExpenseDao
import com.se3092.pulsefinance.data.local.entity.ExpenseEntity
import com.se3092.pulsefinance.data.remote.dto.ExpenseDto
import com.se3092.pulsefinance.data.remote.firebase.FirestoreDataSource
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val firestoreDataSource: FirestoreDataSource
) : ExpenseRepository {

    override fun getExpenses(userId: String): Flow<List<Expense>> = cacheBackedFlow(
        tag = TAG,
        local = expenseDao.getExpenses(userId).map { entities -> entities.map { it.toDomain() } },
        sync = {
            firestoreDataSource.observeExpenses(userId).collect { dtos ->
                expenseDao.replaceAll(userId, dtos.map { ExpenseEntity.fromDomain(it.toDomain()) })
            }
        }
    )

    override suspend fun saveExpense(expense: Expense): Result<Unit> = runCatching {
        val prepared = expense.copy(id = expense.id.ifBlank { UUID.randomUUID().toString() })
        // Local-first: the record is visible immediately, Firestore queues the upload if offline.
        expenseDao.insertExpense(ExpenseEntity.fromDomain(prepared))
        firestoreDataSource.saveExpense(prepared.userId, ExpenseDto.fromDomain(prepared))
    }

    override suspend fun deleteExpense(expenseId: String, userId: String): Result<Unit> = runCatching {
        expenseDao.deleteExpense(expenseId, userId)
        firestoreDataSource.deleteExpense(userId, expenseId)
    }

    private companion object {
        const val TAG = "ExpenseRepository"
    }
}
