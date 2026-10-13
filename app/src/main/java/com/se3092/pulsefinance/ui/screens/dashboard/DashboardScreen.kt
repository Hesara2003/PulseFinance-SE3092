package com.se3092.pulsefinance.ui.screens.dashboard

import com.se3092.pulsefinance.ui.common.presetVisual
import com.se3092.pulsefinance.ui.common.TransactionVisual
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.se3092.pulsefinance.R
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.se3092.pulsefinance.ui.common.compactAmount
import com.se3092.pulsefinance.ui.common.lkr
import com.se3092.pulsefinance.ui.common.signedLkr
import com.se3092.pulsefinance.ui.common.transactionVisual
import com.se3092.pulsefinance.ui.components.CommittedVsDiscretionaryBar
import com.se3092.pulsefinance.ui.components.ExpenseItemCard
import com.se3092.pulsefinance.ui.components.MacBookGoalCard
import com.se3092.pulsefinance.ui.components.SafeToSpendGauge
import com.se3092.pulsefinance.ui.screens.expense.AddExpenseBottomSheet
import com.se3092.pulsefinance.ui.theme.NeoBorder
import com.se3092.pulsefinance.ui.theme.NeoCard
import com.se3092.pulsefinance.ui.theme.NeoCardLow
import com.se3092.pulsefinance.ui.theme.NeoContainer
import com.se3092.pulsefinance.ui.theme.NeoContainerHigh
import com.se3092.pulsefinance.ui.theme.NeoErrorContainer
import com.se3092.pulsefinance.ui.theme.NeoGreen
import com.se3092.pulsefinance.ui.theme.NeoGreenDark
import com.se3092.pulsefinance.ui.theme.NeoGreenGlow
import com.se3092.pulsefinance.ui.theme.NeoRose
import com.se3092.pulsefinance.ui.theme.NeoSurface
import com.se3092.pulsefinance.ui.theme.NeoTextPrimary
import com.se3092.pulsefinance.ui.theme.NeoTextSecondary
import com.se3092.pulsefinance.ui.theme.NeoTextTertiary
import com.se3092.pulsefinance.ui.theme.NeoTrack

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToIncome: () -> Unit,
    onNavigateToGoal: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onSignOut: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var isBalanceMasked by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissMessage()
        }
    }

    Scaffold(
        containerColor = NeoSurface,
        snackbarHost = { SnackbarHost(snackbarHostState, modifier = Modifier.padding(bottom = 76.dp)) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddExpense() },
                containerColor = NeoGreen,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 76.dp)
                    .shadow(14.dp, CircleShape, spotColor = NeoGreenGlow)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // =========================================================================
            // 1. Greeting & Profile Bar (Exact HTML Replica)
            // =========================================================================
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile Avatar & Greeting
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(modifier = Modifier.size(44.dp)) {
                            // Avatar Circle
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(NeoGreenDark, NeoGreen)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = state.initials,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                            }
                            // Active Presence Indicator Dot
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .align(Alignment.BottomEnd)
                                    .clip(CircleShape)
                                    .background(NeoSurface)
                                    .padding(2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(NeoGreen)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = state.firstName,
                                style = MaterialTheme.typography.titleMedium,
                                color = NeoTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                letterSpacing = (-0.3).sp
                            )
                            Text(
                                text = state.greeting,
                                style = MaterialTheme.typography.bodySmall,
                                color = NeoTextSecondary,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Action Buttons (Eye Mask Toggle, Notification Bell, Sign Out)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Eye Toggle Button for Sensitive Balances
                        Surface(
                            shape = CircleShape,
                            color = NeoCard,
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { isBalanceMasked = !isBalanceMasked }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isBalanceMasked) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle Balance Privacy",
                                    tint = NeoTextSecondary,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }

                        // Sign Out Button
                        IconButton(
                            onClick = {
                                viewModel.signOut()
                                onSignOut()
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Sign Out",
                                tint = NeoTextTertiary,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                }
            }

            // =========================================================================
            // 2. Hero Topographic Card Clone (Flagship Visual Card)
            // =========================================================================
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(26.dp),
                            ambientColor = Color(0x3800C950),
                            spotColor = Color(0x4000C950)
                        ),
                    shape = RoundedCornerShape(26.dp),
                    color = NeoGreen
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF00C950), Color(0xFF00A844))
                                )
                            )
                    ) {
                        // Vector Topographic Contour Waves & Dash Lines Overlay
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            val wave1 = Path().apply {
                                moveTo(-10f, h * 0.3f)
                                cubicTo(w * 0.2f, h * 0.05f, w * 0.55f, h * 0.45f, w + 10f, h * 0.15f)
                            }
                            drawPath(
                                path = wave1,
                                color = Color.White.copy(alpha = 0.22f),
                                style = Stroke(width = 1.6.dp.toPx())
                            )

                            val wave2 = Path().apply {
                                moveTo(-15f, h * 0.55f)
                                cubicTo(w * 0.25f, h * 0.25f, w * 0.6f, h * 0.7f, w + 15f, h * 0.35f)
                            }
                            drawPath(
                                path = wave2,
                                color = Color.White.copy(alpha = 0.20f),
                                style = Stroke(width = 1.6.dp.toPx())
                            )

                            val wave3 = Path().apply {
                                moveTo(-10f, h * 0.8f)
                                cubicTo(w * 0.35f, h * 0.5f, w * 0.65f, h * 0.95f, w + 10f, h * 0.6f)
                            }
                            drawPath(
                                path = wave3,
                                color = Color.White.copy(alpha = 0.18f),
                                style = Stroke(width = 1.6.dp.toPx())
                            )

                            // Dashed Topographic Wave Line
                            val waveDashed = Path().apply {
                                moveTo(-10f, h * 0.1f)
                                cubicTo(w * 0.25f, -10f, w * 0.7f, h * 0.25f, w + 10f, 0f)
                            }
                            drawPath(
                                path = waveDashed,
                                color = Color.White.copy(alpha = 0.24f),
                                style = Stroke(
                                    width = 1.2.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                                )
                            )
                        }

                        // Top-Right Lighting Scrim
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .align(Alignment.TopEnd)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(Color.White.copy(alpha = 0.15f), Color.Transparent),
                                        center = Offset.Unspecified,
                                        radius = 180f
                                    )
                                )
                        )

                        // Card Content Structure
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 22.dp, vertical = 20.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Top Row: VISA Brand & Commercial Bank Tag
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "PULSE",
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontStyle = FontStyle.Italic,
                                        fontSize = 19.sp,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "|",
                                        color = Color.White.copy(alpha = 0.4f),
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = state.monthLabel.uppercase(),
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp
                                    )
                                }
                            }

                            // Center: this month's net cash flow across every income source
                            Column {
                                Text(
                                    text = if (isBalanceMasked)
                                        "LKR ••••••••"
                                    else
                                        signedLkr(state.summary.netSavingsLkr),
                                    color = Color.White,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = (-0.5).sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Net cash flow this month",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 0.3.sp
                                )
                            }

                            // Bottom: money in vs money out
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isBalanceMasked) "In •••• · Out ••••" else
                                        "In ${compactAmount(state.summary.totalRealizedIncomeLkr)} · Out ${compactAmount(state.summary.totalExpensesLkr)}",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.5.sp
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Contactless Wave Glyph (rotated 90°)
                                    Icon(
                                        imageVector = Icons.Default.WifiTethering,
                                        contentDescription = "Contactless",
                                        tint = Color.White.copy(alpha = 0.9f),
                                        modifier = Modifier
                                            .size(18.dp)
                                            .rotate(90f)
                                    )

                                    // EMV Microchip Emblem
                                    Surface(
                                        modifier = Modifier.size(width = 30.dp, height = 22.dp),
                                        shape = RoundedCornerShape(5.dp),
                                        color = Color.White.copy(alpha = 0.22f),
                                        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.4f))
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(2.dp),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(2.dp)
                                                    .background(Color.White.copy(alpha = 0.4f))
                                            )
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(width = 10.dp, height = 8.dp)
                                                        .background(Color.White.copy(alpha = 0.5f))
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .size(width = 10.dp, height = 8.dp)
                                                        .background(Color.White.copy(alpha = 0.5f))
                                                )
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(2.dp)
                                                    .background(Color.White.copy(alpha = 0.4f))
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // 3. Overdue Milestone Reconcile Banner
            // =========================================================================
            if (state.unpaidInvoices.isNotEmpty()) item {
                val firstUnpaid = state.unpaidInvoices.first()
                val hasOverdue = state.overdueInvoiceCount > 0
                val bannerTitle = if (hasOverdue)
                    "${state.overdueInvoiceCount} invoice${if (state.overdueInvoiceCount > 1) "s" else ""} overdue: ${firstUnpaid.title}"
                else
                    "${state.unpaidInvoices.size} invoice${if (state.unpaidInvoices.size > 1) "s" else ""} awaiting payment"
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 3.dp,
                            shape = RoundedCornerShape(16.dp),
                            ambientColor = Color(0x1FBA1A1A),
                            spotColor = Color(0x1FBA1A1A)
                        )
                        .clickable { onNavigateToIncome() },
                    shape = RoundedCornerShape(16.dp),
                    color = NeoErrorContainer,
                    border = BorderStroke(1.dp, NeoRose.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(NeoRose.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Alert",
                                    tint = NeoRose,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = bannerTitle,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeoRose
                                )
                                Text(
                                    text = "${lkr(state.summary.pendingIncomeLkr)} unpaid — tap to follow up",
                                    fontSize = 11.sp,
                                    color = NeoRose.copy(alpha = 0.85f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Reconcile",
                            tint = NeoRose.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // =========================================================================
            // 4. 3-Tap Quick Presets Carousel
            // =========================================================================
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Frequent Transfers",
                            style = MaterialTheme.typography.titleMedium,
                            color = NeoTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "3-Tap Presets",
                            fontSize = 11.5.sp,
                            color = NeoTextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // "+ Add" circular button
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { viewModel.openAddExpense() }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(NeoContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Custom",
                                    tint = NeoTextSecondary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Add",
                                fontSize = 11.sp,
                                color = NeoTextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // The user's own repeated spends (starter templates until habits emerge)
                        state.presets.forEach { preset ->
                            QuickPresetBadge(
                                visual = presetVisual(preset.title, preset.category),
                                label = preset.title,
                                amount = compactAmount(preset.amountLkr)
                            ) {
                                viewModel.openAddExpense(preset)
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // 5. Recent Transactions Section (Horizontal Slider Replica)
            // =========================================================================
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Transactions",
                            style = MaterialTheme.typography.titleMedium,
                            color = NeoTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "View all",
                            fontSize = 12.sp,
                            color = NeoGreenDark,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { onNavigateToAnalytics() }
                        )
                    }

                    if (state.recentTransactions.isEmpty()) {
                        Text(
                            text = "Nothing logged yet. Tap + to record your first expense, or add income from the Income hub.",
                            fontSize = 12.5.sp,
                            color = NeoTextSecondary,
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    } else {
                        // Horizontal Cards Slider — newest income and expenses together
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            state.recentTransactions.forEach { tx ->
                                val visual = transactionVisual(tx)
                                TransactionSquareCard(
                                    currency = tx.originalAmountLabel?.substringBefore(' ') ?: "LKR",
                                    amount = tx.originalAmountLabel?.substringAfter(' ')
                                        ?: "%,.0f".format(kotlin.math.abs(tx.amountLkr)),
                                    title = tx.title,
                                    icon = visual.icon,
                                    iconContainerColor = visual.container,
                                    iconColor = visual.tint,
                                    isIncoming = tx.isIncoming
                                )
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // 6. Weekly Velocity Teaser Card
            // =========================================================================
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = NeoCard,
                    border = BorderStroke(1.dp, NeoBorder),
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(7.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(NeoGreen)
                                )
                                Column {
                                    Text(
                                        text = "Discretionary this week",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeoTextPrimary
                                    )
                                    Text(
                                        text = lkr(state.weekDiscretionaryLkr),
                                        fontSize = 11.5.sp,
                                        color = NeoTextSecondary
                                    )
                                }
                            }
                            // Fewer discretionary rupees than last week is the win worth showing.
                            state.weekChangePct?.let { change ->
                                Text(
                                    text = if (change <= 0) "${-change}% less than last week" else "+$change% vs last week",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (change <= 0) NeoGreenDark else NeoRose
                                )
                            }
                        }

                        // Micro Capsule 7-Day Chart (Mon..Sun of the current week)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            state.weekBars.forEach { bar ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(10.dp)
                                            .height(40.dp)
                                            .clip(CircleShape)
                                            .background(NeoContainerHigh),
                                        contentAlignment = Alignment.BottomCenter
                                    ) {
                                        if (bar.amountLkr > 0) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height((40 * bar.fillRatio).coerceAtLeast(4f).dp)
                                                    .clip(CircleShape)
                                                    .background(NeoGreen)
                                            )
                                        }
                                    }
                                    Text(
                                        text = bar.label,
                                        fontSize = 10.sp,
                                        color = if (bar.isToday) NeoGreenDark else NeoTextSecondary,
                                        fontWeight = if (bar.isToday) FontWeight.ExtraBold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // 7. Safe-to-Spend & Goal Pacing Cards (Extended Features)
            // =========================================================================
            if (!state.isLoading && !state.summary.hasAnyData) item {
                GettingStartedCard(
                    onLogExpense = { viewModel.openAddExpense() },
                    onAddIncome = onNavigateToIncome,
                    onSetGoal = onNavigateToGoal,
                    hasGoal = state.primaryGoal != null
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SafeToSpendGauge(
                        dailyAllowanceLkr = state.summary.safeToSpendDailyLkr,
                        daysRemainingInMonth = state.summary.daysRemainingInMonth
                    )
                    if (state.summary.incomeBasisIsAverage) {
                        Text(
                            text = "Using your 3-month average income (${lkr(state.summary.incomeBasisLkr)}) until this month's income arrives.",
                            fontSize = 11.sp,
                            color = NeoTextSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )
                    }
                }
            }

            item {
                val goal = state.primaryGoal
                if (goal != null) {
                    MacBookGoalCard(goal = goal, onMoreClick = onNavigateToGoal)
                } else if (!state.isLoading) {
                    SetGoalPromptCard(onClick = onNavigateToGoal)
                }
            }

            item {
                CommittedVsDiscretionaryBar(
                    committedAmountLkr = state.summary.committedExpensesLkr,
                    discretionaryAmountLkr = state.summary.discretionaryExpensesLkr
                )
            }

            // Real Expense Ledger List (if logged items exist)
            if (state.recentExpenses.isNotEmpty()) {
                item {
                    Text(
                        text = "Ledger History",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeoTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                items(state.recentExpenses, key = { it.id }) { expense ->
                    ExpenseItemCard(
                        expense = expense,
                        onDelete = { viewModel.deleteExpense(expense.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(90.dp)) // Clearance for bottom navigation dock
            }
        }

        if (state.addExpense.isVisible) {
            AddExpenseBottomSheet(
                form = state.addExpense,
                presets = state.presets,
                accounts = state.accounts,
                onTitleChange = viewModel::onTitleChanged,
                onAmountChange = viewModel::onAmountChanged,
                onCategoryChange = viewModel::onCategoryChanged,
                onCommittedToggle = viewModel::onCommittedToggled,
                onAccountSelect = viewModel::onAccountSelected,
                onDaysAgoSelect = viewModel::onDaysAgoSelected,
                onPresetSelect = viewModel::onPresetSelected,
                onSubmit = viewModel::submitExpense,
                onDismiss = viewModel::dismissAddExpense
            )
        }
    }
}

@Composable
private fun GettingStartedCard(
    onLogExpense: () -> Unit,
    onAddIncome: () -> Unit,
    onSetGoal: () -> Unit,
    hasGoal: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = NeoCard,
        border = BorderStroke(1.dp, NeoBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Get your first clear picture",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = NeoTextPrimary
            )
            Text(
                text = "Three quick steps and this screen starts showing where your money really goes.",
                fontSize = 12.sp,
                color = NeoTextSecondary
            )
            GettingStartedStep("1", "Record this month's income — salary, freelance, AdSense or crypto", onAddIncome)
            GettingStartedStep("2", "Log today's spending — presets make it one tap", onLogExpense)
            if (!hasGoal) GettingStartedStep("3", "Set the goal you're saving for", onSetGoal)
        }
    }
}

@Composable
private fun GettingStartedStep(number: String, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NeoCardLow)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(NeoGreen),
            contentAlignment = Alignment.Center
        ) {
            Text(text = number, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Text(text = text, fontSize = 12.5.sp, color = NeoTextPrimary, modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = NeoTextTertiary,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun SetGoalPromptCard(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = NeoCard,
        border = BorderStroke(1.dp, NeoBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.illustration_piggy_bank),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Set a savings goal", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeoTextPrimary)
                Text(
                    text = "See the exact monthly pace needed and when you'll get there",
                    fontSize = 12.sp,
                    color = NeoTextSecondary
                )
            }
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = NeoTextTertiary)
        }
    }
}

@Composable
fun QuickPresetBadge(
    visual: TransactionVisual,
    label: String,
    amount: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(64.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(visual.container)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = visual.icon,
                    contentDescription = null,
                    tint = visual.tint,
                    modifier = Modifier.size(24.dp)
                )
            }
            // Bottom-Right Pill Badge
            Surface(
                modifier = Modifier.align(Alignment.BottomEnd),
                shape = CircleShape,
                color = NeoContainerHigh
            ) {
                Text(
                    text = amount,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeoTextPrimary,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = NeoTextPrimary,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun TransactionSquareCard(
    currency: String,
    amount: String,
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconContainerColor: Color,
    iconColor: Color,
    isIncoming: Boolean
) {
    Surface(
        modifier = Modifier
            .size(width = 136.dp, height = 136.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = Color(0x1F0F172A),
                spotColor = Color(0x1A0F172A)
            ),
        shape = RoundedCornerShape(18.dp),
        color = NeoCard,
        border = BorderStroke(1.dp, NeoBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(13.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row: Amount & Arrow Glyph
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = currency,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = NeoTextSecondary
                    )
                    Text(
                        text = amount,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeoTextPrimary
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = NeoCardLow,
                    modifier = Modifier.size(26.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isIncoming)
                                Icons.Default.SouthWest
                            else
                                Icons.Default.NorthEast,
                            contentDescription = null,
                            tint = if (isIncoming) NeoGreenDark else NeoTextPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            // Bottom Row: Category Icon & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(iconContainerColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(13.dp)
                    )
                }
                Text(
                    text = title,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = NeoTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
