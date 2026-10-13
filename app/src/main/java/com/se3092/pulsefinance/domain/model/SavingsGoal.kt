package com.se3092.pulsefinance.domain.model

data class SavingsGoal(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val targetAmountLkr: Double = 0.0,
    val currentSavedLkr: Double = 0.0,
    val targetDateMillis: Long = System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000), // 12 months
    val createdDateMillis: Long = System.currentTimeMillis(),
    val notes: String = ""
) {
    val isComplete: Boolean get() = targetAmountLkr > 0 && currentSavedLkr >= targetAmountLkr

    val remainingAmountLkr: Double get() = (targetAmountLkr - currentSavedLkr).coerceAtLeast(0.0)
    val progressPercentage: Float get() = if (targetAmountLkr > 0) ((currentSavedLkr / targetAmountLkr).toFloat()).coerceIn(0f, 1f) else 0f
    
    val daysRemaining: Long get() {
        val diff = targetDateMillis - System.currentTimeMillis()
        return (diff / (24 * 60 * 60 * 1000)).coerceAtLeast(1)
    }

    val requiredMonthlySavingsLkr: Double get() {
        val months = (daysRemaining / 30.0).coerceAtLeast(1.0)
        return remainingAmountLkr / months
    }

    val requiredDailySavingsLkr: Double get() {
        return remainingAmountLkr / daysRemaining.coerceAtLeast(1)
    }
}
