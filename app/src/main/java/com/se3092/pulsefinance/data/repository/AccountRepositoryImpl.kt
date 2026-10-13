package com.se3092.pulsefinance.data.repository

import com.se3092.pulsefinance.data.local.dao.AccountDao
import com.se3092.pulsefinance.data.local.entity.AccountEntity
import com.se3092.pulsefinance.data.remote.dto.AccountDto
import com.se3092.pulsefinance.data.remote.firebase.FirestoreDataSource
import com.se3092.pulsefinance.domain.model.Account
import com.se3092.pulsefinance.domain.model.AccountType
import com.se3092.pulsefinance.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao,
    private val firestoreDataSource: FirestoreDataSource,
    private val remoteWriter: RemoteWriter
) : AccountRepository {

    override fun getAccounts(userId: String): Flow<List<Account>> = cacheBackedFlow(
        label = "accounts",
        local = accountDao.getAccounts(userId),
        remote = firestoreDataSource.observeAccounts(userId),
        persist = { dtos ->
            accountDao.replaceUserAccounts(userId, dtos.map { AccountEntity.fromDomain(it.toDomain()) })
        }
    ).map { entities -> entities.map { it.toDomain() } }

    override suspend fun saveAccount(account: Account): Result<Unit> = runCatching {
        val prepared = account.copy(id = account.id.ifBlank { UUID.randomUUID().toString() })
        accountDao.upsertAccount(AccountEntity.fromDomain(prepared))
        remoteWriter.enqueue("saveAccount") {
            firestoreDataSource.saveAccount(prepared.userId, AccountDto.fromDomain(prepared))
        }
    }

    override suspend fun updateBalance(
        accountId: String,
        userId: String,
        newBalanceLkr: Double
    ): Result<Unit> = runCatching {
        accountDao.updateBalance(accountId, userId, newBalanceLkr)
        remoteWriter.enqueue("updateAccountBalance") {
            firestoreDataSource.updateAccountBalance(userId, accountId, newBalanceLkr)
        }
    }

    override suspend fun ensureDefaultAccount(userId: String): Result<Unit> = runCatching {
        if (accountDao.countAccounts(userId) > 0) return@runCatching
        // Room is empty on a fresh install even for returning users, so ask Firestore too.
        val hasRemoteAccounts = try {
            firestoreDataSource.hasAccounts(userId)
        } catch (_: Exception) {
            false
        }
        if (hasRemoteAccounts) return@runCatching
        saveAccount(
            Account(
                id = DEFAULT_ACCOUNT_ID,
                userId = userId,
                name = "Main Account",
                type = AccountType.BANK_PRIMARY,
                balanceLkr = 0.0
            )
        ).getOrThrow()
    }

    override suspend fun initializeDefaultAccountsForKavindu(userId: String): Result<Unit> = runCatching {
        listOf(
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
        ).forEach { saveAccount(it).getOrThrow() }
    }

    companion object {
        const val DEFAULT_ACCOUNT_ID = "acc_main"
    }
}
