package com.se3092.pulsefinance.domain.model

enum class SubscriptionDecision {
    KEEP,
    CANCELLED
}

enum class SubscriptionRisk {
    LOW,
    MEDIUM,
    HIGH
}

/** The user's verdict and usage history on one detected subscription, keyed by its normalised title. */
data class SubscriptionReview(
    val key: String,
    val decision: SubscriptionDecision,
    val decidedAt: Long,
    val consecutiveUnusedMonths: Int = 0
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
    val status: SubscriptionStatus,
    val risk: SubscriptionRisk = SubscriptionRisk.LOW,
    val consecutiveUnusedMonths: Int = 0,
    val lastAppOpenedDaysAgo: Int? = null,
    /** True when the subscription maps to an installed-app lookup (Netflix, Spotify...). */
    val isAppLinked: Boolean = false,
    val isSecondaryAccount: Boolean = false
) {
    val annualCostLkr: Double get() = monthlyCostLkr * 12
    val isFlagged: Boolean get() = status == SubscriptionStatus.NEEDS_REVIEW ||
            status == SubscriptionStatus.STILL_CHARGING ||
            risk == SubscriptionRisk.HIGH
}
