package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.DAY_MILLIS
import com.se3092.pulsefinance.domain.model.DetectedSubscription
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.SubscriptionDecision
import com.se3092.pulsefinance.domain.model.SubscriptionReview
import com.se3092.pulsefinance.domain.model.SubscriptionStatus
import javax.inject.Inject

/**
 * Groups subscription/auto-debit expenses by merchant to surface what is still being charged.
 * Kavindu's two forgotten subscriptions were invisible because nothing ever totalled them;
 * this turns them into an annual figure he has to look at and explicitly keep or cancel.
 */
class DetectSubscriptionsUseCase @Inject constructor() {

    operator fun invoke(
        expenses: List<Expense>,
        reviews: Map<String, SubscriptionReview>,
        now: Long = System.currentTimeMillis()
    ): List<DetectedSubscription> {
        val lookbackStart = now - LOOKBACK_DAYS * DAY_MILLIS
        val activeCutoff = now - ACTIVE_DAYS * DAY_MILLIS

        return expenses
            .filter { it.category == ExpenseCategory.SUBSCRIPTION && it.timestamp >= lookbackStart }
            .groupBy { keyFor(it.title) }
            .filterKeys { it.isNotBlank() }
            .mapNotNull { (key, charges) ->
                val latest = charges.maxBy { it.timestamp }
                val review = reviews[key]
                val status = when {
                    review?.decision == SubscriptionDecision.KEEP -> SubscriptionStatus.KEPT
                    review?.decision == SubscriptionDecision.CANCELLED && latest.timestamp > review.decidedAt -> SubscriptionStatus.STILL_CHARGING
                    review?.decision == SubscriptionDecision.CANCELLED -> SubscriptionStatus.CANCELLED
                    else -> SubscriptionStatus.NEEDS_REVIEW
                }
                // Something not charged for ~6 weeks and never reviewed has probably lapsed already.
                if (status == SubscriptionStatus.NEEDS_REVIEW && latest.timestamp < activeCutoff) return@mapNotNull null

                DetectedSubscription(
                    key = key,
                    title = latest.title,
                    accountName = latest.paymentAccountName,
                    monthlyCostLkr = latest.amountLkr,
                    lastChargedMillis = latest.timestamp,
                    chargeCount = charges.size,
                    status = status
                )
            }
            .sortedWith(compareByDescending<DetectedSubscription> { it.isFlagged }.thenByDescending { it.monthlyCostLkr })
    }

    companion object {
        private const val LOOKBACK_DAYS = 120L
        private const val ACTIVE_DAYS = 45L

        /** Stable key for a merchant, e.g. "Netflix Premium " -> "netflix_premium". Safe as a Firestore map key. */
        fun keyFor(title: String): String =
            title.lowercase().replace(Regex("[^a-z0-9]+"), "_").trim('_')
    }
}
