package com.se3092.pulsefinance.data.repository

import com.se3092.pulsefinance.data.local.dao.ExpenseDao
import com.se3092.pulsefinance.data.local.entity.ExpenseEntity
import com.se3092.pulsefinance.data.remote.dto.ExpenseDto
import com.se3092.pulsefinance.data.remote.firebase.FirestoreDataSource
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.repository.ExpenseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val firestoreDataSource: FirestoreDataSource
) : ExpenseRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    override fun getExpenses(userId: String): Flow<List<Expense>> {
        // Automatically trigger background Firestore synchronization
        repositoryScope.launch { syncExpenses(userId) }
        return expenseDao.getExpenses(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addExpense(expense: Expense): Result<Unit> {
        return try {
            val expenseId = if (expense.id.isNotBlank()) expense.id else UUID.randomUUID().toString()
            val preparedExpense = expense.copy(id = expenseId)

            // Local-first write: immediate UI responsiveness
            expenseDao.insertExpense(ExpenseEntity.fromDomain(preparedExpense))

            // Remote write to Firestore
            firestoreDataSource.saveExpense(expense.userId, ExpenseDto.fromDomain(preparedExpense))
            Result.success(Unit)
        } catch (e: Exception) {
            // Even if remote fails (e.g. offline), local cache has the record!
            Result.success(Unit)
        }
    }

    override suspend fun deleteExpense(expenseId: String, userId: String): Result<Unit> {
        return try {
            expenseDao.deleteExpense(expenseId, userId)
            firestoreDataSource.deleteExpense(userId, expenseId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncExpenses(userId: String) {
        repositoryScope.launch {
            try {
                firestoreDataSource.observeExpenses(userId).collect { dtos ->
                    val entities = dtos.map { ExpenseEntity.fromDomain(it.toDomain()) }
                    expenseDao.insertExpenses(entities)
                }
            } catch (_: Exception) {
                // Ignore network sync issues, offline cache will serve data
            }
        }
    }
}
