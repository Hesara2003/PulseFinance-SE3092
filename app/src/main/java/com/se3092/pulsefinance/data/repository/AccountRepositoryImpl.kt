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
    private val firestoreDataSource: FirestoreDataSource
) : AccountRepository {

    override fun getAccounts(userId: String): Flow<List<Account>> = cacheBackedFlow(
        tag = TAG,
        local = accountDao.getAccounts(userId).map { entities -> entities.map { it.toDomain() } },
        sync = {
            firestoreDataSource.observeAccounts(userId).collect { dtos ->
                accountDao.replaceAll(userId, dtos.map { AccountEntity.fromDomain(it.toDomain()) })
            }
        }
    )

    override suspend fun saveAccount(account: Account): Result<Unit> = runCatching {
        val prepared = account.copy(id = account.id.ifBlank { UUID.randomUUID().toString() })
        accountDao.insertAccount(AccountEntity.fromDomain(prepared))
        firestoreDataSource.saveAccount(prepared.userId, AccountDto.fromDomain(prepared))
    }

    override suspend fun freezeAccount(account: Account, untilMillis: Long): Result<Unit> {
        val updated = account.copy(
            isFrozen = true,
            frozenUntilMillis = untilMillis
        )
        return saveAccount(updated)
    }

    override suspend fun unfreezeAccount(account: Account): Result<Unit> {
        val updated = account.copy(
            isFrozen = false,
            frozenUntilMillis = null
        )
        return saveAccount(updated)
    }

    override suspend fun recordFreezeBreak(account: Account): Result<Unit> {
        val updated = account.copy(
            freezeBreakCount = account.freezeBreakCount + 1
        )
        return saveAccount(updated)
    }

    /**
     * Gives a brand-new user one payment channel per place money leaves from — the scenario's
     * card, auto-debit account, cash and exchange wallet — so expenses can be attributed from the
     * first entry. Channels only; balances start at zero and nothing else is pre-filled.
     * Requires a server round-trip so an empty offline cache is never mistaken for a new user.
     */
    override suspend fun ensureDefaultAccounts(userId: String): Result<Unit> = runCatching {
        if (firestoreDataSource.hasAnyAccountOnServer(userId)) return@runCatching
        defaultChannels.forEach { (id, type) ->
            saveAccount(
                Account(
                    id = id,
                    userId = userId,
                    name = type.displayName,
                    type = type,
                    balanceLkr = 0.0,
                    isAutoDebitSource = type == AccountType.BANK_SECONDARY,
                    accountNumber = when (type) {
                        AccountType.BANK_PRIMARY -> "8001452178"
                        AccountType.BANK_SECONDARY -> "1002938472"
                        else -> ""
                    }
                )
            ).getOrThrow()
        }
    }

    private companion object {
        const val TAG = "AccountRepository"
        val defaultChannels = listOf(
            "acc_primary_bank" to AccountType.BANK_PRIMARY,
            "acc_secondary_bank" to AccountType.BANK_SECONDARY,
            "acc_cash" to AccountType.CASH,
            "acc_crypto_exchange" to AccountType.CRYPTO_EXCHANGE
        )
    }
}
