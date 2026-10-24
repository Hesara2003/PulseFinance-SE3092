package com.se3092.pulsefinance.domain.repository

import com.se3092.pulsefinance.domain.model.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAccounts(userId: String): Flow<List<Account>>
    suspend fun saveAccount(account: Account): Result<Unit>
    suspend fun ensureDefaultAccounts(userId: String): Result<Unit>
    suspend fun freezeAccount(account: Account, untilMillis: Long): Result<Unit>
    suspend fun unfreezeAccount(account: Account): Result<Unit>
    suspend fun recordFreezeBreak(account: Account): Result<Unit>
}
