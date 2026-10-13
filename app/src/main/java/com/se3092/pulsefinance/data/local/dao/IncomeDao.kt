package com.se3092.pulsefinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.se3092.pulsefinance.data.local.entity.IncomeEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class IncomeDao {
    @Query("SELECT * FROM income_streams WHERE userId = :userId ORDER BY dateReceivedOrExpected DESC")
    abstract fun getIncomeStreams(userId: String): Flow<List<IncomeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertIncome(income: IncomeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertIncomeStreams(incomes: List<IncomeEntity>)

    @Query("UPDATE income_streams SET status = :status, dateReceivedOrExpected = :dateMillis WHERE id = :incomeId AND userId = :userId")
    abstract suspend fun updateStatus(incomeId: String, userId: String, status: String, dateMillis: Long)

    @Query("DELETE FROM income_streams WHERE id = :incomeId AND userId = :userId")
    abstract suspend fun deleteIncome(incomeId: String, userId: String)

    @Query("DELETE FROM income_streams WHERE userId = :userId")
    abstract suspend fun clearUserIncome(userId: String)

    /** Mirrors a Firestore snapshot so remote deletions also leave the cache. */
    @Transaction
    open suspend fun replaceAll(userId: String, incomes: List<IncomeEntity>) {
        clearUserIncome(userId)
        insertIncomeStreams(incomes)
    }
}
