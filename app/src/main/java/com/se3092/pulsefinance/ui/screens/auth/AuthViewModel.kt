package com.se3092.pulsefinance.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se3092.pulsefinance.domain.repository.AccountRepository
import com.se3092.pulsefinance.domain.repository.AuthRepository
import com.se3092.pulsefinance.domain.repository.GoalRepository
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
    private val accountRepository: AccountRepository,
    private val goalRepository: GoalRepository
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

    fun submit() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        if (email.isBlank() || password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Please enter valid email and 6+ character password") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = if (_uiState.value.isLoginMode) {
                authRepository.signInWithEmail(email, password)
            } else {
                authRepository.signUpWithEmail(email, password)
            }

            result.fold(
                onSuccess = { uid ->
                    // Initialize default accounts for Kavindu if newly registered
                    accountRepository.initializeDefaultAccountsForKavindu(uid)
                    _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Authentication failed. Check credentials."
                        )
                    }
                }
            )
        }
    }
}
