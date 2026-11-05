package com.se3092.pulsefinance.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUserId: String?
    val isUserAuthenticated: Boolean
    val userEmail: String?
    val userDisplayName: String?
    /** Profile photo from the sign-in provider (Google); null for email accounts. */
    val userPhotoUrl: String?
    fun observeAuthState(): Flow<String?>
    suspend fun signInWithEmail(email: String, password: String): Result<String>
    suspend fun signUpWithEmail(email: String, password: String): Result<String>
    suspend fun signInWithGoogle(idToken: String): Result<String>
    suspend fun signOut()
}
