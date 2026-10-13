package com.se3092.pulsefinance.data.remote.firebase

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

    fun signOut() {
        auth.signOut()
    }
}
