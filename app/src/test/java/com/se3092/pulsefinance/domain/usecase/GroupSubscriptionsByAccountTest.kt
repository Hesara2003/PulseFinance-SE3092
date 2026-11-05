package com.se3092.pulsefinance.domain.usecase

import com.se3092.pulsefinance.domain.model.DetectedSubscription
import com.se3092.pulsefinance.domain.model.SubscriptionStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class GroupSubscriptionsByAccountTest {

    private fun sub(title: String, account: String) = DetectedSubscription(
        key = title.lowercase(), title = title, accountName = account, monthlyCostLkr = 1000.0,
        lastChargedMillis = 0L, chargeCount = 2, status = SubscriptionStatus.NEEDS_REVIEW
    )

    @Test
    fun `subscriptions are grouped by account in their original order (FR-SUB-6)`() {
        val groups = DetectSubscriptionsUseCase.groupByAccount(
            listOf(sub("Gym", "Secondary"), sub("Netflix", "Primary"), sub("VPS", "Secondary"))
        )
        assertEquals(listOf("Secondary", "Primary"), groups.map { it.first })
        assertEquals(listOf("Gym", "VPS"), groups.first().second.map { it.title })
    }

    @Test
    fun `a charge with no account goes under Unassigned`() {
        val groups = DetectSubscriptionsUseCase.groupByAccount(listOf(sub("Spotify", "")))
        assertEquals(DetectSubscriptionsUseCase.UNASSIGNED, groups.single().first)
    }
}
