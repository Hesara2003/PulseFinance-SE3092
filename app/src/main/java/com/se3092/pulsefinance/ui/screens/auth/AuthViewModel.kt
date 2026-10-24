package com.se3092.pulsefinance.ui.screens.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.se3092.pulsefinance.domain.repository.AccountRepository
import com.se3092.pulsefinance.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState(isAuthenticated = authRepository.isUserAuthenticated))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun toggleAuthMode() {
        _uiState.update { it.copy(isLoginMode = !it.isLoginMode, errorMessage = null) }
    }

    fun setLoginMode(isLogin: Boolean) {
        _uiState.update { it.copy(isLoginMode = isLogin, errorMessage = null) }
    }

    fun fillDemoCredentials(email: String, pass: String) {
        _uiState.update { it.copy(email = email, password = pass, isLoginMode = true, errorMessage = null) }
    }

    fun submit() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        val validationError = when {
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Enter a valid email address"
            password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError) }
            return
        }

        authenticate {
            if (_uiState.value.isLoginMode) authRepository.signInWithEmail(email, password)
            else authRepository.signUpWithEmail(email, password)
        }
    }

    /** Called by the screen once the Google account picker has produced an ID token. */
    fun onGoogleIdToken(idToken: String) {
        authenticate { authRepository.signInWithGoogle(idToken) }
    }

    fun onGoogleSignInFailed(error: Throwable) {
        _uiState.update { it.copy(isLoading = false, errorMessage = "Google sign-in failed: ${error.localizedMessage ?: "unknown error"}") }
    }

    private fun authenticate(call: suspend () -> Result<String>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            call().fold(
                onSuccess = { uid ->
                    // First sign-in: create the payment channels expenses are attributed to.
                    // Best effort — an offline failure just means it runs on the next sign-in.
                    accountRepository.ensureDefaultAccounts(uid)
                    _uiState.update { it.copy(isLoading = false, isAuthenticated = true, password = "") }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = friendlyMessage(error)) }
                }
            )
        }
    }

    private fun friendlyMessage(error: Throwable): String = when (error) {
        is FirebaseNetworkException -> "No internet connection. Check your network and try again."
        is FirebaseAuthWeakPasswordException -> "That password is too weak — use at least 6 characters."
        is FirebaseAuthUserCollisionException -> "An account already exists with this email. Sign in instead."
        is FirebaseAuthInvalidUserException -> "No account found for this email. Create one first."
        is FirebaseAuthInvalidCredentialsException -> "Email or password is incorrect."
        else -> error.localizedMessage ?: "Authentication failed. Please try again."
    }
}
