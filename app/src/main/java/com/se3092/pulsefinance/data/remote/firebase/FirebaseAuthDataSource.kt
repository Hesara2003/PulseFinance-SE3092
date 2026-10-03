package com.se3092.pulsefinance.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
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

    fun observeAuthState(): Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.uid)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun signInWithEmail(email: String, password: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
            val uid = result.user?.uid ?: throw IllegalStateException("Firebase User ID is null after login")
            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(email: String, password: String): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val uid = result.user?.uid ?: throw IllegalStateException("Firebase User ID is null after sign up")
            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut() {
        auth.signOut()
    }
}
