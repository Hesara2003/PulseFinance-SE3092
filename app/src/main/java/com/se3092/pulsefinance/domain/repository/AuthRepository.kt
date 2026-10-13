package com.se3092.pulsefinance.domain.repository

import com.se3092.pulsefinance.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUserId: String?
    val isUserAuthenticated: Boolean
    val userEmail: String?
    val currentUser: UserProfile?

    /** Emits the signed-in uid (or null) whenever the Firebase auth state changes. */
    fun observeAuthState(): Flow<String?>
    fun observeCurrentUser(): Flow<UserProfile?>

    suspend fun signInWithEmail(email: String, password: String): Result<String>
    suspend fun signUpWithEmail(email: String, password: String): Result<String>
    suspend fun signInWithGoogle(idToken: String): Result<String>
    suspend fun signOut()

    /** Persona seeding happens once per user; the flag lives on the Firestore user document. */
    suspend fun hasSeededScenario(userId: String): Boolean
    suspend fun markScenarioSeeded(userId: String)
}
