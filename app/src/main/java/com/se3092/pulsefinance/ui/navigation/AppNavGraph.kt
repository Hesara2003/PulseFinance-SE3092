package com.se3092.pulsefinance.ui.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
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
import com.se3092.pulsefinance.ui.theme.NeoTextSecondary
import com.se3092.pulsefinance.ui.theme.NeoTextTertiary

data class NavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)

@Composable
fun AppNavGraph(
    startDestination: String = Screen.Dashboard.route
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute != Screen.Auth.route

    Scaffold(
        containerColor = NeoSurface,
        bottomBar = {
            if (showBottomBar) {
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
                            // 1. Cockpit
                            val isCockpit = currentRoute == Screen.Dashboard.route
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable {
                                        if (!isCockpit) {
                                            navController.navigate(Screen.Dashboard.route) {
                                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Dashboard,
                                    contentDescription = "Cockpit",
                                    tint = if (isCockpit) NeoGreen else NeoTextTertiary,
                                    modifier = Modifier.size(21.dp)
                                )
                                Text(
                                    text = "Cockpit",
                                    fontSize = 10.sp,
                                    fontWeight = if (isCockpit) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isCockpit) NeoGreen else NeoTextTertiary
                                )
                            }

                            // 2. Analytics
                            val isAnalytics = currentRoute == Screen.Analytics.route
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable {
                                        if (!isAnalytics) {
                                            navController.navigate(Screen.Analytics.route) {
                                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Analytics,
                                    contentDescription = "Analytics",
                                    tint = if (isAnalytics) NeoGreen else NeoTextTertiary,
                                    modifier = Modifier.size(21.dp)
                                )
                                Text(
                                    text = "Analytics",
                                    fontSize = 10.sp,
                                    fontWeight = if (isAnalytics) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isAnalytics) NeoGreen else NeoTextTertiary
                                )
                            }

                            // 3. Center Floating Add / Income Hub Button
                            val isIncome = currentRoute == Screen.Income.route
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .shadow(8.dp, CircleShape, spotColor = Color(0x5900C950))
                                    .clip(CircleShape)
                                    .background(NeoGreen)
                                    .clickable {
                                        if (!isIncome) {
                                            navController.navigate(Screen.Income.route) {
                                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Income Hub",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            // 4. Goals
                            val isGoals = currentRoute == Screen.Goal.route
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable {
                                        if (!isGoals) {
                                            navController.navigate(Screen.Goal.route) {
                                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RocketLaunch,
                                    contentDescription = "Goals",
                                    tint = if (isGoals) NeoGreen else NeoTextTertiary,
                                    modifier = Modifier.size(21.dp)
                                )
                                Text(
                                    text = "Goals",
                                    fontSize = 10.sp,
                                    fontWeight = if (isGoals) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isGoals) NeoGreen else NeoTextTertiary
                                )
                            }

                            // 5. Audit
                            val isAudit = currentRoute == Screen.Expenses.route
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable {
                                        if (!isAudit) {
                                            navController.navigate(Screen.Expenses.route) {
                                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                                    contentDescription = "Audit",
                                    tint = if (isAudit) NeoGreen else NeoTextTertiary,
                                    modifier = Modifier.size(21.dp)
                                )
                                Text(
                                    text = "Audit",
                                    fontSize = 10.sp,
                                    fontWeight = if (isAudit) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isAudit) NeoGreen else NeoTextTertiary
                                )
                            }
                        }
                    }
                }
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
                fun goToTab(route: String) {
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    onNavigateToIncome = { goToTab(Screen.Income.route) },
                    onNavigateToGoal = { goToTab(Screen.Goal.route) },
                    onNavigateToAnalytics = { goToTab(Screen.Analytics.route) },
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
