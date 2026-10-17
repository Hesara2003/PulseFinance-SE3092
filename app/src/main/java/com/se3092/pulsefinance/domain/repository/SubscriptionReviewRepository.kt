package com.se3092.pulsefinance.domain.repository

import com.se3092.pulsefinance.domain.model.SubscriptionReview
import kotlinx.coroutines.flow.Flow

interface SubscriptionReviewRepository {
    /** Reviews keyed by DetectedSubscription.key. */
    fun observeReviews(userId: String): Flow<Map<String, SubscriptionReview>>
    suspend fun saveReview(review: SubscriptionReview): Result<Unit>
    suspend fun clearReview(userId: String, subscriptionKey: String): Result<Unit>
}
