package com.se3092.pulsefinance.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se3092.pulsefinance.domain.repository.AuthRepository
import com.se3092.pulsefinance.domain.usecase.SeedPersonaScenarioUseCase
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
    private val seedPersonaScenario: SeedPersonaScenarioUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        if (authRepository.isUserAuthenticated) {
            _uiState.update { it.copy(isAuthenticated = true) }
        }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun toggleAuthMode() {
        _uiState.update { it.copy(isLoginMode = !it.isLoginMode, errorMessage = null) }
    }

    fun fillDemoPersona() {
        _uiState.update {
            it.copy(
                email = AuthUiState.DEMO_EMAIL,
                password = AuthUiState.DEMO_PASSWORD,
                isLoginMode = true,
                errorMessage = null
            )
        }
    }

    fun submit() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        if (email.isBlank() || password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Please enter valid email and 6+ character password") }
            return
        }

        authenticate(AuthMethod.EMAIL) {
            if (_uiState.value.isLoginMode) {
                authRepository.signInWithEmail(email, password)
            } else {
                authRepository.signUpWithEmail(email, password)
            }
        }
    }

    fun onGoogleSignInStarted() {
        _uiState.update { it.copy(loadingMethod = AuthMethod.GOOGLE, errorMessage = null) }
    }

    fun signInWithGoogle(idToken: String) {
        authenticate(AuthMethod.GOOGLE) { authRepository.signInWithGoogle(idToken) }
    }

    fun onGoogleSignInFailed(error: Throwable) {
        _uiState.update {
            it.copy(
                loadingMethod = null,
                errorMessage = if (error is GoogleSignInCancelledException) null
                else error.localizedMessage ?: "Google sign-in failed. Please try again."
            )
        }
    }

    private fun authenticate(method: AuthMethod, call: suspend () -> Result<String>) {
        viewModelScope.launch {
            _uiState.update { it.copy(loadingMethod = method, errorMessage = null) }
            call().fold(
                onSuccess = { uid ->
                    // Demo persona gets Kavindu's scenario once; everyone else a default account.
                    seedPersonaScenario(uid, authRepository.userEmail)
                    _uiState.update { it.copy(loadingMethod = null, isAuthenticated = true) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            loadingMethod = null,
                            errorMessage = error.localizedMessage ?: "Authentication failed. Check credentials."
                        )
                    }
                }
            )
        }
    }
}
