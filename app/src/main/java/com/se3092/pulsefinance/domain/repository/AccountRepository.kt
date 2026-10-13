package com.se3092.pulsefinance.domain.repository

import com.se3092.pulsefinance.domain.model.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    /** Room-backed stream that stays in sync with Firestore while collected. */
    fun getAccounts(userId: String): Flow<List<Account>>
    suspend fun saveAccount(account: Account): Result<Unit>
    suspend fun updateBalance(accountId: String, userId: String, newBalanceLkr: Double): Result<Unit>

    /** Creates a single zero-balance main account for brand-new users that have none. */
    suspend fun ensureDefaultAccount(userId: String): Result<Unit>
    suspend fun initializeDefaultAccountsForKavindu(userId: String): Result<Unit>
}
