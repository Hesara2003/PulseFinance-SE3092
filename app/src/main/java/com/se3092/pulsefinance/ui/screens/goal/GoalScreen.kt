package com.se3092.pulsefinance.ui.screens.goal

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CancelPresentation
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.se3092.pulsefinance.R
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.se3092.pulsefinance.domain.model.SavingsGoal
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.se3092.pulsefinance.domain.usecase.GoalVelocityReport
import com.se3092.pulsefinance.ui.common.expenseCategoryVisual
import com.se3092.pulsefinance.ui.common.formatDate
import com.se3092.pulsefinance.ui.common.lkr
import com.se3092.pulsefinance.ui.theme.NeoRose
import com.se3092.pulsefinance.ui.components.MacBookGoalCard
import com.se3092.pulsefinance.ui.theme.NeoBorder
import com.se3092.pulsefinance.ui.theme.NeoCard
import com.se3092.pulsefinance.ui.theme.NeoCardLow
import com.se3092.pulsefinance.ui.theme.NeoContainer
import com.se3092.pulsefinance.ui.theme.NeoContainerHigh
import com.se3092.pulsefinance.ui.theme.NeoGreen
import com.se3092.pulsefinance.ui.theme.NeoGreenDark
import com.se3092.pulsefinance.ui.theme.NeoGreenFixed
import com.se3092.pulsefinance.ui.theme.NeoGreenGlow
import com.se3092.pulsefinance.ui.theme.NeoInverseOnSurface
import com.se3092.pulsefinance.ui.theme.NeoInverseSurface
import com.se3092.pulsefinance.ui.theme.NeoOnPrimaryFixed
import com.se3092.pulsefinance.ui.theme.NeoPrimaryFixed
import com.se3092.pulsefinance.ui.theme.NeoSurface
import com.se3092.pulsefinance.ui.theme.NeoTextPrimary
import com.se3092.pulsefinance.ui.theme.NeoTextSecondary
import com.se3092.pulsefinance.ui.theme.NeoTextTertiary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalScreen(
    viewModel: GoalViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Auto-dismiss toast
    LaunchedEffect(state.toastMessage) {
        if (state.toastMessage != null) {
            delay(2400)
            viewModel.dismissToast()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NeoSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // =========================================================================
            // Top Navigation & Branding Header (Exact HTML replica)
            // =========================================================================
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // PulseFinance Emblem & Brand Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.logo_mark),
                        contentDescription = "PulseFinance logo",
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = "PulseFinance",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NeoTextPrimary,
                        fontSize = 17.sp,
                        letterSpacing = (-0.2).sp
                    )
                }

                // Goal Velocity Engine label & User Profile Avatar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Goal Velocity Engine",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = NeoTextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Profile Avatar Circle
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(NeoContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(NeoGreenDark, NeoGreen)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // =========================================================================
            // Main Content Scrollable
            // =========================================================================
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // 1. Goal Velocity Header & Pacing Active Pill
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Goal Velocity",
                                style = MaterialTheme.typography.headlineLarge,
                                color = NeoTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp,
                                letterSpacing = (-0.5).sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Hardware & Life Milestones",
                                style = MaterialTheme.typography.bodyMedium,
                                color = NeoTextSecondary,
                                fontSize = 14.sp
                            )
                        }

                        // Pacing Active Pill with Pulsing Green Dot
                        val infiniteTransition = rememberInfiniteTransition(label = "pulseDot")
                        val dotScale by infiniteTransition.animateFloat(
                            initialValue = 0.85f,
                            targetValue = 1.25f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(900),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "scale"
                        )
                        val dotAlpha by infiniteTransition.animateFloat(
                            initialValue = 0.6f,
                            targetValue = 1.0f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(900),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "alpha"
                        )

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(NeoContainerHigh)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .scale(dotScale)
                                        .alpha(dotAlpha)
                                        .clip(CircleShape)
                                        .background(NeoGreen)
                                )
                                Text(
                                    text = when {
                                        state.velocityReport == null -> "No goal yet"
                                        state.velocityReport!!.isPacingOnTrack -> "On pace"
                                        else -> "Behind pace"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NeoTextPrimary
                                )
                            }
                        }
                    }
                }

                // 2. Goal Hero Card (or first-time setup)
                item {
                    val goal = state.goal
                    if (goal != null) {
                        MacBookGoalCard(
                            goal = goal,
                            onMoreClick = { viewModel.openGoalEditor() }
                        )
                    } else if (!state.isLoading) {
                        GoalSetupCard(onCreate = viewModel::openGoalEditor)
                    }
                }

                // 2b. Actual savings rate vs required pace
                state.velocityReport?.let { report ->
                    item { PacingCard(report) }
                }

                // 3. Opportunity Cost Translator Header
                if (state.goal != null) item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Opportunity Cost",
                                tint = NeoGreenDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Opportunity Cost Translator",
                                style = MaterialTheme.typography.titleMedium,
                                color = NeoTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                letterSpacing = (-0.2).sp
                            )
                        }
                        Text(
                            text = "Live Impact",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeoTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // 4. Opportunity cost, priced from the user's own discretionary habits
                if (state.goal != null) item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (state.opportunities.isEmpty()) {
                            Text(
                                text = "Log a few discretionary expenses and this shows which habit, trimmed by a quarter, moves your goal closest.",
                                fontSize = 12.5.sp,
                                color = NeoTextSecondary,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                        state.opportunities.forEach { opportunity ->
                            OpportunityCostCard(
                                icon = expenseCategoryVisual(opportunity.category).icon,
                                title = "Trim ${opportunity.category.displayName} by 25%",
                                subtitle = "${lkr(opportunity.monthlySpendLkr)} last 30 days · saves ${lkr(opportunity.monthlySavingLkr)}/mo",
                                badgeText = "+${"%.1f".format(opportunity.daysOfGoalPace)} days",
                                onClick = { viewModel.openAllocateWith(opportunity.monthlySavingLkr) }
                            )
                        }
                    }
                }

                // 5. Arrival Projection Box
                state.velocityReport?.let { report -> item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = NeoCardLow,
                        border = BorderStroke(1.dp, NeoBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(NeoGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "Target Date",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "ARRIVAL PROJECTION",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeoTextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Deadline: ${formatDate(report.goal.targetDateMillis, "dd MMMM yyyy")}",
                                    fontSize = 12.sp,
                                    color = NeoTextSecondary
                                )
                                Text(
                                    text = report.projectedArrivalMillis?.let { "At your pace: ${formatDate(it, "dd MMMM yyyy")}" }
                                        ?: "At your pace: not reachable yet",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = NeoTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }

                }

                // 6. Action Button: Allocate Funds to Goal
                if (state.goal != null) item {
                    Button(
                        onClick = {
                            viewModel.setAllocateSheetVisible(true)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = CircleShape,
                                spotColor = NeoGreenGlow
                            ),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeoGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddCircle,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "Set Money Aside",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp)) // Clearance for bottom dock
                }
            }
        }

        // =============================================================================
        // Top Floating Toast Notification (Exact HTML prototype replica)
        // =============================================================================
        AnimatedVisibility(
            visible = state.toastMessage != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 70.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = NeoInverseSurface,
                shadowElevation = 12.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = NeoGreenFixed,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = state.toastMessage ?: "LKR allocation window ready",
                        color = NeoInverseOnSurface,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // =============================================================================
        // Interactive Allocation Bottom Sheet
        // =============================================================================
        if (state.showAllocateSheet) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.setAllocateSheetVisible(false) },
                sheetState = sheetState,
                containerColor = NeoCard,
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .size(width = 36.dp, height = 4.dp)
                            .clip(CircleShape)
                            .background(NeoBorder)
                    )
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 36.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Allocate Funds",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = NeoTextPrimary,
                                fontSize = 20.sp
                            )
                            Text(
                                text = "Add to ${state.goal?.title ?: "your goal"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = NeoTextSecondary,
                                fontSize = 13.sp
                            )
                        }

                        IconButton(
                            onClick = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    viewModel.setAllocateSheetVisible(false)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = NeoTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Quick Preset Chips
                    Text(
                        text = "QUICK ALLOCATION PRESETS",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeoTextTertiary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PresetChip(
                            label = "+2,500",
                            sublabel = "Small",
                            onClick = { viewModel.onDepositAmountChanged("2500") },
                            modifier = Modifier.weight(1f)
                        )
                        PresetChip(
                            label = "+5,000",
                            sublabel = "Medium",
                            onClick = { viewModel.onDepositAmountChanged("5000") },
                            modifier = Modifier.weight(1f)
                        )
                        PresetChip(
                            label = "+10,000",
                            sublabel = "Boost",
                            onClick = { viewModel.onDepositAmountChanged("10000") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Input Field
                    OutlinedTextField(
                        value = state.depositAmountInput,
                        onValueChange = viewModel::onDepositAmountChanged,
                        label = { Text("Allocation Amount", fontSize = 13.sp) },
                        prefix = { Text("LKR ", color = NeoGreenDark, fontWeight = FontWeight.Bold) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeoGreen,
                            unfocusedBorderColor = NeoBorder,
                            focusedTextColor = NeoTextPrimary,
                            unfocusedTextColor = NeoTextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Impact Preview
                    val inputAmount = state.depositAmountInput.toDoubleOrNull() ?: 0.0
                    if (inputAmount > 0) {
                        val dailyPace = state.goal?.requiredDailySavingsLkr?.takeIf { it > 0 } ?: inputAmount
                        val days = inputAmount / dailyPace
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(NeoCardLow)
                                .padding(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = NeoGreenDark,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Covers ~${String.format("%.1f", days)} days of your required pace",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NeoGreenDark
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Confirm Allocation Button
                    Button(
                        onClick = {
                            viewModel.addSavingsToGoal()
                        },
                        enabled = state.depositAmountInput.isNotBlank() && !state.isUpdating,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeoGreen,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .shadow(10.dp, CircleShape, spotColor = NeoGreenGlow)
                    ) {
                        Text(
                            text = if (state.isUpdating) "Allocating..." else "Confirm Allocation",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    
        state.celebration?.let { celebration ->
            CelebrationDialog(celebration = celebration, onDismiss = viewModel::dismissCelebration)
        }

        if (state.editor.isVisible) {
            GoalEditorSheet(
                editor = state.editor,
                onChange = viewModel::onEditorChanged,
                onSave = viewModel::saveGoal,
                onDismiss = viewModel::dismissGoalEditor
            )
        }
    }
}

@Composable
private fun OpportunityCostCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badgeText: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = Color(0x080F172A),
                spotColor = Color(0x080F172A)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = NeoCard,
        border = BorderStroke(1.dp, NeoBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Category Icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(NeoCardLow),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = NeoTextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = NeoTextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = NeoTextSecondary,
                        fontSize = 12.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Days Saved / Speed Boost Pill
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(NeoPrimaryFixed)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = badgeText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeoOnPrimaryFixed
                )
            }
        }
    }
}

@Composable
private fun PresetChip(
    label: String,
    sublabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = NeoCardLow,
        border = BorderStroke(1.dp, NeoBorder)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = NeoTextPrimary
            )
            Text(
                text = sublabel,
                fontSize = 10.sp,
                color = NeoTextSecondary
            )
        }
    }
}

@Composable
private fun GoalSetupCard(onCreate: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = NeoCard,
        border = BorderStroke(1.dp, NeoBorder)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.illustration_piggy_bank),
                contentDescription = null,
                modifier = Modifier
                    .size(180.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(text = "What are you saving for?", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeoTextPrimary)
            Text(
                text = "Name it, price it and pick a deadline. PulseFinance then shows the exact monthly pace, " +
                    "compares it to what you actually save, and projects when you'll get there.",
                fontSize = 13.sp,
                color = NeoTextSecondary
            )
            Button(
                onClick = onCreate,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = NeoGreen, contentColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Set my goal", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PacingCard(report: GoalVelocityReport) {
    val accent = if (report.isPacingOnTrack) NeoGreenDark else NeoRose
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = NeoCard,
        border = BorderStroke(1.dp, NeoBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PaceStat(
                    label = "YOU SAVE",
                    value = "${lkr(report.actualMonthlySavingsLkr)}/mo",
                    color = accent,
                    modifier = Modifier.weight(1f)
                )
                PaceStat(
                    label = "NEEDED",
                    value = "${lkr(report.monthlyPacingTargetLkr)}/mo",
                    color = NeoTextPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
            // A visible reaction makes the pace land harder than a number alone
            Image(
                painter = painterResource(
                    if (report.isPacingOnTrack) R.drawable.illustration_success else R.drawable.illustration_stressed
                ),
                contentDescription = if (report.isPacingOnTrack) "People celebrating with a trophy" else "Worried person surrounded by bills",
                modifier = Modifier
                    .size(170.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(text = report.behavioralInsight, fontSize = 12.5.sp, color = NeoTextSecondary)
            Text(
                text = "Savings rate = received income minus spending, averaged over your last 90 days.",
                fontSize = 10.5.sp,
                color = NeoTextTertiary
            )
        }
    }
}

@Composable
private fun PaceStat(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(NeoCardLow)
            .padding(12.dp)
    ) {
        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeoTextSecondary, letterSpacing = 0.5.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = color)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoalEditorSheet(
    editor: GoalEditorState,
    onChange: ((GoalEditorState) -> GoalEditorState) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = NeoGreen,
        unfocusedBorderColor = NeoBorder,
        focusedTextColor = NeoTextPrimary,
        unfocusedTextColor = NeoTextPrimary
    )
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = NeoCard
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = if (editor.isNew) "Set your goal" else "Edit goal",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = NeoTextPrimary
            )
            OutlinedTextField(
                value = editor.titleInput,
                onValueChange = { v -> onChange { it.copy(titleInput = v) } },
                label = { Text("Goal (e.g. MacBook Pro M4)") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )
            OutlinedTextField(
                value = editor.targetInput,
                onValueChange = { v -> onChange { it.copy(targetInput = v) } },
                label = { Text("Target amount") },
                prefix = { Text("LKR ", color = NeoGreenDark, fontWeight = FontWeight.Bold) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )
            OutlinedTextField(
                value = editor.savedInput,
                onValueChange = { v -> onChange { it.copy(savedInput = v) } },
                label = { Text("Already saved") },
                prefix = { Text("LKR ", color = NeoGreenDark, fontWeight = FontWeight.Bold) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )
            OutlinedTextField(
                value = editor.monthsInput,
                onValueChange = { v -> onChange { it.copy(monthsInput = v) } },
                label = { Text("Reach it within (months)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )
            OutlinedTextField(
                value = editor.notesInput,
                onValueChange = { v -> onChange { it.copy(notesInput = v) } },
                label = { Text("Why it matters (optional)") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )
            editor.errorMessage?.let { Text(text = it, color = NeoRose, fontSize = 12.sp) }
            Button(
                onClick = onSave,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = NeoGreen, contentColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(text = if (editor.isNew) "Create goal" else "Save changes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun CelebrationDialog(celebration: Celebration, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(28.dp), color = NeoCard) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.illustration_success),
                    contentDescription = null,
                    modifier = Modifier.size(220.dp)
                )
                Text(
                    text = celebration.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeoTextPrimary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = celebration.message,
                    fontSize = 14.sp,
                    color = NeoTextSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = onDismiss,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = NeoGreen, contentColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = "Keep going", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
