package com.se3092.pulsefinance.domain.repository

import com.se3092.pulsefinance.domain.model.OnboardingProfile
import kotlinx.coroutines.flow.Flow

interface OnboardingRepository {
    fun saveProfile(userId: String, profile: OnboardingProfile)

    /** Null until he has finished setup or chosen a buffer. */
    fun getProfile(userId: String): Flow<OnboardingProfile?>

    /** Updates only the emergency buffer (FR-AUTH-6); everything else he told setup is kept. */
    fun saveRunway(userId: String, months: Int, savedLkr: Double)
}
