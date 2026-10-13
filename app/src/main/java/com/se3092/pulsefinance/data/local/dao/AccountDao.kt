package com.se3092.pulsefinance.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.se3092.pulsefinance.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AccountDao {
    @Query("SELECT * FROM accounts WHERE userId = :userId ORDER BY name ASC")
    abstract fun getAccounts(userId: String): Flow<List<AccountEntity>>

    @Query("SELECT COUNT(*) FROM accounts WHERE userId = :userId")
    abstract suspend fun countAccounts(userId: String): Int

    @Upsert
    abstract suspend fun upsertAccount(account: AccountEntity)

    @Upsert
    abstract suspend fun upsertAccounts(accounts: List<AccountEntity>)

    @Query("UPDATE accounts SET balanceLkr = :balanceLkr WHERE id = :accountId AND userId = :userId")
    abstract suspend fun updateBalance(accountId: String, userId: String, balanceLkr: Double)

    @Query("DELETE FROM accounts WHERE userId = :userId")
    abstract suspend fun clearUserAccounts(userId: String)

    /** Atomically mirrors the remote snapshot so deleted remote rows don't linger locally. */
    @Transaction
    open suspend fun replaceUserAccounts(userId: String, accounts: List<AccountEntity>) {
        clearUserAccounts(userId)
        upsertAccounts(accounts)
    }
}
