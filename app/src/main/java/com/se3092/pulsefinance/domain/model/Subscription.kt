package com.se3092.pulsefinance.domain.model

enum class SubscriptionDecision {
    KEEP,
    CANCELLED
}

/** The user's verdict on one detected subscription, keyed by its normalised title. */
data class SubscriptionReview(
    val key: String,
    val decision: SubscriptionDecision,
    val decidedAt: Long
)

enum class SubscriptionStatus {
    /** Charged recently and never reviewed — the "zombie" candidate. */
    NEEDS_REVIEW,
    /** User marked it cancelled, yet a new charge was logged afterwards. */
    STILL_CHARGING,
    /** User chose to keep it. */
    KEPT,
    /** User cancelled it and no charge has appeared since. */
    CANCELLED
}

/** A recurring auto-debit inferred from the user's own subscription-category expenses. */
data class DetectedSubscription(
    val key: String,
    val title: String,
    val accountName: String,
    val monthlyCostLkr: Double,
    val lastChargedMillis: Long,
    val chargeCount: Int,
    val status: SubscriptionStatus
) {
    val annualCostLkr: Double get() = monthlyCostLkr * 12
    val isFlagged: Boolean get() = status == SubscriptionStatus.NEEDS_REVIEW || status == SubscriptionStatus.STILL_CHARGING
}
