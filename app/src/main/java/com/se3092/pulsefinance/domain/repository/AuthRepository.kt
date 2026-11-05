package com.se3092.pulsefinance.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUserId: String?
    val isUserAuthenticated: Boolean
    val userEmail: String?
    val userDisplayName: String?
    /** Profile photo from the sign-in provider (Google); null for email accounts. */
    val userPhotoUrl: String?
    val isEmailVerified: Boolean
    /** True for an email-and-password account, whose deletion is confirmed with the password. */
    val usesPasswordSignIn: Boolean
    fun observeAuthState(): Flow<String?>
    suspend fun signInWithEmail(email: String, password: String): Result<String>
    suspend fun signUpWithEmail(email: String, password: String): Result<String>
    suspend fun signInWithGoogle(idToken: String): Result<String>
    /** FR-AUTH-8: emails a reset link; the same answer whether or not the address has an account. */
    suspend fun sendPasswordReset(email: String): Result<Unit>
    /** FR-AUTH-10 */
    suspend fun sendEmailVerification(): Result<Unit>
    /** Re-reads the account and returns whether the email is now verified. */
    suspend fun refreshEmailVerified(): Result<Boolean>
    /**
     * FR-AUTH-9: proves it is him ([password], or [googleIdToken] for a Google account), erases every
     * record stored for him, deletes the sign-in, and empties the device cache. Nothing is erased if
     * the proof fails.
     */
    suspend fun deleteAccount(password: String?, googleIdToken: String?): Result<Unit>
    suspend fun signOut()
}
