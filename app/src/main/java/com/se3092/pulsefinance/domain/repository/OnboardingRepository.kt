package com.se3092.pulsefinance.domain.repository

import com.se3092.pulsefinance.domain.model.OnboardingProfile

interface OnboardingRepository {
    fun saveProfile(userId: String, profile: OnboardingProfile)
}
