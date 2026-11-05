package com.se3092.pulsefinance.ui.navigation

sealed class Screen(val route: String, val title: String) {
    data object Auth : Screen("auth", "Authentication")
    data object Setup : Screen("setup", "Set up")
    data object Dashboard : Screen("dashboard", "Cockpit")
    data object Expenses : Screen("expenses", "Expenses")
    data object Income : Screen("income", "Income Streams")
    data object Goal : Screen("goal", "MacBook Goal")
    data object Analytics : Screen("analytics", "Clarity & Audit")
}
