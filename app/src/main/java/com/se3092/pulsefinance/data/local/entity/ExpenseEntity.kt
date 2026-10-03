package com.se3092.pulsefinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val amountLkr: Double,
    val category: String,
    val isCommitted: Boolean,
    val paymentAccountId: String,
    val paymentAccountName: String,
    val timestamp: Long,
    val notes: String
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
        fun fromDomain(domain: Expense): ExpenseEntity = ExpenseEntity(
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
