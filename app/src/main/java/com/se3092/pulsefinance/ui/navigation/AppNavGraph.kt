package com.se3092.pulsefinance.ui.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.se3092.pulsefinance.ui.screens.analytics.AnalyticsScreen
import com.se3092.pulsefinance.ui.screens.analytics.AnalyticsViewModel
import com.se3092.pulsefinance.ui.screens.auth.AuthScreen
import com.se3092.pulsefinance.ui.screens.auth.AuthViewModel
import com.se3092.pulsefinance.ui.screens.dashboard.DashboardScreen
import com.se3092.pulsefinance.ui.screens.dashboard.DashboardViewModel
import com.se3092.pulsefinance.ui.screens.expense.ExpenseScreen
import com.se3092.pulsefinance.ui.screens.expense.ExpenseViewModel
import com.se3092.pulsefinance.ui.screens.goal.GoalScreen
import com.se3092.pulsefinance.ui.screens.goal.GoalViewModel
import com.se3092.pulsefinance.ui.screens.income.IncomeScreen
import com.se3092.pulsefinance.ui.screens.income.IncomeViewModel
import com.se3092.pulsefinance.ui.theme.NeoBorder
import com.se3092.pulsefinance.ui.theme.NeoCard
import com.se3092.pulsefinance.ui.theme.NeoGreen
import com.se3092.pulsefinance.ui.theme.NeoSurface
import com.se3092.pulsefinance.ui.theme.NeoTextTertiary
import com.se3092.pulsefinance.ui.theme.OnAccent
import kotlinx.coroutines.flow.Flow

data class NavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)

/** Bottom dock tabs; the centre slot is the Income Hub action button. */
private val leadingTabs = listOf(
    NavItem(Screen.Dashboard, Icons.Default.Dashboard, "Today"),
    NavItem(Screen.Analytics, Icons.Default.Analytics, "Analytics")
)
private val trailingTabs = listOf(
    NavItem(Screen.Goal, Icons.Default.RocketLaunch, "Goals"),
    NavItem(Screen.Expenses, Icons.AutoMirrored.Filled.ReceiptLong, "Audit")
)

/** Single-top tab switch that keeps each tab's back stack and scroll state. */
fun NavHostController.navigateToTab(route: String) {
    if (currentDestination?.route == route) return
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun AppNavGraph(
    startDestination: String = Screen.Dashboard.route,
    authState: Flow<String?>? = null
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute != null && currentRoute != Screen.Auth.route

    // Session expiry / sign-out from anywhere returns to the auth flow with a clean stack.
    if (authState != null) {
        LaunchedEffect(authState) {
            authState.collect { uid ->
                if (uid == null && navController.currentDestination?.route != Screen.Auth.route) {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = NeoSurface,
        bottomBar = {
            if (showBottomBar) {
                BottomDock(
                    currentRoute = currentRoute,
                    onNavigate = navController::navigateToTab
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Auth.route) {
                val authViewModel: AuthViewModel = hiltViewModel()
                AuthScreen(
                    viewModel = authViewModel,
                    onAuthSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Dashboard.route) {
                val dashboardViewModel: DashboardViewModel = hiltViewModel()
                val expenseViewModel: ExpenseViewModel = hiltViewModel()
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    expenseViewModel = expenseViewModel,
                    onNavigateToAnalytics = { navController.navigateToTab(Screen.Analytics.route) },
                    onNavigateToIncome = { navController.navigateToTab(Screen.Income.route) },
                    onNavigateToGoal = { navController.navigateToTab(Screen.Goal.route) },
                    onSignOut = {
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Expenses.route) {
                val expenseViewModel: ExpenseViewModel = hiltViewModel()
                ExpenseScreen(viewModel = expenseViewModel)
            }

            composable(Screen.Income.route) {
                val incomeViewModel: IncomeViewModel = hiltViewModel()
                IncomeScreen(viewModel = incomeViewModel)
            }

            composable(Screen.Goal.route) {
                val goalViewModel: GoalViewModel = hiltViewModel()
                GoalScreen(viewModel = goalViewModel)
            }

            composable(Screen.Analytics.route) {
                val analyticsViewModel: AnalyticsViewModel = hiltViewModel()
                AnalyticsScreen(viewModel = analyticsViewModel)
            }
        }
    }
}

@Composable
private fun BottomDock(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 16.dp,
                    shape = CircleShape,
                    ambientColor = Color(0x1F0F172A),
                    spotColor = Color(0x1F0F172A)
                ),
            shape = CircleShape,
            color = NeoCard,
            border = BorderStroke(1.dp, NeoBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                leadingTabs.forEach { item ->
                    DockTab(item, isSelected = currentRoute == item.screen.route, onNavigate)
                }

                // Centre floating Income Hub button
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .shadow(8.dp, CircleShape, spotColor = Color(0x5900C950))
                        .clip(CircleShape)
                        .background(NeoGreen)
                        .clickable { onNavigate(Screen.Income.route) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = "Income Hub",
                        tint = OnAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }

                trailingTabs.forEach { item ->
                    DockTab(item, isSelected = currentRoute == item.screen.route, onNavigate)
                }
            }
        }
    }
}

@Composable
private fun DockTab(
    item: NavItem,
    isSelected: Boolean,
    onNavigate: (String) -> Unit
) {
    val tint = if (isSelected) NeoGreen else NeoTextTertiary
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onNavigate(item.screen.route) }
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = tint,
            modifier = Modifier.size(21.dp)
        )
        Text(
            text = item.label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = tint
        )
    }
}
