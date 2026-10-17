package com.se3092.pulsefinance.domain.model

enum class SubscriptionReviewStatus(val displayName: String) {
    KEEP("Kept Active"),
    CANCELLED("Cancelled & Blocked"),
    DISMISSED("Not a Subscription")
}

enum class SubscriptionRisk(val displayName: String) {
    ZOMBIE("Zombie Leak"),
    WATCH("Review Soon"),
    HEALTHY("High Utility")
}

/** A user's decision about a detected recurring debit, persisted across sessions. */
data class SubscriptionReview(
    val subscriptionKey: String,
    val userId: String,
    val status: SubscriptionReviewStatus,
    val reviewedAtMillis: Long = System.currentTimeMillis()
)

/** A recurring debit inferred from the expense history by DetectSubscriptionsUseCase. */
data class DetectedSubscription(
    val key: String,
    val title: String,
    val averageAmountLkr: Double,
    val occurrences: Int,
    val cadenceDays: Int,
    val lastChargedMillis: Long,
    val nextExpectedMillis: Long,
    val paymentAccountName: String,
    val isOnAutoDebitAccount: Boolean,
    val risk: SubscriptionRisk,
    val reviewStatus: SubscriptionReviewStatus? = null
) {
    val monthlyCostLkr: Double get() = averageAmountLkr * (30.0 / cadenceDays.coerceAtLeast(1))
    val annualCostLkr: Double get() = monthlyCostLkr * 12
    val isPendingReview: Boolean get() = reviewStatus == null
    val isFlagged: Boolean get() = risk == SubscriptionRisk.ZOMBIE && isPendingReview
}
