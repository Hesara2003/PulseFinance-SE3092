package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.Account
import com.se3092.pulsefinance.domain.model.AccountType
import com.se3092.pulsefinance.domain.model.DAY_MILLIS
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.SubscriptionDecision
import com.se3092.pulsefinance.domain.model.SubscriptionReview
import com.se3092.pulsefinance.domain.model.SubscriptionRisk
import com.se3092.pulsefinance.domain.model.SubscriptionStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DetectSubscriptionsUseCaseTest {

    private val useCase = DetectSubscriptionsUseCase()

    @Test
    fun `detects subscriptions and flags high risk for 2 consecutive unused months`() {
        val now = 1700000000000L
        val expenses = listOf(
            Expense(
                id = "e1",
                title = "Netflix Premium",
                amountLkr = 2200.0,
                category = ExpenseCategory.SUBSCRIPTION,
                paymentAccountName = "HNB Primary",
                timestamp = now - 10 * DAY_MILLIS
            ),
            Expense(
                id = "e2",
                title = "Netflix Premium",
                amountLkr = 2200.0,
                category = ExpenseCategory.SUBSCRIPTION,
                paymentAccountName = "HNB Primary",
                timestamp = now - 40 * DAY_MILLIS
            )
        )

        val reviews = mapOf(
            "netflix_premium" to SubscriptionReview(
                key = "netflix_premium",
                decision = SubscriptionDecision.KEEP,
                decidedAt = now - 60 * DAY_MILLIS,
                consecutiveUnusedMonths = 2
            )
        )

        val subs = useCase(expenses, reviews, emptyList(), null, now)

        assertEquals(1, subs.size)
        val sub = subs.first()
        assertEquals("Netflix Premium", sub.title)
        assertEquals(SubscriptionRisk.HIGH, sub.risk)
        assertEquals(2, sub.consecutiveUnusedMonths)
        assertTrue(sub.isFlagged)
    }

    @Test
    fun `detects secondary account auto debit per FR-SUB-6`() {
        val now = 1700000000000L
        val accounts = listOf(
            Account(id = "acc1", name = "Commercial Bank Primary", type = AccountType.BANK_PRIMARY),
            Account(id = "acc2", name = "Sampath Web Card", type = AccountType.BANK_SECONDARY, isAutoDebitSource = true)
        )

        val expenses = listOf(
            Expense(
                id = "e1",
                title = "Spotify Duo",
                amountLkr = 1100.0,
                category = ExpenseCategory.SUBSCRIPTION,
                paymentAccountName = "Sampath Web Card",
                timestamp = now - 5 * DAY_MILLIS
            )
        )

        val subs = useCase(expenses, emptyMap(), accounts, null, now)

        assertEquals(1, subs.size)
        val sub = subs.first()
        assertTrue(sub.isSecondaryAccount)
        assertEquals("Sampath Web Card", sub.accountName)
    }

    @Test
    fun `marks still charging when debited after cancellation`() {
        val now = 1700000000000L
        val expenses = listOf(
            Expense(
                id = "e1",
                title = "Gym Membership",
                amountLkr = 8000.0,
                category = ExpenseCategory.SUBSCRIPTION,
                paymentAccountName = "Primary Bank",
                timestamp = now - 2 * DAY_MILLIS
            )
        )

        // User marked cancelled 10 days ago, but charged 2 days ago!
        val reviews = mapOf(
            "gym_membership" to SubscriptionReview(
                key = "gym_membership",
                decision = SubscriptionDecision.CANCELLED,
                decidedAt = now - 10 * DAY_MILLIS
            )
        )

        val subs = useCase(expenses, reviews, emptyList(), null, now)

        assertEquals(1, subs.size)
        val sub = subs.first()
        assertEquals(SubscriptionStatus.STILL_CHARGING, sub.status)
        assertEquals(SubscriptionRisk.HIGH, sub.risk)
        assertTrue(sub.isFlagged)
    }
}
