package com.se3092.pulsefinance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.se3092.pulsefinance.domain.repository.AuthRepository
import com.se3092.pulsefinance.ui.navigation.AppNavGraph
import com.se3092.pulsefinance.ui.navigation.Screen
import com.se3092.pulsefinance.ui.theme.NeoSurface
import com.se3092.pulsefinance.ui.theme.PulseFinanceTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT)
        )

        val startDestination = if (authRepository.isUserAuthenticated) {
            Screen.Dashboard.route
        } else {
            Screen.Auth.route
        }

        setContent {
            PulseFinanceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = NeoSurface
                ) {
                    AppNavGraph(startDestination = startDestination)
                }
            }
        }
    }
}
