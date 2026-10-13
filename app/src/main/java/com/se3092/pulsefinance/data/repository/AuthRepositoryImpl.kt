package com.se3092.pulsefinance.data.repository

import com.se3092.pulsefinance.data.remote.firebase.FirebaseAuthDataSource
import com.se3092.pulsefinance.data.remote.firebase.FirestoreDataSource
import com.se3092.pulsefinance.domain.model.UserProfile
import com.se3092.pulsefinance.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthDataSource: FirebaseAuthDataSource,
    private val firestoreDataSource: FirestoreDataSource,
    private val remoteWriter: RemoteWriter
) : AuthRepository {

    override val currentUserId: String?
        get() = firebaseAuthDataSource.currentUserId

    override val isUserAuthenticated: Boolean
        get() = firebaseAuthDataSource.isUserAuthenticated

    override val userEmail: String?
        get() = firebaseAuthDataSource.userEmail

    override val currentUser: UserProfile?
        get() = firebaseAuthDataSource.currentUser

    override fun observeAuthState(): Flow<String?> =
        firebaseAuthDataSource.observeAuthState()

    override fun observeCurrentUser(): Flow<UserProfile?> =
        firebaseAuthDataSource.observeCurrentUser()

    override suspend fun signInWithEmail(email: String, password: String): Result<String> =
        firebaseAuthDataSource.signInWithEmail(email, password).onSuccessSyncProfile()

    override suspend fun signUpWithEmail(email: String, password: String): Result<String> =
        firebaseAuthDataSource.signUpWithEmail(email, password).onSuccessSyncProfile()

    override suspend fun signInWithGoogle(idToken: String): Result<String> =
        firebaseAuthDataSource.signInWithGoogle(idToken).onSuccessSyncProfile()

    override suspend fun signOut() =
        firebaseAuthDataSource.signOut()

    override suspend fun hasSeededScenario(userId: String): Boolean =
        firestoreDataSource.isScenarioSeeded(userId)

    override suspend fun markScenarioSeeded(userId: String) {
        remoteWriter.enqueue("markScenarioSeeded") { firestoreDataSource.markScenarioSeeded(userId) }
    }

    /** Keeps users/{uid} populated with profile fields; never blocks sign-in on it. */
    private fun Result<UserProfile>.onSuccessSyncProfile(): Result<String> = map { profile ->
        remoteWriter.enqueue("upsertUserDocument") { firestoreDataSource.upsertUserDocument(profile) }
        profile.uid
    }
}
