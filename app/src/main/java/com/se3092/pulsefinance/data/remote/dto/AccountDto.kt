package com.se3092.pulsefinance.data.remote.dto

import com.google.firebase.firestore.DocumentId
import com.se3092.pulsefinance.domain.model.Account
import com.se3092.pulsefinance.domain.model.AccountType

data class AccountDto(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val type: String = "BANK_PRIMARY",
    val balanceLkr: Double = 0.0,
    val isAutoDebitSource: Boolean = false
) {
    fun toDomain(): Account = Account(
        id = id,
        userId = userId,
        name = name,
        type = try { AccountType.valueOf(type) } catch (e: Exception) { AccountType.BANK_PRIMARY },
        balanceLkr = balanceLkr,
        isAutoDebitSource = isAutoDebitSource
    )

    companion object {
        fun fromDomain(domain: Account): AccountDto = AccountDto(
            id = domain.id,
            userId = domain.userId,
            name = domain.name,
            type = domain.type.name,
            balanceLkr = domain.balanceLkr,
            isAutoDebitSource = domain.isAutoDebitSource
        )
    }
}
