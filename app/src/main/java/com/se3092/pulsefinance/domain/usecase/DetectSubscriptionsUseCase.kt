package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.Account
import com.se3092.pulsefinance.domain.model.DetectedSubscription
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.SubscriptionReview
import com.se3092.pulsefinance.domain.model.SubscriptionReviewStatus
import com.se3092.pulsefinance.domain.model.SubscriptionRisk
import javax.inject.Inject
import kotlin.math.abs

/**
 * Scans the expense ledger for recurring debits.
 *
 * A group of expenses sharing a normalised title is treated as a subscription when either
 * - it is explicitly categorised as [ExpenseCategory.SUBSCRIPTION], or
 * - it has at least two charges on a weekly, monthly or yearly cadence with stable amounts.
 *
 * Debits that land on an auto-debit source account (the "invisible" secondary card) are
 * flagged as zombie risks unless the user has already reviewed them.
 */
class DetectSubscriptionsUseCase @Inject constructor() {

    operator fun invoke(
        expenses: List<Expense>,
        accounts: List<Account>,
        reviews: Map<String, SubscriptionReview> = emptyMap()
    ): List<DetectedSubscription> {
        val autoDebitAccountIds = accounts.filter { it.isAutoDebitSource }.map { it.id }.toSet()

        return expenses
            .groupBy { subscriptionKeyFor(it.title) }
            .filterKeys { it.isNotBlank() }
            .mapNotNull { (key, charges) -> detect(key, charges, autoDebitAccountIds, reviews[key]) }
            .sortedByDescending { it.monthlyCostLkr }
    }

    private fun detect(
        key: String,
        charges: List<Expense>,
        autoDebitAccountIds: Set<String>,
        review: SubscriptionReview?
    ): DetectedSubscription? {
        val sorted = charges.sortedBy { it.timestamp }
        val isTaggedSubscription = sorted.any { it.category == ExpenseCategory.SUBSCRIPTION }

        val intervalsDays = sorted.zipWithNext { a, b -> ((b.timestamp - a.timestamp) / DAY_MILLIS).toInt() }
        val cadenceDays = intervalsDays.medianOrNull()
        val hasRecurringCadence = cadenceDays != null && RECURRING_CADENCES.any { cadenceDays in it }

        val amounts = sorted.map { it.amountLkr }
        val medianAmount = amounts.median()
        val hasStableAmounts = medianAmount > 0 &&
            amounts.all { abs(it - medianAmount) / medianAmount <= AMOUNT_TOLERANCE }

        val isRecurring = sorted.size >= 2 && hasRecurringCadence && hasStableAmounts
        if (!isTaggedSubscription && !isRecurring) return null

        val latest = sorted.last()
        val cadence = if (isRecurring) cadenceDays!! else DEFAULT_CADENCE_DAYS
        val isOnAutoDebit = latest.paymentAccountId in autoDebitAccountIds

        val risk = when {
            review?.status == SubscriptionReviewStatus.KEEP -> SubscriptionRisk.HEALTHY
            isOnAutoDebit -> SubscriptionRisk.ZOMBIE
            isTaggedSubscription -> SubscriptionRisk.WATCH
            else -> SubscriptionRisk.HEALTHY
        }

        return DetectedSubscription(
            key = key,
            title = latest.title,
            averageAmountLkr = amounts.average(),
            occurrences = sorted.size,
            cadenceDays = cadence,
            lastChargedMillis = latest.timestamp,
            nextExpectedMillis = latest.timestamp + cadence * DAY_MILLIS,
            paymentAccountName = latest.paymentAccountName,
            isOnAutoDebitAccount = isOnAutoDebit,
            risk = risk,
            reviewStatus = review?.status
        )
    }

    companion object {
        private const val DAY_MILLIS = 24L * 60 * 60 * 1000
        private const val DEFAULT_CADENCE_DAYS = 30
        private const val AMOUNT_TOLERANCE = 0.2
        private val RECURRING_CADENCES = listOf(6..8, 26..35, 355..375)

        /** Stable, Firestore-safe document id derived from a merchant title. */
        fun subscriptionKeyFor(title: String): String =
            title.lowercase()
                .replace(Regex("[^a-z ]"), " ")
                .trim()
                .split(Regex("\\s+"))
                .filter { it.isNotBlank() }
                .joinToString("_")

        private fun List<Int>.medianOrNull(): Int? =
            if (isEmpty()) null else sorted()[size / 2]

        private fun List<Double>.median(): Double =
            if (isEmpty()) 0.0 else sorted()[size / 2]
    }
}
