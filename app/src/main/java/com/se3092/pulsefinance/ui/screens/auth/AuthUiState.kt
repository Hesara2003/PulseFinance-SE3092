package com.se3092.pulsefinance.ui.screens.auth

enum class AuthMethod { EMAIL, GOOGLE }

data class AuthUiState(
    val email: String = DEMO_EMAIL,
    val password: String = DEMO_PASSWORD,
    val isLoginMode: Boolean = true,
    val loadingMethod: AuthMethod? = null,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false
) {
    val isLoading: Boolean get() = loadingMethod != null

    companion object {
        const val DEMO_EMAIL = "kavindu.silva@pulsemail.io"
        const val DEMO_PASSWORD = "ColomboDev2026!"
    }
}
