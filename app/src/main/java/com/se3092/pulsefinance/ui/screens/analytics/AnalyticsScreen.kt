package com.se3092.pulsefinance.ui.screens.analytics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.se3092.pulsefinance.R
import com.se3092.pulsefinance.ui.components.EmptyStateView
import com.se3092.pulsefinance.ui.util.Formatters
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.se3092.pulsefinance.ui.theme.NeoBorder
import com.se3092.pulsefinance.ui.theme.NeoCard
import com.se3092.pulsefinance.ui.theme.NeoCardLow
import com.se3092.pulsefinance.ui.theme.NeoContainer
import com.se3092.pulsefinance.ui.theme.NeoErrorContainer
import com.se3092.pulsefinance.ui.theme.NeoGreen
import com.se3092.pulsefinance.ui.theme.NeoGreenDark
import com.se3092.pulsefinance.ui.theme.NeoGreenGlow
import com.se3092.pulsefinance.ui.theme.NeoRose
import com.se3092.pulsefinance.ui.theme.NeoSecondaryContainer
import com.se3092.pulsefinance.ui.theme.NeoSurface
import com.se3092.pulsefinance.ui.theme.NeoTextPrimary
import com.se3092.pulsefinance.ui.theme.NeoTextSecondary

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

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

                // Financial Analytics & Clarity label & User Profile Avatar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Financial Analytics & Clarity",
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
                // 1. Segmented Control: Weekly / Monthly
                item {
                    Surface(
                        shape = CircleShape,
                        color = NeoContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val isWeekly = state.selectedInterval == "weekly"

                            // Weekly Button
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(CircleShape)
                                    .then(
                                        if (isWeekly) {
                                            Modifier
                                                .shadow(6.dp, CircleShape, spotColor = NeoGreenGlow)
                                                .background(NeoGreen)
                                        } else {
                                            Modifier.background(Color.Transparent)
                                        }
                                    )
                                    .clickable { viewModel.switchInterval("weekly") }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Weekly",
                                    fontWeight = if (isWeekly) FontWeight.Bold else FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = if (isWeekly) Color.White else NeoTextSecondary
                                )
                            }

                            // Monthly Button
                            val isMonthly = state.selectedInterval == "monthly"
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(CircleShape)
                                    .then(
                                        if (isMonthly) {
                                            Modifier
                                                .shadow(6.dp, CircleShape, spotColor = NeoGreenGlow)
                                                .background(NeoGreen)
                                        } else {
                                            Modifier.background(Color.Transparent)
                                        }
                                    )
                                    .clickable { viewModel.switchInterval("monthly") }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Monthly",
                                    fontWeight = if (isMonthly) FontWeight.Bold else FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = if (isMonthly) Color.White else NeoTextSecondary
                                )
                            }
                        }
                    }
                }

                // 2. Analytics Clarity Card with Capsule Bar Chart
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(24.dp),
                                ambientColor = Color(0x0A0F172A),
                                spotColor = Color(0x0A0F172A)
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
                            // Date Range Carousel Control
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(NeoCardLow)
                                        .clickable { viewModel.shiftPeriod(-1) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChevronLeft,
                                        contentDescription = "Previous Period",
                                        tint = NeoTextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(20.dp))

                                Text(
                                    text = state.currentPeriod,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = NeoTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )

                                Spacer(modifier = Modifier.width(20.dp))

                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(NeoCardLow)
                                        .clickable { viewModel.shiftPeriod(1) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = "Next Period",
                                        tint = NeoTextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Total Financial Metric Breakdown
                            Text(
                                text = state.heroAmountFormatted,
                                style = MaterialTheme.typography.headlineLarge,
                                color = NeoTextPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 28.sp,
                                letterSpacing = (-0.5).sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Committed vs Discretionary total",
                                style = MaterialTheme.typography.bodySmall,
                                color = NeoTextSecondary,
                                fontSize = 13.sp
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Capsule Bar Chart (7-day timeline)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                state.timelineDays.forEachIndexed { index, day ->
                                    val isSelected = index == state.selectedBarIndex
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .clickable { viewModel.selectBar(index) }
                                            .padding(horizontal = 2.dp)
                                    ) {
                                        // Vertical Capsule Container (18dp x 144dp)
                                        Box(
                                            modifier = Modifier
                                                .width(18.dp)
                                                .height(144.dp)
                                                .clip(CircleShape)
                                                .background(NeoContainer)
                                                .padding(2.dp),
                                            contentAlignment = Alignment.BottomCenter
                                        ) {
                                            // Filled Bar Portion
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .fillMaxHeight(day.fillRatio)
                                                    .clip(CircleShape)
                                                    .then(
                                                        if (isSelected) {
                                                            Modifier
                                                                .shadow(8.dp, CircleShape, spotColor = NeoGreen)
                                                                .background(NeoGreen)
                                                        } else {
                                                            Modifier.background(NeoGreen)
                                                        }
                                                    )
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Day number (e.g. 17, 18, 23)
                                        Text(
                                            text = day.dayNumber,
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                            color = if (isSelected) NeoGreenDark else NeoTextPrimary
                                        )
                                        // Day of week (e.g. Sun, Mon)
                                        Text(
                                            text = day.dayOfWeek,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) NeoGreenDark else NeoTextSecondary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Capsule Selected Stat Popover Note
                            val infiniteTransition = rememberInfiniteTransition(label = "peakPulse")
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

                            val selectedDay = state.selectedDayStat
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = NeoCardLow,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .scale(dotScale)
                                                .alpha(dotAlpha)
                                                .clip(CircleShape)
                                                .background(NeoGreen)
                                        )
                                        val labelText = if (selectedDay.isPeak) {
                                            "Peak Outflow Day: ${selectedDay.dayNumber} ${selectedDay.dayOfWeek}"
                                        } else {
                                            "Daily Outflow: ${selectedDay.dayNumber} ${selectedDay.dayOfWeek}"
                                        }
                                        Text(
                                            text = labelText,
                                            fontSize = 12.sp,
                                            color = NeoTextSecondary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    Text(
                                        text = Formatters.lkr(selectedDay.amountLkr, decimals = 2),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeoTextPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Category Breakdown for the selected period
                if (state.categoryBreakdown.isNotEmpty()) {
                    item {
                        CategoryBreakdownCard(slices = state.categoryBreakdown)
                    }
                }

                // 4. Recent Transactions Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Transactions",
                            style = MaterialTheme.typography.titleMedium,
                            color = NeoTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Text(
                            text = if (state.showAllTransactions) "Show less" else "View all",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeoGreenDark,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.toggleViewAll() }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                // 4. Recent Transactions Ledger Stack
                val visibleTransactions = if (state.showAllTransactions) {
                    state.transactions
                } else {
                    state.transactions.take(3)
                }

                if (state.transactions.isEmpty()) {
                    item {
                        EmptyStateView(
                            title = "Nothing logged in this period",
                            message = "Switch period or log expenses from the Cockpit to see your spending patterns.",
                            illustration = R.drawable.illustration_insights
                        )
                    }
                }

                items(visibleTransactions, key = { it.id }) { tx ->
                    TransactionLedgerCard(tx = tx)
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp)) // Clearance for bottom dock
                }
            }
        }
    }
}

