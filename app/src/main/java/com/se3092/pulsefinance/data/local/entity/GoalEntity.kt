package com.se3092.pulsefinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.se3092.pulsefinance.domain.model.SavingsGoal

@Entity(tableName = "savings_goals")
data class GoalEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val targetAmountLkr: Double,
    val currentSavedLkr: Double,
    val targetDateMillis: Long,
    val createdDateMillis: Long,
    val notes: String,
    val usdReferencePrice: Double? = null
) {
    fun toDomain(): SavingsGoal = SavingsGoal(
        id = id,
        userId = userId,
        title = title,
        targetAmountLkr = targetAmountLkr,
        currentSavedLkr = currentSavedLkr,
        targetDateMillis = targetDateMillis,
        createdDateMillis = createdDateMillis,
        notes = notes,
        usdReferencePrice = usdReferencePrice
    )

    companion object {
        fun fromDomain(domain: SavingsGoal): GoalEntity = GoalEntity(
            id = domain.id,
            userId = domain.userId,
            title = domain.title,
            targetAmountLkr = domain.targetAmountLkr,
            currentSavedLkr = domain.currentSavedLkr,
            targetDateMillis = domain.targetDateMillis,
            createdDateMillis = domain.createdDateMillis,
            notes = domain.notes,
            usdReferencePrice = domain.usdReferencePrice
        )
    }
}
