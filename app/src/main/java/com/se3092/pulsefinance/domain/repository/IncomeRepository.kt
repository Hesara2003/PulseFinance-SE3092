package com.se3092.pulsefinance.domain.repository

import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import kotlinx.coroutines.flow.Flow

interface IncomeRepository {
    fun getIncomeStreams(userId: String): Flow<List<IncomeStream>>
    /** Inserts, or replaces when [IncomeStream.id] already exists. */
    suspend fun saveIncomeStream(income: IncomeStream): Result<Unit>
    suspend fun updateInvoiceStatus(incomeId: String, userId: String, status: InvoiceStatus, dateMillis: Long): Result<Unit>
    suspend fun deleteIncomeStream(incomeId: String, userId: String): Result<Unit>
}
