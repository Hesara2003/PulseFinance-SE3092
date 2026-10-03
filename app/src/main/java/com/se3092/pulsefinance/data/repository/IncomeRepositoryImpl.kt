package com.se3092.pulsefinance.data.repository

import com.se3092.pulsefinance.data.local.dao.IncomeDao
import com.se3092.pulsefinance.data.local.entity.IncomeEntity
import com.se3092.pulsefinance.data.remote.dto.IncomeDto
import com.se3092.pulsefinance.data.remote.firebase.FirestoreDataSource
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import com.se3092.pulsefinance.domain.repository.IncomeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IncomeRepositoryImpl @Inject constructor(
    private val incomeDao: IncomeDao,
    private val firestoreDataSource: FirestoreDataSource
) : IncomeRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    override fun getIncomeStreams(userId: String): Flow<List<IncomeStream>> {
        repositoryScope.launch { syncIncome(userId) }
        return incomeDao.getIncomeStreams(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addIncomeStream(income: IncomeStream): Result<Unit> {
        return try {
            val incomeId = if (income.id.isNotBlank()) income.id else UUID.randomUUID().toString()
            val preparedIncome = income.copy(id = incomeId)

            incomeDao.insertIncome(IncomeEntity.fromDomain(preparedIncome))
            firestoreDataSource.saveIncomeStream(income.userId, IncomeDto.fromDomain(preparedIncome))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit) // Offline fallback
        }
    }

    override suspend fun updateInvoiceStatus(
        incomeId: String,
        userId: String,
        status: InvoiceStatus
    ): Result<Unit> {
        return try {
            incomeDao.updateStatus(incomeId, userId, status.name)
            firestoreDataSource.updateIncomeStatus(userId, incomeId, status.name)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteIncomeStream(incomeId: String, userId: String): Result<Unit> {
        return try {
            incomeDao.deleteIncome(incomeId, userId)
            firestoreDataSource.deleteIncomeStream(userId, incomeId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncIncome(userId: String) {
        repositoryScope.launch {
            try {
                firestoreDataSource.observeIncomeStreams(userId).collect { dtos ->
                    val entities = dtos.map { IncomeEntity.fromDomain(it.toDomain()) }
                    incomeDao.insertIncomeStreams(entities)
                }
            } catch (_: Exception) {}
        }
    }
}
