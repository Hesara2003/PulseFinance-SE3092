package com.se3092.pulsefinance.domain.repository

import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import kotlinx.coroutines.flow.Flow

interface IncomeRepository {
    fun getIncomeStreams(userId: String): Flow<List<IncomeStream>>
    suspend fun addIncomeStream(income: IncomeStream): Result<Unit>
    suspend fun updateInvoiceStatus(incomeId: String, userId: String, status: InvoiceStatus): Result<Unit>
    suspend fun deleteIncomeStream(incomeId: String, userId: String): Result<Unit>
    suspend fun syncIncome(userId: String)
}
