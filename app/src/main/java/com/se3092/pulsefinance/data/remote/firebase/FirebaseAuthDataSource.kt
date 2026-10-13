package com.se3092.pulsefinance.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.se3092.pulsefinance.domain.model.AuthProvider
import com.se3092.pulsefinance.domain.model.UserProfile
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
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

    val currentUser: UserProfile?
        get() = auth.currentUser?.toProfile()

    fun observeAuthState(): Flow<String?> =
        observeFirebaseUser().map { it?.uid }.distinctUntilChanged()

    fun observeCurrentUser(): Flow<UserProfile?> =
        observeFirebaseUser().map { it?.toProfile() }

    private fun observeFirebaseUser(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun signInWithEmail(email: String, password: String): Result<UserProfile> = runCatching {
        val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
        result.user?.toProfile() ?: throw IllegalStateException("Firebase user is null after login")
    }

    suspend fun signUpWithEmail(email: String, password: String): Result<UserProfile> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
        val user = result.user ?: throw IllegalStateException("Firebase user is null after sign up")
        // Email sign-ups have no display name; derive one from the address so greetings aren't blank.
        val derivedName = email.substringBefore("@")
            .split('.', '_', '-')
            .filter { it.isNotBlank() }
            .joinToString(" ") { part -> part.replaceFirstChar { it.uppercase() } }
        if (derivedName.isNotBlank()) {
            user.updateProfile(userProfileChangeRequest { displayName = derivedName }).await()
        }
        user.toProfile().copy(displayName = user.displayName ?: derivedName)
    }

    suspend fun signInWithGoogle(idToken: String): Result<UserProfile> = runCatching {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        result.user?.toProfile() ?: throw IllegalStateException("Firebase user is null after Google sign-in")
    }

    fun signOut() {
        auth.signOut()
    }

    private fun FirebaseUser.toProfile(): UserProfile = UserProfile(
        uid = uid,
        email = email,
        displayName = displayName,
        photoUrl = photoUrl?.toString(),
        provider = if (providerData.any { it.providerId == GoogleAuthProvider.PROVIDER_ID }) {
            AuthProvider.GOOGLE
        } else {
            AuthProvider.EMAIL
        }
    )
}
