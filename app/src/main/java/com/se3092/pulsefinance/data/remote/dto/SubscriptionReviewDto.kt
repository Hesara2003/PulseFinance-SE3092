package com.se3092.pulsefinance.data.remote.dto

import com.google.firebase.firestore.DocumentId
import com.se3092.pulsefinance.domain.model.SubscriptionReview
import com.se3092.pulsefinance.domain.model.SubscriptionReviewStatus

data class SubscriptionReviewDto(
    @DocumentId var id: String = "",
    var userId: String = "",
    var subscriptionKey: String = "",
    var status: String = SubscriptionReviewStatus.KEEP.name,
    var reviewedAtMillis: Long = System.currentTimeMillis()
) {
    fun toDomain(): SubscriptionReview = SubscriptionReview(
        subscriptionKey = subscriptionKey.ifBlank { id },
        userId = userId,
        status = try { SubscriptionReviewStatus.valueOf(status) } catch (e: Exception) { SubscriptionReviewStatus.KEEP },
        reviewedAtMillis = reviewedAtMillis
    )

    companion object {
        fun fromDomain(domain: SubscriptionReview): SubscriptionReviewDto = SubscriptionReviewDto(
            id = domain.subscriptionKey,
            userId = domain.userId,
            subscriptionKey = domain.subscriptionKey,
            status = domain.status.name,
            reviewedAtMillis = domain.reviewedAtMillis
        )
    }
}
