package com.se3092.pulsefinance.domain.model

data class Expense(
    val id: String = "",
    val userId: String = "",
    val title: String,
    val amountLkr: Double,
    val category: ExpenseCategory,
    val isCommitted: Boolean = false, // true = Committed/Fixed, false = Discretionary
    val paymentAccountId: String = "",
    val paymentAccountName: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)
