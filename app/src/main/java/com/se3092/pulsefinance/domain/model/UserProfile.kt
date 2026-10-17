package com.se3092.pulsefinance.domain.model

data class UserProfile(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String? = null,
    val provider: AuthProvider = AuthProvider.EMAIL
)

enum class AuthProvider { EMAIL, GOOGLE }
