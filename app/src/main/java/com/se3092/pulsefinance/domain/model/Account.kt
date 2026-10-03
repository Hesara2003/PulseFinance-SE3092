package com.se3092.pulsefinance.domain.model

data class Account(
    val id: String = "",
    val userId: String = "",
    val name: String,
    val type: AccountType,
    val balanceLkr: Double,
    val isAutoDebitSource: Boolean = false // e.g. Secondary account for gym & subscriptions
)
