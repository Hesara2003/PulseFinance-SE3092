package com.se3092.pulsefinance.di

import android.content.Context
import androidx.room.Room
import com.se3092.pulsefinance.data.local.AppDatabase
import com.se3092.pulsefinance.data.local.dao.AccountDao
import com.se3092.pulsefinance.data.local.dao.ExpenseDao
import com.se3092.pulsefinance.data.local.dao.GoalDao
import com.se3092.pulsefinance.data.local.dao.IncomeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "pulse_finance_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideExpenseDao(database: AppDatabase): ExpenseDao = database.expenseDao()

    @Provides
    fun provideIncomeDao(database: AppDatabase): IncomeDao = database.incomeDao()

    @Provides
    fun provideGoalDao(database: AppDatabase): GoalDao = database.goalDao()

    @Provides
    fun provideAccountDao(database: AppDatabase): AccountDao = database.accountDao()
}
