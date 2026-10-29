package com.se3092.pulsefinance.data.repository

import com.se3092.pulsefinance.data.remote.firebase.FirestoreDataSource
import com.se3092.pulsefinance.domain.model.OnboardingProfile
import com.se3092.pulsefinance.domain.repository.OnboardingRepository
import javax.inject.Inject
import javax.inject.Singleton

/** Written once at the end of first-run setup; Firestore queues it if he is offline. */
@Singleton
class OnboardingRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource
) : OnboardingRepository {

    override fun saveProfile(userId: String, profile: OnboardingProfile) {
        firestoreDataSource.saveOnboardingProfile(userId, profile)
    }
}
