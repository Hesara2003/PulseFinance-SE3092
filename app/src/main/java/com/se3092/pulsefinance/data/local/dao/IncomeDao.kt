package com.se3092.pulsefinance.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.se3092.pulsefinance.data.local.entity.IncomeEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class IncomeDao {
    @Query("SELECT * FROM income_streams WHERE userId = :userId ORDER BY dateReceivedOrExpected DESC")
    abstract fun getIncomeStreams(userId: String): Flow<List<IncomeEntity>>

    @Upsert
    abstract suspend fun upsertIncome(income: IncomeEntity)

    @Upsert
    abstract suspend fun upsertIncomeStreams(incomes: List<IncomeEntity>)

    @Query("UPDATE income_streams SET status = :status WHERE id = :incomeId AND userId = :userId")
    abstract suspend fun updateStatus(incomeId: String, userId: String, status: String)

    @Query("DELETE FROM income_streams WHERE id = :incomeId AND userId = :userId")
    abstract suspend fun deleteIncome(incomeId: String, userId: String)

    @Query("DELETE FROM income_streams WHERE userId = :userId")
    abstract suspend fun clearUserIncome(userId: String)

    @Transaction
    open suspend fun replaceUserIncome(userId: String, incomes: List<IncomeEntity>) {
        clearUserIncome(userId)
        upsertIncomeStreams(incomes)
    }
}
