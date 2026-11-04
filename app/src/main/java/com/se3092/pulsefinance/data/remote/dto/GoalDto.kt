package com.se3092.pulsefinance.data.remote.dto

import com.google.firebase.firestore.DocumentId
import com.se3092.pulsefinance.domain.model.SavingsGoal

data class GoalDto(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val targetAmountLkr: Double = 0.0,
    val currentSavedLkr: Double = 0.0,
    val targetDateMillis: Long = System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000),
    val createdDateMillis: Long = System.currentTimeMillis(),
    val notes: String = "",
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
        fun fromDomain(domain: SavingsGoal): GoalDto = GoalDto(
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
