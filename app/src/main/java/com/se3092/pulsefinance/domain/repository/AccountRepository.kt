package com.se3092.pulsefinance.domain.repository

import com.se3092.pulsefinance.domain.model.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAccounts(userId: String): Flow<List<Account>>
    suspend fun saveAccount(account: Account): Result<Unit>
    suspend fun syncAccounts(userId: String)
    suspend fun initializeDefaultAccountsForKavindu(userId: String): Result<Unit>
}
