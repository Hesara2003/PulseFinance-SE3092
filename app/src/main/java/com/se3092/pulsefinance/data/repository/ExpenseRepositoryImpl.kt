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
    private val firestoreDataSource: FirestoreDataSource,
    private val remoteWriter: RemoteWriter
) : ExpenseRepository {

    override fun getExpenses(userId: String): Flow<List<Expense>> = cacheBackedFlow(
        label = "expenses",
        local = expenseDao.getExpenses(userId),
        remote = firestoreDataSource.observeExpenses(userId),
        persist = { dtos ->
            expenseDao.replaceUserExpenses(userId, dtos.map { ExpenseEntity.fromDomain(it.toDomain()) })
        }
    ).map { entities -> entities.map { it.toDomain() } }

    override suspend fun addExpense(expense: Expense): Result<Unit> = runCatching {
        val prepared = expense.copy(id = expense.id.ifBlank { UUID.randomUUID().toString() })
        // Local-first write: the Room flow re-emits immediately
        expenseDao.upsertExpense(ExpenseEntity.fromDomain(prepared))
        remoteWriter.enqueue("saveExpense") {
            firestoreDataSource.saveExpense(prepared.userId, ExpenseDto.fromDomain(prepared))
        }
    }

    override suspend fun deleteExpense(expenseId: String, userId: String): Result<Unit> = runCatching {
        expenseDao.deleteExpense(expenseId, userId)
        remoteWriter.enqueue("deleteExpense") { firestoreDataSource.deleteExpense(userId, expenseId) }
    }
}
