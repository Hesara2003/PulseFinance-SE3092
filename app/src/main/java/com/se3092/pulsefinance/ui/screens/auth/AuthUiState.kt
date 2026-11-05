package com.se3092.pulsefinance.ui.screens.auth

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoginMode: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    /** A calm confirmation, e.g. that a password reset link was sent (FR-AUTH-8). */
    val infoMessage: String? = null,
    val isAuthenticated: Boolean = false,
    /** First sign-in of a new account: send him through setup before the dashboard (FR-AUTH-4). */
    val needsSetup: Boolean = false
)
