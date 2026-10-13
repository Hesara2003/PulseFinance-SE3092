package com.se3092.pulsefinance.data.remote.dto

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory

data class ExpenseDto(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val amountLkr: Double = 0.0,
    val category: String = "OTHER",
    @get:PropertyName("isCommitted") val isCommitted: Boolean = false,
    val paymentAccountId: String = "",
    val paymentAccountName: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
) {
    fun toDomain(): Expense = Expense(
        id = id,
        userId = userId,
        title = title,
        amountLkr = amountLkr,
        category = try { ExpenseCategory.valueOf(category) } catch (e: Exception) { ExpenseCategory.OTHER },
        isCommitted = isCommitted,
        paymentAccountId = paymentAccountId,
        paymentAccountName = paymentAccountName,
        timestamp = timestamp,
        notes = notes
    )

    companion object {
        fun fromDomain(domain: Expense): ExpenseDto = ExpenseDto(
            id = domain.id,
            userId = domain.userId,
            title = domain.title,
            amountLkr = domain.amountLkr,
            category = domain.category.name,
            isCommitted = domain.isCommitted,
            paymentAccountId = domain.paymentAccountId,
            paymentAccountName = domain.paymentAccountName,
            timestamp = domain.timestamp,
            notes = domain.notes
        )
    }
}
