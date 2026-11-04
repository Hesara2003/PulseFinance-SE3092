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
    val usdReferencePrice: Double? = null,
    /** USD→LKR rate when the target was last set; null when the target is not USD-linked. */
    val usdRateAtSetLkr: Double? = null,
    val usdRateSetAtMillis: Long? = null,
    /** The LKR target as he entered it, before following the rate. Persisted as the stored target. */
    val targetAtSetLkr: Double? = null
) {
    val isUsdLinked: Boolean get() = usdReferencePrice != null && usdRateAtSetLkr != null && targetAtSetLkr != null

    /**
     * FR-GOAL-6: an imported item's LKR price tracks the dollar, so the target he entered
     * moves with the USD→LKR rate (keeping the reseller's markup). Saved amounts never change.
     */
    fun atUsdRate(usdLkrRate: Double): SavingsGoal {
        val rateAtSet = usdRateAtSetLkr ?: return this
        val base = targetAtSetLkr ?: return this
        if (!isUsdLinked || rateAtSet <= 0 || usdLkrRate <= 0) return this
        return copy(targetAmountLkr = base * usdLkrRate / rateAtSet)
    }

    /** How far the LKR target has moved since it was set, or null when not USD-linked. */
    val usdTargetChangeLkr: Double? get() = if (isUsdLinked) targetAmountLkr - targetAtSetLkr!! else null

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
