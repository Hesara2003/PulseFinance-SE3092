package com.se3092.pulsefinance.ui.navigation

import com.se3092.pulsefinance.ui.theme.pulseColors
import androidx.compose.material3.MaterialTheme
import com.se3092.pulsefinance.R
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.border
import androidx.compose.foundation.Image
import androidx.compose.animation.animateColorAsState
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.navigation.NavBackStackEntry
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.Paid
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
import com.se3092.pulsefinance.ui.screens.setup.SetupScreen
import com.se3092.pulsefinance.ui.screens.setup.SetupViewModel

data class NavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)

/** Dock order, left to right, so a tab change slides the way the user's finger moved. */
private val tabOrder = listOf(
    Screen.Dashboard.route,
    Screen.Analytics.route,
    Screen.Income.route,
    Screen.Goal.route,
    Screen.Expenses.route
)

private const val PAGE_MS = 300

/**
 * Material "shared axis" page change: between tabs the new page slides in a fifth of the way
 * from the side of the tab that was tapped while it fades in; anything else (sign-in, setup)
 * fades in with a slight zoom. Short and eased, instead of the default 700 ms crossfade.
 */
private fun AnimatedContentTransitionScope<NavBackStackEntry>.pageEnter(from: String?, to: String?): EnterTransition {
    val direction = tabDirection(from, to) ?: return fadeIn(tween(PAGE_MS)) + scaleIn(tween(PAGE_MS, easing = FastOutSlowInEasing), initialScale = 0.96f)
    return slideIntoContainer(direction, tween(PAGE_MS, easing = FastOutSlowInEasing)) { it / 5 } +
        fadeIn(tween(PAGE_MS - 90, delayMillis = 90, easing = LinearOutSlowInEasing))
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.pageExit(from: String?, to: String?): ExitTransition {
    val direction = tabDirection(from, to) ?: return fadeOut(tween(PAGE_MS / 2))
    return slideOutOfContainer(direction, tween(PAGE_MS, easing = FastOutSlowInEasing)) { it / 5 } +
        fadeOut(tween(90, easing = FastOutLinearInEasing))
}

private fun tabDirection(from: String?, to: String?): AnimatedContentTransitionScope.SlideDirection? {
    val a = tabOrder.indexOf(from)
    val b = tabOrder.indexOf(to)
    if (a < 0 || b < 0 || a == b) return null
    return if (b > a) AnimatedContentTransitionScope.SlideDirection.Start else AnimatedContentTransitionScope.SlideDirection.End
}

@Composable
fun AppNavGraph(
    startDestination: String = Screen.Dashboard.route,
    sharedSms: String? = null
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute != Screen.Auth.route && currentRoute != Screen.Setup.route

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
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
                                ambientColor = MaterialTheme.pulseColors.shadow.copy(alpha = 0.12f),
                                spotColor = MaterialTheme.pulseColors.shadow.copy(alpha = 0.12f)
                            ),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            fun goTo(route: String) {
                                if (currentRoute != route) navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                            DockItem("Today", R.drawable.ic_nav_today, currentRoute == Screen.Dashboard.route) { goTo(Screen.Dashboard.route) }
                            DockItem("Analytics", R.drawable.ic_nav_analytics, currentRoute == Screen.Analytics.route) { goTo(Screen.Analytics.route) }
                            DockCenterButton(currentRoute == Screen.Income.route) { goTo(Screen.Income.route) }
                            DockItem("Quest", R.drawable.ic_nav_quest, currentRoute == Screen.Goal.route) { goTo(Screen.Goal.route) }
                            DockItem("Audit", R.drawable.ic_nav_audit, currentRoute == Screen.Expenses.route) { goTo(Screen.Expenses.route) }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { pageEnter(initialState.destination.route, targetState.destination.route) },
            exitTransition = { pageExit(initialState.destination.route, targetState.destination.route) },
            popEnterTransition = { pageEnter(initialState.destination.route, targetState.destination.route) },
            popExitTransition = { pageExit(initialState.destination.route, targetState.destination.route) }
        ) {
            composable(Screen.Auth.route) {
                val authViewModel: AuthViewModel = hiltViewModel()
                AuthScreen(
                    viewModel = authViewModel,
                    onAuthSuccess = { needsSetup ->
                        navController.navigate(if (needsSetup) Screen.Setup.route else Screen.Dashboard.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Setup.route) {
                val setupViewModel: SetupViewModel = hiltViewModel()
                SetupScreen(
                    viewModel = setupViewModel,
                    onFinished = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Setup.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Dashboard.route) {
                val dashboardViewModel: DashboardViewModel = hiltViewModel()
                LaunchedEffect(sharedSms) {
                    sharedSms?.let { dashboardViewModel.handleSharedBankSms(it) }
                }
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

/** Desaturates an unselected tab's icon, so the selected one is the only one in colour. */
private val dockInactiveFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })

/**
 * One dock tab: the illustrated icon in colour on a soft green blob when selected, the same icon
 * in grey when not.
 */
@Composable
private fun DockItem(label: String, @DrawableRes icon: Int, selected: Boolean, onClick: () -> Unit) {
    val blob by animateColorAsState(if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.16f) else Color.Transparent, label = "dock-blob")
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .selectable(selected = selected, role = Role.Tab, onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 44.dp, height = 30.dp)
                .clip(CircleShape)
                .background(blob),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                colorFilter = if (selected) null else dockInactiveFilter,
                alpha = if (selected) 1f else 0.8f,
                modifier = Modifier.size(26.dp)
            )
        }
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.pulseColors.textTertiary
        )
    }
}

/** The raised Income button in the middle of the dock: a white disc with a green ring and the wallet. */
@Composable
private fun DockCenterButton(selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .shadow(8.dp, CircleShape, spotColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
            .clip(CircleShape)
            .background(if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surface)
            .border(if (selected) 3.dp else 2.dp, MaterialTheme.colorScheme.primaryContainer, CircleShape)
            .selectable(selected = selected, role = Role.Tab, onClick = onClick)
            .semantics { contentDescription = "Income" },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_nav_income),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
    }
}
