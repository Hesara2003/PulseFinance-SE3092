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
    private val firestoreDataSource: FirestoreDataSource,
    private val remoteWriter: RemoteWriter
) : IncomeRepository {

    override fun getIncomeStreams(userId: String): Flow<List<IncomeStream>> = cacheBackedFlow(
        label = "income_streams",
        local = incomeDao.getIncomeStreams(userId),
        remote = firestoreDataSource.observeIncomeStreams(userId),
        persist = { dtos ->
            incomeDao.replaceUserIncome(userId, dtos.map { IncomeEntity.fromDomain(it.toDomain()) })
        }
    ).map { entities -> entities.map { it.toDomain() } }

    override suspend fun addIncomeStream(income: IncomeStream): Result<Unit> = runCatching {
        val prepared = income.copy(id = income.id.ifBlank { UUID.randomUUID().toString() })
        incomeDao.upsertIncome(IncomeEntity.fromDomain(prepared))
        remoteWriter.enqueue("saveIncomeStream") {
            firestoreDataSource.saveIncomeStream(prepared.userId, IncomeDto.fromDomain(prepared))
        }
    }

    override suspend fun updateInvoiceStatus(
        incomeId: String,
        userId: String,
        status: InvoiceStatus
    ): Result<Unit> = runCatching {
        incomeDao.updateStatus(incomeId, userId, status.name)
        remoteWriter.enqueue("updateIncomeStatus") {
            firestoreDataSource.updateIncomeStatus(userId, incomeId, status.name)
        }
    }

    override suspend fun deleteIncomeStream(incomeId: String, userId: String): Result<Unit> = runCatching {
        incomeDao.deleteIncome(incomeId, userId)
        remoteWriter.enqueue("deleteIncomeStream") { firestoreDataSource.deleteIncomeStream(userId, incomeId) }
    }
}
