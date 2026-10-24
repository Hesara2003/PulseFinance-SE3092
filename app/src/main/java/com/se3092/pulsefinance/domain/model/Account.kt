package com.se3092.pulsefinance.domain.model

data class Account(
    val id: String = "",
    val userId: String = "",
    val name: String,
    val type: AccountType,
    val balanceLkr: Double = 0.0,
    val isAutoDebitSource: Boolean = false, // e.g. Secondary account for gym & subscriptions
    val accountNumber: String = "",
    val isFrozen: Boolean = false,
    val frozenUntilMillis: Long? = null,
    val freezeBreakCount: Int = 0
) {
    val maskedAccountNumber: String
        get() = if (accountNumber.length >= 4) {
            "•••• " + accountNumber.takeLast(4)
        } else if (accountNumber.isNotBlank()) {
            "•••• $accountNumber"
        } else {
            ""
        }
}
