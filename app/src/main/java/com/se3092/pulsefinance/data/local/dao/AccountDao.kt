package com.se3092.pulsefinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.se3092.pulsefinance.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AccountDao {
    @Query("SELECT * FROM accounts WHERE userId = :userId")
    abstract fun getAccounts(userId: String): Flow<List<AccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAccount(account: AccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAccounts(accounts: List<AccountEntity>)

    @Query("DELETE FROM accounts WHERE id = :accountId AND userId = :userId")
    abstract suspend fun deleteAccount(accountId: String, userId: String)

    @Query("DELETE FROM accounts WHERE userId = :userId")
    abstract suspend fun clearUserAccounts(userId: String)

    /** Mirrors a Firestore snapshot so remote deletions also leave the cache. */
    @Transaction
    open suspend fun replaceAll(userId: String, accounts: List<AccountEntity>) {
        clearUserAccounts(userId)
        insertAccounts(accounts)
    }
}
