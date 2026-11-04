package com.se3092.pulsefinance.data.repository

import android.util.Log
import com.se3092.pulsefinance.data.remote.firebase.FirestoreDataSource
import com.se3092.pulsefinance.domain.model.SubscriptionReview
import com.se3092.pulsefinance.domain.repository.SubscriptionReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Review decisions are small and only read by the audit screen, so they rely on
 * Firestore's own offline cache rather than a Room table.
 */
@Singleton
class SubscriptionReviewRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource
) : SubscriptionReviewRepository {

    override fun getReviews(userId: String): Flow<Map<String, SubscriptionReview>> =
        firestoreDataSource.observeSubscriptionReviews(userId)
            .onStart { emit(emptyMap()) }
            .catch { e ->
                Log.w(TAG, "Subscription review listener failed", e)
                emit(emptyMap())
            }

    override fun saveReview(userId: String, review: SubscriptionReview) {
        firestoreDataSource.saveSubscriptionReview(userId, review)
    }

    private companion object {
        const val TAG = "SubscriptionReviewRepo"
    }
}
