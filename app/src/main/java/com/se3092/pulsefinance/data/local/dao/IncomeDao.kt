package com.se3092.pulsefinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.se3092.pulsefinance.data.local.entity.IncomeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {
    @Query("SELECT * FROM income_streams WHERE userId = :userId ORDER BY dateReceivedOrExpected DESC")
    fun getIncomeStreams(userId: String): Flow<List<IncomeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncome(income: IncomeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncomeStreams(incomes: List<IncomeEntity>)

    @Query("UPDATE income_streams SET status = :status WHERE id = :incomeId AND userId = :userId")
    suspend fun updateStatus(incomeId: String, userId: String, status: String)

    @Query("DELETE FROM income_streams WHERE id = :incomeId AND userId = :userId")
    suspend fun deleteIncome(incomeId: String, userId: String)

    @Query("DELETE FROM income_streams WHERE userId = :userId")
    suspend fun clearUserIncome(userId: String)
}
