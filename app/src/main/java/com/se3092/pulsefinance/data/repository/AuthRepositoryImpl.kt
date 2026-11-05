package com.se3092.pulsefinance.data.repository

import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import com.se3092.pulsefinance.data.local.AppDatabase
import com.se3092.pulsefinance.data.remote.firebase.FirebaseAuthDataSource
import com.se3092.pulsefinance.data.remote.firebase.FirestoreDataSource
import com.se3092.pulsefinance.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthDataSource: FirebaseAuthDataSource,
    private val firestoreDataSource: FirestoreDataSource,
    private val appDatabase: AppDatabase
) : AuthRepository {

    override val currentUserId: String?
        get() = firebaseAuthDataSource.currentUserId

    override val isUserAuthenticated: Boolean
        get() = firebaseAuthDataSource.isUserAuthenticated

    override val userEmail: String?
        get() = firebaseAuthDataSource.userEmail

    override val userDisplayName: String?
        get() = firebaseAuthDataSource.userDisplayName

    override val userPhotoUrl: String?
        get() = firebaseAuthDataSource.userPhotoUrl

    override val isEmailVerified: Boolean
        get() = firebaseAuthDataSource.isEmailVerified

    override val usesPasswordSignIn: Boolean
        get() = firebaseAuthDataSource.usesPasswordSignIn

    override fun observeAuthState(): Flow<String?> =
        firebaseAuthDataSource.observeAuthState()

    override suspend fun signInWithEmail(email: String, password: String): Result<String> =
        firebaseAuthDataSource.signInWithEmail(email, password)

    override suspend fun signUpWithEmail(email: String, password: String): Result<String> =
        firebaseAuthDataSource.signUpWithEmail(email, password)

    override suspend fun signInWithGoogle(idToken: String): Result<String> =
        firebaseAuthDataSource.signInWithGoogle(idToken)

    override suspend fun sendPasswordReset(email: String): Result<Unit> =
        firebaseAuthDataSource.sendPasswordReset(email)

    override suspend fun sendEmailVerification(): Result<Unit> =
        firebaseAuthDataSource.sendEmailVerification()

    override suspend fun refreshEmailVerified(): Result<Boolean> =
        firebaseAuthDataSource.reloadUser()

    override suspend fun deleteAccount(password: String?, googleIdToken: String?): Result<Unit> = runCatching {
        val userId = currentUserId ?: throw IllegalStateException("You're signed out")
        // Prove it is him first, so a wrong password fails before anything is erased.
        when {
            googleIdToken != null -> firebaseAuthDataSource.reauthenticateWithGoogle(googleIdToken)
            !password.isNullOrEmpty() -> firebaseAuthDataSource.reauthenticateWithPassword(password)
            else -> throw IllegalArgumentException("Confirm with your password")
        }
        // Records first, while still signed in and allowed to touch them; then the sign-in itself.
        firestoreDataSource.deleteAllUserData(userId)
        firebaseAuthDataSource.deleteCurrentUser()
        withContext(NonCancellable + Dispatchers.IO) { appDatabase.clearAllTables() }
    }

    /**
     * Signs out and empties the Room cache, so the previous user's records don't stay on the
     * device at rest (#98). Firestore still has them; they sync back on the next sign-in.
     */
    override suspend fun signOut() {
        firebaseAuthDataSource.signOut()
        // NonCancellable: the screen that started sign-out is left at once, which cancels its
        // scope; the cache must still be cleared.
        withContext(NonCancellable + Dispatchers.IO) { appDatabase.clearAllTables() }
    }
}
