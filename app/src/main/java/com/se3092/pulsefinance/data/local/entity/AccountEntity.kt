package com.se3092.pulsefinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.se3092.pulsefinance.domain.model.Account
import com.se3092.pulsefinance.domain.model.AccountType

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val type: String,
    val balanceLkr: Double,
    val isAutoDebitSource: Boolean,
    val accountNumber: String = "",
    val isFrozen: Boolean = false,
    val frozenUntilMillis: Long? = null,
    val freezeBreakCount: Int = 0
) {
    fun toDomain(): Account = Account(
        id = id,
        userId = userId,
        name = name,
        type = try { AccountType.valueOf(type) } catch (e: Exception) { AccountType.BANK_PRIMARY },
        balanceLkr = balanceLkr,
        isAutoDebitSource = isAutoDebitSource,
        accountNumber = accountNumber,
        isFrozen = isFrozen,
        frozenUntilMillis = frozenUntilMillis,
        freezeBreakCount = freezeBreakCount
    )

    companion object {
        fun fromDomain(domain: Account): AccountEntity = AccountEntity(
            id = domain.id,
            userId = domain.userId,
            name = domain.name,
            type = domain.type.name,
            balanceLkr = domain.balanceLkr,
            isAutoDebitSource = domain.isAutoDebitSource,
            accountNumber = domain.accountNumber,
            isFrozen = domain.isFrozen,
            frozenUntilMillis = domain.frozenUntilMillis,
            freezeBreakCount = domain.freezeBreakCount
        )
    }
}
