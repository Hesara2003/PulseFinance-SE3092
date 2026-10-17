package com.se3092.pulsefinance.data.repository

import android.util.Log
import com.se3092.pulsefinance.data.remote.dto.SubscriptionReviewDto
import com.se3092.pulsefinance.data.remote.firebase.FirestoreDataSource
import com.se3092.pulsefinance.domain.model.SubscriptionReview
import com.se3092.pulsefinance.domain.repository.SubscriptionReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persists keep / cancel / dismiss decisions for detected subscriptions in
 * users/{uid}/subscription_reviews. Firestore's disk cache provides offline reads, and an
 * in-memory overlay makes a decision visible instantly, before the snapshot listener fires.
 */
@Singleton
class SubscriptionReviewRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource,
    private val remoteWriter: RemoteWriter
) : SubscriptionReviewRepository {

    // userId -> (subscriptionKey -> review or null when cleared)
    private val pendingOverlay = MutableStateFlow<Map<String, Map<String, SubscriptionReview?>>>(emptyMap())

    override fun observeReviews(userId: String): Flow<Map<String, SubscriptionReview>> {
        val remote = firestoreDataSource.observeSubscriptionReviews(userId)
            .map { snapshot -> snapshot.items.map { it.toDomain() }.associateBy { it.subscriptionKey } }
            .onStart { emit(emptyMap()) }
            .catch { e ->
                Log.w(TAG, "Subscription review sync failed", e)
                emit(emptyMap())
            }
        return combine(remote, pendingOverlay) { remoteReviews, overlay ->
            val userOverlay = overlay[userId].orEmpty()
            val merged = remoteReviews.toMutableMap()
            userOverlay.forEach { (key, review) ->
                if (review == null) merged.remove(key) else merged[key] = review
            }
            merged
        }
    }

    override suspend fun saveReview(review: SubscriptionReview): Result<Unit> = runCatching {
        setOverlay(review.userId, review.subscriptionKey, review)
        remoteWriter.enqueue("saveSubscriptionReview") {
            firestoreDataSource.saveSubscriptionReview(review.userId, SubscriptionReviewDto.fromDomain(review))
        }
    }

    override suspend fun clearReview(userId: String, subscriptionKey: String): Result<Unit> = runCatching {
        setOverlay(userId, subscriptionKey, null)
        remoteWriter.enqueue("deleteSubscriptionReview") {
            firestoreDataSource.deleteSubscriptionReview(userId, subscriptionKey)
        }
    }

    private fun setOverlay(userId: String, key: String, review: SubscriptionReview?) {
        pendingOverlay.update { all ->
            all + (userId to (all[userId].orEmpty() + (key to review)))
        }
    }

    private companion object {
        const val TAG = "SubscriptionReviewRepo"
    }
}
