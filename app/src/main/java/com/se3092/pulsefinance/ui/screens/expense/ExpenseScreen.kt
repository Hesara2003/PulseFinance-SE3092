package com.se3092.pulsefinance.ui.screens.expense

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HistoryToggleOff
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.se3092.pulsefinance.ui.theme.NeoBorder
import com.se3092.pulsefinance.ui.theme.NeoCard
import com.se3092.pulsefinance.ui.theme.NeoCardLow
import com.se3092.pulsefinance.ui.theme.NeoContainer
import com.se3092.pulsefinance.ui.theme.NeoContainerHigh
import com.se3092.pulsefinance.ui.theme.NeoErrorContainer
import com.se3092.pulsefinance.ui.theme.NeoGreen
import com.se3092.pulsefinance.ui.theme.NeoGreenDark
import com.se3092.pulsefinance.ui.theme.NeoGreenFixed
import com.se3092.pulsefinance.ui.theme.NeoGreenLight
import com.se3092.pulsefinance.ui.theme.NeoInverseOnSurface
import com.se3092.pulsefinance.ui.theme.NeoInverseSurface
import com.se3092.pulsefinance.ui.theme.NeoOnPrimaryFixed
import com.se3092.pulsefinance.ui.theme.NeoOnSecondaryFixed
import com.se3092.pulsefinance.ui.theme.NeoRose
import com.se3092.pulsefinance.ui.theme.NeoSecondaryContainer
import com.se3092.pulsefinance.ui.theme.NeoSurface
import com.se3092.pulsefinance.ui.theme.NeoTextPrimary
import com.se3092.pulsefinance.ui.theme.NeoTextSecondary
import com.se3092.pulsefinance.R
import com.se3092.pulsefinance.ui.components.EmptyStateView
import com.se3092.pulsefinance.ui.util.Formatters
import androidx.compose.material3.TextButton
import kotlinx.coroutines.delay

