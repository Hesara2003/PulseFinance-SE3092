package com.se3092.pulsefinance.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.se3092.pulsefinance.data.local.dao.AccountDao
import com.se3092.pulsefinance.data.local.dao.ExpenseDao
import com.se3092.pulsefinance.data.local.dao.GoalDao
import com.se3092.pulsefinance.data.local.dao.IncomeDao
import com.se3092.pulsefinance.data.local.entity.AccountEntity
import com.se3092.pulsefinance.data.local.entity.ExpenseEntity
import com.se3092.pulsefinance.data.local.entity.GoalEntity
import com.se3092.pulsefinance.data.local.entity.IncomeEntity

@Database(
    entities = [
        ExpenseEntity::class,
        IncomeEntity::class,
        GoalEntity::class,
        AccountEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun incomeDao(): IncomeDao
    abstract fun goalDao(): GoalDao
    abstract fun accountDao(): AccountDao
}
