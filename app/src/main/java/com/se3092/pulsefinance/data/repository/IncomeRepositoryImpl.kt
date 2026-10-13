package com.se3092.pulsefinance.data.repository

import com.se3092.pulsefinance.data.local.dao.IncomeDao
import com.se3092.pulsefinance.data.local.entity.IncomeEntity
import com.se3092.pulsefinance.data.remote.dto.IncomeDto
import com.se3092.pulsefinance.data.remote.firebase.FirestoreDataSource
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import com.se3092.pulsefinance.domain.repository.IncomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IncomeRepositoryImpl @Inject constructor(
    private val incomeDao: IncomeDao,
    private val firestoreDataSource: FirestoreDataSource
) : IncomeRepository {

    override fun getIncomeStreams(userId: String): Flow<List<IncomeStream>> = cacheBackedFlow(
        tag = TAG,
        local = incomeDao.getIncomeStreams(userId).map { entities -> entities.map { it.toDomain() } },
        sync = {
            firestoreDataSource.observeIncomeStreams(userId).collect { dtos ->
                incomeDao.replaceAll(userId, dtos.map { IncomeEntity.fromDomain(it.toDomain()) })
            }
        }
    )

    override suspend fun saveIncomeStream(income: IncomeStream): Result<Unit> = runCatching {
        val prepared = income.copy(id = income.id.ifBlank { UUID.randomUUID().toString() })
        incomeDao.insertIncome(IncomeEntity.fromDomain(prepared))
        firestoreDataSource.saveIncomeStream(prepared.userId, IncomeDto.fromDomain(prepared))
    }

    override suspend fun updateInvoiceStatus(
        incomeId: String,
        userId: String,
        status: InvoiceStatus,
        dateMillis: Long
    ): Result<Unit> = runCatching {
        incomeDao.updateStatus(incomeId, userId, status.name, dateMillis)
        firestoreDataSource.updateIncomeStatus(userId, incomeId, status.name, dateMillis)
    }

    override suspend fun deleteIncomeStream(incomeId: String, userId: String): Result<Unit> = runCatching {
        incomeDao.deleteIncome(incomeId, userId)
        firestoreDataSource.deleteIncomeStream(userId, incomeId)
    }

    private companion object {
        const val TAG = "IncomeRepository"
    }
}
