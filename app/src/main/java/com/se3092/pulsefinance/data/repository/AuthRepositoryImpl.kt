package com.se3092.pulsefinance.data.repository

import com.se3092.pulsefinance.data.remote.firebase.FirebaseAuthDataSource
import com.se3092.pulsefinance.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthDataSource: FirebaseAuthDataSource
) : AuthRepository {

    override val currentUserId: String?
        get() = firebaseAuthDataSource.currentUserId

    override val isUserAuthenticated: Boolean
        get() = firebaseAuthDataSource.isUserAuthenticated

    override val userEmail: String?
        get() = firebaseAuthDataSource.userEmail

    override fun observeAuthState(): Flow<String?> =
        firebaseAuthDataSource.observeAuthState()

    override suspend fun signInWithEmail(email: String, password: String): Result<String> =
        firebaseAuthDataSource.signInWithEmail(email, password)

    override suspend fun signUpWithEmail(email: String, password: String): Result<String> =
        firebaseAuthDataSource.signUpWithEmail(email, password)

    override suspend fun signOut() =
        firebaseAuthDataSource.signOut()
}
