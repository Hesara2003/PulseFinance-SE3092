package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.Account
import com.se3092.pulsefinance.domain.model.AccountType
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.model.SubscriptionReview
import com.se3092.pulsefinance.domain.model.SubscriptionReviewStatus
import com.se3092.pulsefinance.domain.model.SubscriptionRisk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DetectSubscriptionsUseCaseTest {

    private val detect = DetectSubscriptionsUseCase()
    private val day = 24L * 60 * 60 * 1000
    private val now = 1_790_000_000_000L

    private val accounts = listOf(
        Account(id = "primary", name = "Commercial Bank", type = AccountType.BANK_PRIMARY, balanceLkr = 0.0),
        Account(
            id = "secondary", name = "Secondary Bank", type = AccountType.BANK_SECONDARY,
            balanceLkr = 0.0, isAutoDebitSource = true
        )
    )

    private fun expense(
        title: String,
        amount: Double,
        daysAgo: Long,
        category: ExpenseCategory = ExpenseCategory.OTHER,
        accountId: String = "primary"
    ) = Expense(
        id = "$title-$daysAgo",
        title = title,
        amountLkr = amount,
        category = category,
        isCommitted = false,
        paymentAccountId = accountId,
        paymentAccountName = accountId,
        timestamp = now - daysAgo * day
    )

    @Test
    fun `monthly charges on the auto-debit account are flagged as zombie`() {
        val gym = (0..2).map { expense("Fitness First Gym", 8500.0, it * 30L, accountId = "secondary") }

        val result = detect(gym, accounts)

        assertEquals(1, result.size)
        val sub = result.single()
        assertEquals(SubscriptionRisk.ZOMBIE, sub.risk)
        assertEquals(30, sub.cadenceDays)
        assertEquals(3, sub.occurrences)
        assertEquals(8500.0, sub.monthlyCostLkr, 0.01)
        assertTrue(sub.isFlagged)
    }

    @Test
    fun `irregular one-off purchases are not subscriptions`() {
        val coffee = listOf(
            expense("Coffee", 850.0, 0),
            expense("Coffee", 1200.0, 3),
            expense("Coffee", 650.0, 11)
        )

        assertTrue(detect(coffee, accounts).isEmpty())
    }

    @Test
    fun `explicit subscription category is detected even with one charge`() {
        val result = detect(listOf(expense("Claude Pro", 6100.0, 2, ExpenseCategory.SUBSCRIPTION)), accounts)

        assertEquals(1, result.size)
        assertEquals(SubscriptionRisk.WATCH, result.single().risk)
    }

    @Test
    fun `kept subscriptions are no longer flagged`() {
        val gym = (0..1).map { expense("Fitness First Gym", 8500.0, it * 30L, accountId = "secondary") }
        val key = DetectSubscriptionsUseCase.subscriptionKeyFor("Fitness First Gym")
        val reviews = mapOf(key to SubscriptionReview(key, "u", SubscriptionReviewStatus.KEEP))

        val sub = detect(gym, accounts, reviews).single()

        assertEquals(SubscriptionRisk.HEALTHY, sub.risk)
        assertFalse(sub.isFlagged)
    }

    @Test
    fun `subscription key is stable and firestore safe`() {
        assertEquals("cloud_vps_digitalocean", DetectSubscriptionsUseCase.subscriptionKeyFor("Cloud VPS (DigitalOcean)"))
        assertEquals("fitness_first_gym", DetectSubscriptionsUseCase.subscriptionKeyFor("  Fitness First Gym #2 "))
    }
}
