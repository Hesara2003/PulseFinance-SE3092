package com.se3092.pulsefinance.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.se3092.pulsefinance.ui.theme.NeoBorder
import com.se3092.pulsefinance.ui.theme.NeoCard
import com.se3092.pulsefinance.ui.theme.NeoCardLow
import com.se3092.pulsefinance.ui.theme.NeoContainer
import com.se3092.pulsefinance.ui.theme.NeoGreen
import com.se3092.pulsefinance.ui.theme.NeoGreenDark
import com.se3092.pulsefinance.ui.theme.NeoGreenLight
import com.se3092.pulsefinance.ui.theme.NeoTextPrimary
import com.se3092.pulsefinance.ui.theme.NeoTextSecondary
import com.se3092.pulsefinance.ui.theme.NeoTextTertiary

@Composable
fun OnboardingStep2(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Visual Cockpit Stage
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            // Ambient Luminous Underlay
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(NeoGreen.copy(alpha = 0.22f), Color.Transparent),
                            center = Offset.Unspecified,
                            radius = 260f
                        )
                    )
            )

            // Main Cockpit Card Container
            Surface(
                modifier = Modifier
                    .width(310.dp)
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(26.dp),
                        ambientColor = Color(0x1F0F172A),
                        spotColor = Color(0x280F172A)
                    ),
                shape = RoundedCornerShape(26.dp),
                color = NeoCard,
                border = BorderStroke(1.dp, NeoBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    // Inner Hero Emerald Card
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(132.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = NeoGreen
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(NeoGreen, NeoGreenDark)
                                    )
                                )
                        ) {
                            // Topographic Wave Canvas Overlay
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height

                                val wavePath1 = Path().apply {
                                    moveTo(-10f, h * 0.45f)
                                    cubicTo(w * 0.25f, h * 0.15f, w * 0.6f, h * 0.8f, w + 10f, h * 0.35f)
                                    lineTo(w + 10f, h + 10f)
                                    lineTo(-10f, h + 10f)
                                    close()
                                }
                                drawPath(
                                    path = wavePath1,
                                    color = Color.White.copy(alpha = 0.16f)
                                )

                                val waveStroke = Path().apply {
                                    moveTo(0f, h * 0.25f)
                                    cubicTo(w * 0.3f, h * 0.45f, w * 0.7f, h * 0.1f, w, h * 0.35f)
                                }
                                drawPath(
                                    path = waveStroke,
                                    color = Color.White.copy(alpha = 0.30f),
                                    style = Stroke(width = 1.6.dp.toPx())
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Card Header Details
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color.White.copy(alpha = 0.20f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Wallet,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Text(
                                                text = "Income Streams",
                                                color = Color.White,
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = CircleShape,
                                        color = Color.White.copy(alpha = 0.25f)
                                    ) {
                                        Text(
                                            text = "+18.4% Realized",
                                            color = Color.White,
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                // Realized Income Metric
                                Column {
                                    Text(
                                        text = "MONTHLY CASH REALIZED",
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.6.sp
                                    )
                                    Text(
                                        text = "LKR 347,600",
                                        color = Color.White,
                                        fontSize = 21.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = (-0.5).sp
                                    )
                                }

                                // Micro Pulse Frequency Sparkline (11 bars)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    val sparkHeights = listOf(10.dp, 16.dp, 8.dp, 22.dp, 14.dp, 28.dp, 20.dp, 25.dp, 11.dp, 21.dp, 30.dp)
                                    val sparkAlphas = listOf(0.4f, 0.5f, 0.35f, 0.7f, 0.5f, 0.95f, 0.75f, 0.95f, 0.45f, 0.75f, 1.0f)
                                    sparkHeights.forEachIndexed { i, heightVal ->
                                        Box(
                                            modifier = Modifier
                                                .width(5.dp)
                                                .height(heightVal)
                                                .clip(CircleShape)
                                                .background(Color.White.copy(alpha = sparkAlphas[i]))
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Micro Mini Ledger Rows inside Cockpit
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Row 1: Remote Client Retainer
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = NeoCardLow,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(NeoCard),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Hub,
                                            contentDescription = null,
                                            tint = NeoGreenDark,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "Remote Client Retainer",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NeoTextPrimary
                                        )
                                        Text(
                                            text = "Hourly Escrow Release",
                                            fontSize = 9.5.sp,
                                            color = NeoTextSecondary
                                        )
                                    }
                                }
                                Text(
                                    text = "+LKR 84,000",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NeoGreenDark
                                )
                            }
                        }

                        // Row 2: Staking APY Pool
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = NeoCardLow,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(NeoCard),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Savings,
                                            contentDescription = null,
                                            tint = NeoTextSecondary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "Staking APY Pool",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NeoTextPrimary
                                        )
                                        Text(
                                            text = "Auto-reinvested",
                                            fontSize = 9.5.sp,
                                            color = NeoTextSecondary
                                        )
                                    }
                                }
                                Text(
                                    text = "+$114.20",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NeoGreenDark
                                )
                            }
                        }
                    }
                }
            }

            // Floating Badge 1 (Top Right: USD AdSense Payout)
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 6.dp, y = (-8).dp)
                    .zIndex(10f)
                    .shadow(
                        elevation = 12.dp,
                        shape = CircleShape,
                        ambientColor = Color(0x1F0F172A),
                        spotColor = Color(0x220F172A)
                    ),
                shape = CircleShape,
                color = NeoCard,
                border = BorderStroke(1.dp, NeoBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(NeoGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = NeoGreenDark,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "$320.00 AdSense",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeoTextPrimary
                        )
                        Text(
                            text = "Payout Realized ✓",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeoGreenDark
                        )
                    }
                }
            }

            // Floating Badge 2 (Bottom Left: Tech Salary Cleared)
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-6).dp, y = 10.dp)
                    .zIndex(10f)
                    .shadow(
                        elevation = 12.dp,
                        shape = CircleShape,
                        ambientColor = Color(0x1F0F172A),
                        spotColor = Color(0x220F172A)
                    ),
                shape = CircleShape,
                color = NeoCard,
                border = BorderStroke(1.dp, NeoBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDAE2FD)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Domain,
                            contentDescription = null,
                            tint = Color(0xFF131B2E),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "LKR 140,000",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeoTextPrimary
                        )
                        Text(
                            text = "Tech Salary Cleared ✓",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeoGreenDark
                        )
                    }
                }
            }

            // Floating Badge 3 (Bottom: Real-time FX Pill)
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 10.dp, y = 28.dp)
                    .zIndex(15f)
                    .shadow(
                        elevation = 10.dp,
                        shape = CircleShape,
                        ambientColor = Color(0x1F0F172A),
                        spotColor = Color(0x280F172A)
                    ),
                shape = CircleShape,
                color = NeoTextPrimary
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CurrencyExchange,
                        contentDescription = null,
                        tint = NeoGreenLight,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "Real-time FX (USD / LKR / USDT)",
                        color = Color.White,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Headline & Narrative
        Text(
            text = "Track Every Income Stream in Real Time",
            style = MaterialTheme.typography.headlineLarge,
            color = NeoTextPrimary,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 23.sp,
            textAlign = TextAlign.Center,
            lineHeight = 30.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Consolidate tech salaries, freelance USD wires, and digital yields into a single liquid cash-flow cockpit.",
            style = MaterialTheme.typography.bodyMedium,
            color = NeoTextTertiary,
            fontSize = 13.5.sp,
            textAlign = TextAlign.Center,
            lineHeight = 19.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Trust Badges
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = NeoContainer,
                border = BorderStroke(1.dp, NeoBorder.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = NeoGreenDark,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Multi-Currency Auto-FX",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NeoTextPrimary
                    )
                }
            }

            Surface(
                shape = CircleShape,
                color = NeoContainer,
                border = BorderStroke(1.dp, NeoBorder.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = NeoGreenDark,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "Milestone Escrow Sync",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NeoTextPrimary
                    )
                }
            }
        }
    }
}
