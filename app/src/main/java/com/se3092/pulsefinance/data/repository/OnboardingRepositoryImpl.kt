package com.se3092.pulsefinance.data.repository

import android.util.Log
import com.se3092.pulsefinance.data.remote.firebase.FirestoreDataSource
import com.se3092.pulsefinance.domain.model.EmergencyRunway
import com.se3092.pulsefinance.domain.model.OnboardingProfile
import com.se3092.pulsefinance.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

/**
 * One small map on the user document, written at the end of setup and when he adjusts his
 * buffer. Firestore queues writes if he is offline, and its own cache serves reads, so there is
 * no Room table for it.
 */
@Singleton
class OnboardingRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource
) : OnboardingRepository {

    override fun saveProfile(userId: String, profile: OnboardingProfile) {
        firestoreDataSource.saveOnboardingProfile(userId, profile)
    }

    override fun getProfile(userId: String): Flow<OnboardingProfile?> =
        firestoreDataSource.observeOnboardingProfile(userId)
            .onStart { emit(null) }
            .catch { e ->
                Log.w(TAG, "Onboarding listener failed", e)
                emit(null)
            }

    override fun saveRunway(userId: String, months: Int, savedLkr: Double) {
        firestoreDataSource.saveRunway(userId, EmergencyRunway.clampMonths(months), savedLkr.coerceAtLeast(0.0))
    }

    private companion object {
        const val TAG = "OnboardingRepository"
    }
}