@Composable
private fun TransactionLedgerCard(
    tx: com.se3092.pulsefinance.ui.screens.analytics.TransactionDisplayItem
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color(0x060F172A),
                spotColor = Color(0x060F172A)
            ),
        shape = RoundedCornerShape(16.dp),
        color = NeoCard,
        border = BorderStroke(1.dp, NeoBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Category / Type Glyphs Container
                val bgColor = if (tx.isZombie) NeoErrorContainer else tx.visual.container
                val iconTint = if (tx.isZombie) NeoRose else tx.visual.tint
                val iconVector = tx.visual.icon

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = tx.title,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = tx.title,
                            style = MaterialTheme.typography.bodyLarge,
                            color = NeoTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Flagged Zombie Chip
                        if (tx.isZombie) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(NeoRose.copy(alpha = 0.12f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Zombie",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeoRose
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = tx.subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = NeoTextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Amount & Timestamp Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = tx.amountText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (tx.isPositive) NeoGreenDark else NeoTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tx.timeLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = NeoTextSecondary,
                        fontSize = 11.sp
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = NeoTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun CategoryBreakdownCard(slices: List<CategorySlice>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = NeoCard,
        border = BorderStroke(1.dp, NeoBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Where It Went",
                style = MaterialTheme.typography.titleMedium,
                color = NeoTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            // Stacked share bar across all categories
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape)
                    .background(NeoContainer)
            ) {
                slices.forEach { slice ->
                    if (slice.share > 0f) {
                        Box(
                            modifier = Modifier
                                .weight(slice.share)
                                .fillMaxHeight()
                                .background(slice.visual.tint)
                        )
                    }
                }
            }

            slices.forEach { slice ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(slice.visual.container),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = slice.visual.icon,
                            contentDescription = null,
                            tint = slice.visual.tint,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = slice.category.displayName,
                        fontSize = 13.sp,
                        color = NeoTextPrimary,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = Formatters.percent(slice.share),
                        fontSize = 12.sp,
                        color = NeoTextSecondary
                    )
                    Text(
                        text = Formatters.lkr(slice.amountLkr),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeoTextPrimary
                    )
                }
            }
        }
    }
}
