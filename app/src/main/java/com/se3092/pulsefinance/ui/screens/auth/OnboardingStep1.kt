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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SignalCellular4Bar
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiTethering
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.se3092.pulsefinance.ui.theme.NeoBorder
import com.se3092.pulsefinance.ui.theme.NeoCard
import com.se3092.pulsefinance.ui.theme.NeoCardLow
import com.se3092.pulsefinance.ui.theme.NeoContainer
import com.se3092.pulsefinance.ui.theme.NeoContainerHigh
import com.se3092.pulsefinance.ui.theme.NeoGreen
import com.se3092.pulsefinance.ui.theme.NeoGreenDark
import com.se3092.pulsefinance.ui.theme.NeoTextPrimary
import com.se3092.pulsefinance.ui.theme.NeoTextSecondary
import com.se3092.pulsefinance.ui.theme.NeoTextTertiary
import com.se3092.pulsefinance.ui.theme.NeoTrack

@Composable
fun OnboardingStep1(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Visual Hero Stage
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            // Ambient Radial Glow
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

            // Phone Device Mockup Container
            Surface(
                modifier = Modifier
                    .width(264.dp)
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(40.dp),
                        ambientColor = Color(0x1F0F172A),
                        spotColor = Color(0x280F172A)
                    ),
                shape = RoundedCornerShape(40.dp),
                color = NeoContainerHigh,
                border = BorderStroke(1.dp, NeoBorder)
            ) {
                Surface(
                    modifier = Modifier
                        .padding(6.dp)
                        .clip(RoundedCornerShape(34.dp)),
                    shape = RoundedCornerShape(34.dp),
                    color = NeoCard
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        // Dynamic Island & Status Bar Mock
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "9:41",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeoTextTertiary
                            )
                            Box(
                                modifier = Modifier
                                    .width(58.dp)
                                    .height(13.dp)
                                    .clip(CircleShape)
                                    .background(NeoTextPrimary)
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SignalCellular4Bar,
                                    contentDescription = null,
                                    tint = NeoTextTertiary,
                                    modifier = Modifier.size(11.dp)
                                )
                                Icon(
                                    imageVector = Icons.Default.Wifi,
                                    contentDescription = null,
                                    tint = NeoTextTertiary,
                                    modifier = Modifier.size(11.dp)
                                )
                                Icon(
                                    imageVector = Icons.Default.BatteryFull,
                                    contentDescription = null,
                                    tint = NeoTextTertiary,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Mini In-App Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
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
                                        .background(
                                            Brush.linearGradient(
                                                listOf(NeoGreenDark, NeoGreen)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "KS",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                                Column {
                                    Text(
                                        text = "Kavindu Silva",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeoTextPrimary,
                                        lineHeight = 14.sp
                                    )
                                    Text(
                                        text = "Good morning",
                                        fontSize = 9.sp,
                                        color = NeoTextTertiary,
                                        lineHeight = 11.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(NeoContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = NeoTextPrimary,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Mini Hero Card (Topographic Green Texture)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(96.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = NeoGreen
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val w = size.width
                                    val h = size.height

                                    val path1 = Path().apply {
                                        moveTo(-10f, h * 0.25f)
                                        cubicTo(w * 0.25f, h * 0.1f, w * 0.5f, h * 0.65f, w + 10f, h * 0.35f)
                                    }
                                    drawPath(
                                        path = path1,
                                        color = Color.White.copy(alpha = 0.22f),
                                        style = Stroke(width = 2.dp.toPx())
                                    )

                                    val path2 = Path().apply {
                                        moveTo(-10f, h * 0.6f)
                                        cubicTo(w * 0.35f, h * 0.4f, w * 0.65f, h * 0.95f, w + 10f, h * 0.65f)
                                    }
                                    drawPath(
                                        path = path2,
                                        color = Color.White.copy(alpha = 0.18f),
                                        style = Stroke(width = 1.8.dp.toPx())
                                    )

                                    val path3 = Path().apply {
                                        moveTo(-10f, h * 0.85f)
                                        cubicTo(w * 0.3f, h * 0.7f, w * 0.6f, h * 1.1f, w + 10f, h * 0.9f)
                                    }
                                    drawPath(
                                        path = path3,
                                        color = Color.White.copy(alpha = 0.14f),
                                        style = Stroke(width = 1.5.dp.toPx())
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "VISA",
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontStyle = FontStyle.Italic
                                        )
                                        Icon(
                                            imageVector = Icons.Default.MoreHoriz,
                                            contentDescription = null,
                                            tint = Color.White.copy(alpha = 0.8f),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = "SAFE-TO-SPEND BALANCE",
                                            color = Color.White.copy(alpha = 0.85f),
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "LKR 285,400.00",
                                                color = Color.White,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                            Icon(
                                                imageVector = Icons.Default.WifiTethering,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Mini Quick Contacts Carousel Preview
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(NeoContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = NeoTextPrimary,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            listOf("☕", "🚗", "🍔").forEach { emoji ->
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(NeoCardLow),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = emoji, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Mini Capsule Trend Micro-Chart
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(34.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            val barHeights = listOf(14.dp, 28.dp, 20.dp, 32.dp, 12.dp, 26.dp)
                            barHeights.forEach { heightVal ->
                                Box(
                                    modifier = Modifier
                                        .width(9.dp)
                                        .height(34.dp)
                                        .clip(CircleShape)
                                        .background(NeoTrack),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(heightVal)
                                            .clip(CircleShape)
                                            .background(NeoGreen)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Floating Dynamic Notification Badge 1 (Top Right)
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-4).dp, y = 4.dp)
                    .zIndex(10f)
                    .shadow(
                        elevation = 14.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = Color(0x1F0F172A),
                        spotColor = Color(0x240F172A)
                    ),
                shape = RoundedCornerShape(16.dp),
                color = NeoCard,
                border = BorderStroke(1.dp, NeoBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column {
                        Text(
                            text = "LKR 45,000 Received from",
                            fontSize = 9.5.sp,
                            color = NeoTextTertiary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "SME Milestone",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeoTextPrimary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(NeoGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            // Floating Dynamic Notification Badge 2 (Bottom Left)
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 4.dp, y = (-4).dp)
                    .zIndex(10f)
                    .shadow(
                        elevation = 14.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = Color(0x1F0F172A),
                        spotColor = Color(0x240F172A)
                    ),
                shape = RoundedCornerShape(16.dp),
                color = NeoCard,
                border = BorderStroke(1.dp, NeoBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column {
                        Text(
                            text = "LKR 1,450 paid to",
                            fontSize = 9.5.sp,
                            color = NeoTextTertiary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "PickMe Ride",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeoTextPrimary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(NeoGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Headline & Narrative
        Text(
            text = "Innovative Digital Financial Planner and Management",
            style = MaterialTheme.typography.headlineLarge,
            color = NeoTextPrimary,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 23.sp,
            textAlign = TextAlign.Center,
            lineHeight = 30.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Designed to help modern tech workers manage cash flow, eliminate invisible leaks, and reach goals efficiently.",
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
                        text = "Bank-Grade 256-Bit",
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
                        text = "Real-Time Sync",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NeoTextPrimary
                    )
                }
            }
        }
    }
}
