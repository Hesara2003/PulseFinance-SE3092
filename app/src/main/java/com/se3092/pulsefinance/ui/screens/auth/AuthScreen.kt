package com.se3092.pulsefinance.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.se3092.pulsefinance.R
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.se3092.pulsefinance.BuildConfig
import com.se3092.pulsefinance.ui.theme.OnAccent
import com.se3092.pulsefinance.ui.theme.NeoBorder
import com.se3092.pulsefinance.ui.theme.NeoCard
import com.se3092.pulsefinance.ui.theme.NeoCardLow
import com.se3092.pulsefinance.ui.theme.NeoContainerHigh
import com.se3092.pulsefinance.ui.theme.NeoGreen
import com.se3092.pulsefinance.ui.theme.NeoGreenDark
import com.se3092.pulsefinance.ui.theme.NeoGreenGlow
import com.se3092.pulsefinance.ui.theme.NeoRose
import com.se3092.pulsefinance.ui.theme.NeoSurface
import com.se3092.pulsefinance.ui.theme.NeoTextPrimary
import com.se3092.pulsefinance.ui.theme.NeoTextTertiary
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onAuthSuccess: (needsSetup: Boolean) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    var showCustomCredentials by remember { mutableStateOf(false) }
    val context = LocalContext.current

    fun openCredentials(isLogin: Boolean) {
        viewModel.setLoginMode(isLogin)
        showCustomCredentials = true
    }

    fun startGoogleSignIn() {
        coroutineScope.launch {
            GoogleIdTokenRequester.request(context).fold(
                onSuccess = { token -> token?.let(viewModel::onGoogleIdToken) },
                onFailure = viewModel::onGoogleSignInFailed
            )
        }
    }

    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) {
            onAuthSuccess(state.needsSetup)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NeoSurface)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // =========================================================================
            // Top Navigation Bar & 3-Segment Progress Indicator Track
            // =========================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 3-Segment Progress Track
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { index ->
                        val isCurrent = pagerState.currentPage == index
                        val isPassed = pagerState.currentPage > index

                        val width by animateDpAsState(
                            targetValue = if (isCurrent) 38.dp else 22.dp,
                            animationSpec = tween(300),
                            label = "segmentWidth"
                        )
                        val color by animateColorAsState(
                            targetValue = if (isCurrent || isPassed) NeoGreen else NeoContainerHigh,
                            animationSpec = tween(300),
                            label = "segmentColor"
                        )

                        Box(
                            modifier = Modifier
                                .width(width)
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                        )
                    }
                }

                // Step Counter & Skip Action Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (pagerState.currentPage == 2) {
                        Text(
                            text = "Step 3 of 3",
                            fontSize = 11.5.sp,
                            color = NeoTextTertiary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = if (pagerState.currentPage == 2) NeoCardLow else Color.Transparent,
                        modifier = Modifier.clickable {
                            // Skip directly to last step or submit
                            if (pagerState.currentPage < 2) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(2)
                                }
                            } else {
                                openCredentials(isLogin = false)
                            }
                        }
                    ) {
                        Text(
                            text = "Skip",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeoTextTertiary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // =========================================================================
            // Multi-Page Onboarding Showcase Area
            // =========================================================================
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                when (page) {
                    0 -> OnboardingStep1()
                    1 -> OnboardingStep2()
                    2 -> OnboardingStep3()
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // =========================================================================
            // Error Message Banner (if any)
            // =========================================================================
            state.errorMessage?.let { error ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NeoRose.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, NeoRose.copy(alpha = 0.25f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text(
                        text = error,
                        color = NeoRose,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            // =========================================================================
            // Expandable Custom Credentials Drawer
            // =========================================================================
            AnimatedVisibility(
                visible = showCustomCredentials,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(24.dp),
                            ambientColor = Color(0x1F0F172A),
                            spotColor = Color(0x1F0F172A)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    color = NeoCard,
                    border = BorderStroke(1.dp, NeoBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (state.isLoginMode) "Sign In with Account" else "Create New Account",
                            style = MaterialTheme.typography.titleMedium,
                            color = NeoTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = state.email,
                            onValueChange = viewModel::onEmailChanged,
                            label = { Text("Email", fontSize = 12.5.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeoGreen,
                                unfocusedBorderColor = NeoBorder,
                                focusedTextColor = NeoTextPrimary,
                                unfocusedTextColor = NeoTextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.password,
                            onValueChange = viewModel::onPasswordChanged,
                            label = { Text("Password", fontSize = 12.5.sp) },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeoGreen,
                                unfocusedBorderColor = NeoBorder,
                                focusedTextColor = NeoTextPrimary,
                                unfocusedTextColor = NeoTextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        TextButton(onClick = viewModel::toggleAuthMode) {
                            Text(
                                text = if (state.isLoginMode)
                                    "Need a new account? Switch to Register"
                                else
                                    "Already registered? Switch to Login",
                                color = NeoGreenDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            if (BuildConfig.DEBUG && BuildConfig.DEMO_EMAIL.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = NeoCardLow,
                    border = BorderStroke(1.dp, NeoGreen.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clickable {
                            viewModel.fillDemoCredentials(BuildConfig.DEMO_EMAIL, BuildConfig.DEMO_PASSWORD)
                            showCustomCredentials = true
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "🧪 Demo Drawer (Debug Only)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeoGreenDark
                            )
                            Text(
                                text = "Kavindu (Sri Lanka) • ${BuildConfig.DEMO_EMAIL}",
                                fontSize = 11.sp,
                                color = NeoTextTertiary
                            )
                        }
                        Text(
                            text = "Auto Fill",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeoGreenDark
                        )
                    }
                }
            }

            // =========================================================================
            // Primary Action CTA Full Pill Button
            // =========================================================================
            Button(
                onClick = {
                    when {
                        showCustomCredentials -> viewModel.submit()
                        pagerState.currentPage < 2 -> coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                        else -> openCredentials(isLogin = false)
                    }
                },
                enabled = !state.isLoading,
                shape = CircleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = CircleShape,
                        spotColor = NeoGreenGlow
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeoGreen,
                    contentColor = OnAccent
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = OnAccent,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when {
                                showCustomCredentials && state.isLoginMode -> "Sign In"
                                showCustomCredentials && !state.isLoginMode -> "Create Account"
                                pagerState.currentPage == 2 -> "Get Started Now"
                                else -> "Continue"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Same size and shape as the primary CTA so the two read as equal choices
            Button(
                onClick = { startGoogleSignIn() },
                enabled = !state.isLoading,
                shape = CircleShape,
                border = BorderStroke(1.dp, NeoBorder),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = NeoTextPrimary,
                    disabledContainerColor = Color.White.copy(alpha = 0.6f)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_google_logo),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Continue with Google",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = NeoTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sub-Action: Toggle credentials / Login switch
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account?",
                    fontSize = 13.5.sp,
                    color = NeoTextTertiary
                )
                Text(
                    text = if (showCustomCredentials) "Hide form" else "Sign in",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeoGreenDark,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            if (showCustomCredentials) showCustomCredentials = false else openCredentials(isLogin = true)
                        }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
