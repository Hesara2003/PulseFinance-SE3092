package com.se3092.pulsefinance.data.repository

import com.se3092.pulsefinance.data.local.dao.AccountDao
import com.se3092.pulsefinance.data.local.entity.AccountEntity
import com.se3092.pulsefinance.data.remote.dto.AccountDto
import com.se3092.pulsefinance.data.remote.firebase.FirestoreDataSource
import com.se3092.pulsefinance.domain.model.Account
import com.se3092.pulsefinance.domain.model.AccountType
import com.se3092.pulsefinance.domain.repository.AccountRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao,
    private val firestoreDataSource: FirestoreDataSource
) : AccountRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    override fun getAccounts(userId: String): Flow<List<Account>> {
        repositoryScope.launch { syncAccounts(userId) }
        return accountDao.getAccounts(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveAccount(account: Account): Result<Unit> {
        return try {
            val accountId = if (account.id.isNotBlank()) account.id else UUID.randomUUID().toString()
            val prepared = account.copy(id = accountId)

            accountDao.insertAccount(AccountEntity.fromDomain(prepared))
            firestoreDataSource.saveAccount(account.userId, AccountDto.fromDomain(prepared))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }

    override suspend fun syncAccounts(userId: String) {
        repositoryScope.launch {
            try {
                firestoreDataSource.observeAccounts(userId).collect { dtos ->
                    if (dtos.isEmpty()) {
                        initializeDefaultAccountsForKavindu(userId)
                    } else {
                        val entities = dtos.map { AccountEntity.fromDomain(it.toDomain()) }
                        accountDao.insertAccounts(entities)
                    }
                }
            } catch (_: Exception) {}
        }
    }

    override suspend fun initializeDefaultAccountsForKavindu(userId: String): Result<Unit> {
        val defaultAccounts = listOf(
            Account(
                id = "acc_primary_bank",
                userId = userId,
                name = "Commercial Bank (Salary & Card)",
                type = AccountType.BANK_PRIMARY,
                balanceLkr = 8700.0,
                isAutoDebitSource = false
            ),
            Account(
                id = "acc_secondary_bank",
                userId = userId,
                name = "Secondary Bank (Gym & Subscriptions)",
                type = AccountType.BANK_SECONDARY,
                balanceLkr = 2500.0,
                isAutoDebitSource = true
            ),
            Account(
                id = "acc_cash_wallet",
                userId = userId,
                name = "Cash in Hand",
                type = AccountType.CASH,
                balanceLkr = 3200.0,
                isAutoDebitSource = false
            ),
            Account(
                id = "acc_binance_wallet",
                userId = userId,
                name = "Binance (USDT / ETH)",
                type = AccountType.CRYPTO_EXCHANGE,
                balanceLkr = 45000.0,
                isAutoDebitSource = false
            )
        )
        defaultAccounts.forEach { saveAccount(it) }
        return Result.success(Unit)
    }
}