@Composable
fun ExpenseScreen(
    viewModel: ExpenseViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Auto-dismiss toast
    LaunchedEffect(state.toastMessage) {
        if (state.toastMessage != null) {
            delay(2800)
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
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(NeoGreenDark, NeoGreen)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = "PulseFinance Emblem",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "PulseFinance",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NeoTextPrimary,
                        fontSize = 17.sp,
                        letterSpacing = (-0.2).sp
                    )
                }

                // Zombie Subscription Audit label & User Profile Avatar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Zombie Subscription Audit",
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
                            Text(
                                text = state.userInitials,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 12.sp
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
                // 1. Audit Intelligence Title & Auto Mode Icon
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "AUDIT INTELLIGENCE",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeoGreenDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Zombie Subscriptions",
                                style = MaterialTheme.typography.headlineMedium,
                                color = NeoTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Recurring auto-debits on secondary accounts",
                                style = MaterialTheme.typography.bodyMedium,
                                color = NeoTextSecondary,
                                fontSize = 14.sp
                            )
                        }

                        // Auto Mode Glyph Container
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(NeoContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoMode,
                                contentDescription = "Audit Engine",
                                tint = NeoTextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // 2. Red Alert Warning Banner
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = NeoErrorContainer
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Pulsing Warning Icon Container
                            val infiniteTransition = rememberInfiniteTransition(label = "pingWarning")
                            val pingScale by infiniteTransition.animateFloat(
                                initialValue = 1.0f,
                                targetValue = 1.35f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(800),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "scale"
                            )

                            Box(modifier = Modifier.size(36.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .align(Alignment.Center)
                                        .clip(CircleShape)
                                        .background(NeoRose),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Warning",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .scale(pingScale)
                                        .align(Alignment.TopEnd)
                                        .clip(CircleShape)
                                        .background(NeoRose)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (state.activeFlaggedCount == 1) "1 Subscription Flagged"
                                    else "${state.activeFlaggedCount} Subscriptions Flagged",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF93000A),
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = buildAnnotatedString {
                                        append("Draining ")
                                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append(state.totalDrainMonthlyFormatted)
                                        }
                                        append(" through auto-debits you haven't reviewed yet.")
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF93000A).copy(alpha = 0.9f),
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                // 3. Grid of 2 Metric Cards: Total Annual Drain & Potential Savings
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Metric Card 1: Total Annual Drain
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(
                                    elevation = 6.dp,
                                    shape = RoundedCornerShape(20.dp),
                                    ambientColor = Color(0x060F172A),
                                    spotColor = Color(0x060F172A)
                                ),
                            shape = RoundedCornerShape(20.dp),
                            color = NeoCard,
                            border = BorderStroke(1.dp, NeoBorder)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                                        contentDescription = null,
                                        tint = NeoRose,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "TOTAL ANNUAL DRAIN",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeoTextSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = state.totalAnnualDrainDisplay,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NeoTextPrimary,
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Accumulated silently",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeoTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Metric Card 2: Potential Savings
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(
                                    elevation = 6.dp,
                                    shape = RoundedCornerShape(20.dp),
                                    ambientColor = Color(0x060F172A),
                                    spotColor = Color(0x060F172A)
                                ),
                            shape = RoundedCornerShape(20.dp),
                            color = NeoCard,
                            border = BorderStroke(1.dp, NeoBorder)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RocketLaunch,
                                        contentDescription = null,
                                        tint = NeoGreenDark,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "POTENTIAL SAVINGS",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeoTextSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = state.potentialSavingsPct,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NeoGreenDark,
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "of MacBook M4 Goal",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeoTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // 4. Detected Outflows Section Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Detected Outflows",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = NeoTextPrimary,
                            fontSize = 17.sp
                        )

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(NeoContainerHigh)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Sorted by risk",
                                fontSize = 11.sp,
                                color = NeoTextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // 5. Zombie Subscription Cards List
                if (!state.hasDetections) {
                    item {
                        EmptyStateView(
                            title = "No recurring debits yet",
                            message = "Log subscriptions or repeat bills and PulseFinance will flag the ones quietly draining your accounts.",
                            illustration = R.drawable.illustration_insights
                        )
                    }
                }
                items(
                    items = state.zombieSubscriptions.filter { !it.isDismissed },
                    key = { it.id }
                ) { sub ->
                    ZombieCardItem(
                        sub = sub,
                        onAction = { viewModel.dismissZombieSubscription(sub.id) },
                        onKeep = { viewModel.keepSubscription(sub.id) }
                    )
                }

                // 6. Action Button: Nuke Flagged Leaks
                item {
                    Button(
                        onClick = { viewModel.nukeAllFlagged() },
                        enabled = !state.isAllFlaggedNuked,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = CircleShape,
                                spotColor = Color(0x30000000)
                            ),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (state.isAllFlaggedNuked) NeoInverseSurface.copy(alpha = 0.5f) else NeoInverseSurface,
                            contentColor = NeoInverseOnSurface,
                            disabledContainerColor = NeoInverseSurface.copy(alpha = 0.4f),
                            disabledContentColor = NeoInverseOnSurface.copy(alpha = 0.7f)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (state.isAllFlaggedNuked) {
                                Icon(
                                    imageVector = Icons.Default.DoneAll,
                                    contentDescription = null,
                                    tint = NeoGreenLight,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = if (state.hasDetections) "All Leaks Plugged" else "Nothing to Nuke",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = NeoGreenLight,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = buildAnnotatedString {
                                        append(
                                            if (state.activeFlaggedCount == 1) "Nuke 1 Flagged Leak "
                                            else "Nuke ${state.activeFlaggedCount} Flagged Leaks "
                                        )
                                        withStyle(SpanStyle(color = NeoGreenLight)) {
                                            append("(Save ${Formatters.lkr(state.flaggedMonthlyDrainLkr)}/mo)")
                                        }
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
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
                shadowElevation = 14.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Success",
                        tint = NeoGreenFixed,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = state.toastMessage ?: "Subscription blocked successfully!",
                        color = NeoInverseOnSurface,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ZombieCardItem(
    sub: ZombieSubscription,
    onAction: () -> Unit,
    onKeep: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = Color(0x080F172A),
                spotColor = Color(0x080F172A)
            )
            .animateContentSize(),
        shape = RoundedCornerShape(22.dp),
        color = NeoCard,
        border = BorderStroke(1.dp, NeoBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header Row: Icon, Title/Subtitle, and Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Category / Risk Glyphs Container
                    val (iconBg, iconTint, iconVec) = when (sub.iconType) {
                        "fitness" -> Triple(NeoErrorContainer.copy(alpha = 0.6f), NeoRose, Icons.Default.FitnessCenter)
                        "vps" -> Triple(NeoSecondaryContainer, NeoOnSecondaryFixed, Icons.Default.CloudOff)
                        else -> Triple(NeoGreenFixed, NeoOnPrimaryFixed, Icons.Default.SmartToy)
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(iconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = iconVec,
                            contentDescription = sub.title,
                            tint = iconTint,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = sub.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NeoTextPrimary,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = sub.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = NeoTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                // Risk Tag Chip
                val (tagBg, tagText, tagDot) = when (sub.iconType) {
                    "fitness" -> Triple(NeoErrorContainer, Color(0xFF93000A), NeoRose)
                    "vps" -> Triple(NeoSecondaryContainer, NeoOnSecondaryFixed, NeoTextSecondary)
                    else -> Triple(NeoGreenFixed, NeoOnPrimaryFixed, NeoGreenDark)
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(tagBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(tagDot)
                        )
                        Text(
                            text = sub.tag,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = tagText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Usage Metric Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = NeoCardLow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val (metricIcon, metricTint) = when (sub.iconType) {
                        "fitness" -> Pair(Icons.Default.HistoryToggleOff, NeoRose)
                        "vps" -> Pair(Icons.Default.Speed, NeoTextSecondary)
                        else -> Pair(Icons.Default.Bolt, NeoGreenDark)
                    }

                    Icon(
                        imageVector = metricIcon,
                        contentDescription = null,
                        tint = metricTint,
                        modifier = Modifier.size(16.dp)
                    )

                    Text(
                        text = buildAnnotatedString {
                            append(sub.usageStatText)
                            withStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    color = if (sub.iconType == "fitness") NeoRose else if (sub.iconType == "ai") NeoGreenDark else NeoTextPrimary
                                )
                            ) {
                                append(sub.usageHighlight)
                            }
                        },
                        fontSize = 12.sp,
                        color = NeoTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Drain Rate & Action Button Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DRAIN RATE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeoTextSecondary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = NeoTextPrimary, fontSize = 16.sp)) {
                                append(sub.drainRateDisplay)
                            }
                            withStyle(SpanStyle(color = NeoTextSecondary, fontSize = 12.sp)) {
                                append(sub.drainRateSub)
                            }
                        }
                    )
                }

                if (sub.isFlagged) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onKeep) {
                        Text("Keep", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = NeoTextSecondary)
                    }
                    Button(
                        onClick = onAction,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeoRose,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val actionIcon = if (sub.iconType == "fitness") Icons.Default.Cancel else Icons.Default.DeleteForever
                            Icon(
                                imageVector = actionIcon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = sub.actionLabel,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                  }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(NeoContainer)
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = NeoGreenDark,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = sub.actionLabel,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NeoTextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
