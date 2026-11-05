package com.se3092.pulsefinance.data.remote.firebase

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthDataSource @Inject constructor(
    private val auth: FirebaseAuth
) {
    val currentUserId: String?
        get() = auth.currentUser?.uid

    val isUserAuthenticated: Boolean
        get() = auth.currentUser != null

    val userEmail: String?
        get() = auth.currentUser?.email

    val userDisplayName: String?
        get() = auth.currentUser?.displayName

    val userPhotoUrl: String?
        get() = auth.currentUser?.photoUrl?.toString()

    val isEmailVerified: Boolean
        get() = auth.currentUser?.isEmailVerified == true

    /** True for an email-and-password account; false for Google, which re-authenticates with a token. */
    val usesPasswordSignIn: Boolean
        get() = auth.currentUser?.providerData?.any { it.providerId == EmailAuthProvider.PROVIDER_ID } == true

    fun observeAuthState(): Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.uid)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun signInWithEmail(email: String, password: String): Result<String> = runCatching {
        val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
        result.user?.uid ?: throw IllegalStateException("Firebase User ID is null after login")
    }

    suspend fun signUpWithEmail(email: String, password: String): Result<String> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
        result.user?.uid ?: throw IllegalStateException("Firebase User ID is null after sign up")
    }

    suspend fun signInWithGoogle(idToken: String): Result<String> = runCatching {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        result.user?.uid ?: throw IllegalStateException("Firebase User ID is null after Google sign-in")
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> = runCatching {
        auth.sendPasswordResetEmail(email.trim()).await()
        Unit
    }

    suspend fun sendEmailVerification(): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("Not signed in")
        user.sendEmailVerification().await()
        Unit
    }

    /** Re-reads the account from Firebase so a verification done in the mail app shows up. */
    suspend fun reloadUser(): Result<Boolean> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("Not signed in")
        user.reload().await()
        user.isEmailVerified
    }

    /** Deleting an account needs a recent sign-in; this proves it is him. */
    suspend fun reauthenticateWithPassword(password: String) {
        val user = auth.currentUser ?: throw IllegalStateException("Not signed in")
        val email = user.email ?: throw IllegalStateException("This account has no email")
        user.reauthenticate(EmailAuthProvider.getCredential(email, password)).await()
    }

    suspend fun reauthenticateWithGoogle(idToken: String) {
        val user = auth.currentUser ?: throw IllegalStateException("Not signed in")
        user.reauthenticate(GoogleAuthProvider.getCredential(idToken, null)).await()
    }

    suspend fun deleteCurrentUser() {
        val user = auth.currentUser ?: throw IllegalStateException("Not signed in")
        user.delete().await()
    }

    fun signOut() {
        auth.signOut()
    }
}
