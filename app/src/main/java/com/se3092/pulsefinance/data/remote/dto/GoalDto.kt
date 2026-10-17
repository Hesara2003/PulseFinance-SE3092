package com.se3092.pulsefinance.data.remote.dto

import com.google.firebase.firestore.DocumentId
import com.se3092.pulsefinance.domain.model.SavingsGoal

data class GoalDto(
    @DocumentId var id: String = "",
    var userId: String = "",
    var title: String = "MacBook Pro M4",
    var targetAmountLkr: Double = 490000.0,
    var currentSavedLkr: Double = 11200.0,
    var targetDateMillis: Long = System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000),
    var createdDateMillis: Long = System.currentTimeMillis(),
    var notes: String = ""
) {
    fun toDomain(): SavingsGoal = SavingsGoal(
        id = id,
        userId = userId,
        title = title,
        targetAmountLkr = targetAmountLkr,
        currentSavedLkr = currentSavedLkr,
        targetDateMillis = targetDateMillis,
        createdDateMillis = createdDateMillis,
        notes = notes
    )

    companion object {
        fun fromDomain(domain: SavingsGoal): GoalDto = GoalDto(
            id = domain.id,
            userId = domain.userId,
            title = domain.title,
            targetAmountLkr = domain.targetAmountLkr,
            currentSavedLkr = domain.currentSavedLkr,
            targetDateMillis = domain.targetDateMillis,
            createdDateMillis = domain.createdDateMillis,
            notes = domain.notes
        )
    }
}
