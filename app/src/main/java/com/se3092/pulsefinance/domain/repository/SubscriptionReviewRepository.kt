package com.se3092.pulsefinance.domain.repository

import com.se3092.pulsefinance.domain.model.SubscriptionReview
import kotlinx.coroutines.flow.Flow

interface SubscriptionReviewRepository {
    fun getReviews(userId: String): Flow<Map<String, SubscriptionReview>>
    fun saveReview(userId: String, review: SubscriptionReview)
}
