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
    val usdReferencePrice: Double? = null,
    val usdRateAtSetLkr: Double? = null,
    val usdRateSetAtMillis: Long? = null
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
        usdReferencePrice = usdReferencePrice,
        usdRateAtSetLkr = usdRateAtSetLkr,
        usdRateSetAtMillis = usdRateSetAtMillis,
        targetAtSetLkr = targetAmountLkr.takeIf { usdRateAtSetLkr != null }
    )

    companion object {
        /** Stores the target as entered, never the rate-adjusted one, so it is not scaled twice. */
        fun fromDomain(domain: SavingsGoal): GoalEntity = GoalEntity(
            id = domain.id,
            userId = domain.userId,
            title = domain.title,
            targetAmountLkr = domain.targetAtSetLkr ?: domain.targetAmountLkr,
            currentSavedLkr = domain.currentSavedLkr,
            targetDateMillis = domain.targetDateMillis,
            createdDateMillis = domain.createdDateMillis,
            notes = domain.notes,
            usdReferencePrice = domain.usdReferencePrice,
            usdRateAtSetLkr = domain.usdRateAtSetLkr,
            usdRateSetAtMillis = domain.usdRateSetAtMillis
        )
    }
}
