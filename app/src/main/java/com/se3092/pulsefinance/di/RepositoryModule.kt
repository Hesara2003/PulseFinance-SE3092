package com.se3092.pulsefinance.di

import com.se3092.pulsefinance.data.repository.AccountRepositoryImpl
import com.se3092.pulsefinance.data.repository.AuthRepositoryImpl
import com.se3092.pulsefinance.data.repository.ExpenseRepositoryImpl
import com.se3092.pulsefinance.data.repository.GoalRepositoryImpl
import com.se3092.pulsefinance.data.repository.IncomeRepositoryImpl
import com.se3092.pulsefinance.data.repository.SubscriptionReviewRepositoryImpl
import com.se3092.pulsefinance.data.repository.TaxConfigRepositoryImpl
import com.se3092.pulsefinance.domain.repository.AccountRepository
import com.se3092.pulsefinance.domain.repository.AuthRepository
import com.se3092.pulsefinance.domain.repository.ExpenseRepository
import com.se3092.pulsefinance.domain.repository.GoalRepository
import com.se3092.pulsefinance.domain.repository.IncomeRepository
import com.se3092.pulsefinance.domain.repository.SubscriptionReviewRepository
import com.se3092.pulsefinance.domain.repository.TaxConfigRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindExpenseRepository(impl: ExpenseRepositoryImpl): ExpenseRepository

    @Binds
    @Singleton
    abstract fun bindIncomeRepository(impl: IncomeRepositoryImpl): IncomeRepository

    @Binds
    @Singleton
    abstract fun bindGoalRepository(impl: GoalRepositoryImpl): GoalRepository

    @Binds
    @Singleton
    abstract fun bindAccountRepository(impl: AccountRepositoryImpl): AccountRepository

    @Binds
    @Singleton
    abstract fun bindSubscriptionReviewRepository(impl: SubscriptionReviewRepositoryImpl): SubscriptionReviewRepository

    @Binds
    @Singleton
    abstract fun bindTaxConfigRepository(impl: TaxConfigRepositoryImpl): TaxConfigRepository
}
