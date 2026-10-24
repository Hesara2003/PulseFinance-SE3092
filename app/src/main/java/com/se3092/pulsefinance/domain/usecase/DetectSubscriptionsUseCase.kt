package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.Account
import com.se3092.pulsefinance.domain.model.AccountType
import com.se3092.pulsefinance.domain.model.DAY_MILLIS
import com.se3092.pulsefinance.domain.model.DetectedSubscription
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.SubscriptionDecision
import com.se3092.pulsefinance.domain.model.SubscriptionReview
import com.se3092.pulsefinance.domain.model.SubscriptionRisk
import com.se3092.pulsefinance.domain.model.SubscriptionStatus
import javax.inject.Inject

/**
 * Groups subscription/auto-debit expenses by merchant to surface what is still being charged.
 * Surfaces annual drain rate, usage risk, app inactivity, and secondary auto-debit accounts (FR-SUB-1...6).
 */
class DetectSubscriptionsUseCase @Inject constructor() {

    operator fun invoke(
        expenses: List<Expense>,
        reviews: Map<String, SubscriptionReview>,
        accounts: List<Account> = emptyList(),
        appUsageDaysProvider: ((key: String) -> Int?)? = null,
        now: Long = System.currentTimeMillis()
    ): List<DetectedSubscription> {
        val lookbackStart = now - LOOKBACK_DAYS * DAY_MILLIS
        val activeCutoff = now - ACTIVE_DAYS * DAY_MILLIS

        // Map accounts to check secondary auto-debit flag
        val secondaryAccountNames = accounts
            .filter { it.type == AccountType.BANK_SECONDARY || it.isAutoDebitSource }
            .map { it.name.trim().lowercase() }
            .toSet()

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

                val consecutiveUnused = review?.consecutiveUnusedMonths ?: 0
                val appDaysAgo = appUsageDaysProvider?.invoke(key)

                // Secondary account match check
                val isSecondary = latest.paymentAccountName.trim().lowercase().let { acct ->
                    secondaryAccountNames.any { acct.contains(it) || it.contains(acct) }
                }

                // Determine Subscription Risk (FR-SUB-4, FR-SUB-5)
                val risk = when {
                    status == SubscriptionStatus.STILL_CHARGING -> SubscriptionRisk.HIGH
                    consecutiveUnused >= 2 -> SubscriptionRisk.HIGH
                    appDaysAgo != null && appDaysAgo >= 30 -> SubscriptionRisk.HIGH
                    consecutiveUnused == 1 -> SubscriptionRisk.MEDIUM
                    appDaysAgo != null && appDaysAgo >= 14 -> SubscriptionRisk.MEDIUM
                    status == SubscriptionStatus.NEEDS_REVIEW -> SubscriptionRisk.MEDIUM
                    else -> SubscriptionRisk.LOW
                }

                DetectedSubscription(
                    key = key,
                    title = latest.title,
                    accountName = latest.paymentAccountName,
                    monthlyCostLkr = latest.amountLkr,
                    lastChargedMillis = latest.timestamp,
                    chargeCount = charges.size,
                    status = status,
                    risk = risk,
                    consecutiveUnusedMonths = consecutiveUnused,
                    lastAppOpenedDaysAgo = appDaysAgo,
                    isSecondaryAccount = isSecondary
                )
            }
            .sortedWith(
                compareByDescending<DetectedSubscription> { it.risk == SubscriptionRisk.HIGH }
                    .thenByDescending { it.isFlagged }
                    .thenByDescending { it.monthlyCostLkr }
            )
    }

    companion object {
        private const val LOOKBACK_DAYS = 120L
        private const val ACTIVE_DAYS = 45L

        /** Stable key for a merchant, e.g. "Netflix Premium " -> "netflix_premium". Safe as a Firestore map key. */
        fun keyFor(title: String): String =
            title.lowercase().replace(Regex("[^a-z0-9]+"), "_").trim('_')
    }
}
