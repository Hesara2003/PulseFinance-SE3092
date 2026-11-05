package com.se3092.pulsefinance

import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.se3092.pulsefinance.domain.repository.AuthRepository
import com.se3092.pulsefinance.ui.components.LocalProfilePhotoUrl
import com.se3092.pulsefinance.ui.navigation.AppNavGraph
import com.se3092.pulsefinance.ui.navigation.Screen
import com.se3092.pulsefinance.ui.theme.NeoSurface
import com.se3092.pulsefinance.ui.theme.PulseFinanceTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Keep balances and revealed account numbers out of screenshots and the recent-apps preview (NFR-10).
        // Debug builds stay capturable so the team can take screenshots for docs and wireframes.
        if (!BuildConfig.DEBUG) {
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        }
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT)
        )

        val startDestination = if (authRepository.isUserAuthenticated) {
            Screen.Dashboard.route
        } else {
            Screen.Auth.route
        }

        val sharedSms = if (intent?.action == android.content.Intent.ACTION_SEND && intent?.type == "text/plain") {
            intent.getStringExtra(android.content.Intent.EXTRA_TEXT)
        } else null

        setContent {
            // Re-read on every sign-in and sign-out, so a Google account's photo appears at once.
            val photoUrl by remember { authRepository.observeAuthState().map { authRepository.userPhotoUrl } }
                .collectAsState(initial = authRepository.userPhotoUrl)
            PulseFinanceTheme {
                CompositionLocalProvider(LocalProfilePhotoUrl provides photoUrl) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = NeoSurface
                    ) {
                        AppNavGraph(startDestination = startDestination, sharedSms = sharedSms)
                    }
                }
            }
        }
    }
}
