package com.se3092.pulsefinance.ui.screens.auth

data class AuthUiState(
    val email: String = "kavindu.silva@pulsemail.io",
    val password: String = "ColomboDev2026!",
    val isLoginMode: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false
)
