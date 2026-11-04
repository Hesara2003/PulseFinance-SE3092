package com.se3092.pulsefinance.domain.model

data class SavingsGoal(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val targetAmountLkr: Double = 0.0,
    val currentSavedLkr: Double = 0.0,
    val targetDateMillis: Long = System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000), // 12 months
    val createdDateMillis: Long = System.currentTimeMillis(),
    val notes: String = "",
    val usdReferencePrice: Double? = null
) {
    val isComplete: Boolean get() = targetAmountLkr > 0 && currentSavedLkr >= targetAmountLkr

    val remainingAmountLkr: Double get() = (targetAmountLkr - currentSavedLkr).coerceAtLeast(0.0)
    val progressPercentage: Float get() = if (targetAmountLkr > 0) ((currentSavedLkr / targetAmountLkr).toFloat()).coerceIn(0f, 1f) else 0f
    
    fun daysRemaining(now: Long = System.currentTimeMillis()): Long {
        val diff = targetDateMillis - now
        return (diff / (24 * 60 * 60 * 1000)).coerceAtLeast(1)
    }

    val daysRemaining: Long get() = daysRemaining()

    fun requiredMonthlySavingsLkr(now: Long = System.currentTimeMillis()): Double {
        val months = (daysRemaining(now) / 30.0).coerceAtLeast(1.0)
        return remainingAmountLkr / months
    }

    val requiredMonthlySavingsLkr: Double get() = requiredMonthlySavingsLkr()

    fun requiredDailySavingsLkr(now: Long = System.currentTimeMillis()): Double {
        return remainingAmountLkr / daysRemaining(now).coerceAtLeast(1)
    }

    val requiredDailySavingsLkr: Double get() = requiredDailySavingsLkr()
}
