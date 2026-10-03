package com.se3092.pulsefinance.ui.screens.auth

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.se3092.pulsefinance.ui.theme.NeoBorder
import com.se3092.pulsefinance.ui.theme.NeoCard
import com.se3092.pulsefinance.ui.theme.NeoCardLow
import com.se3092.pulsefinance.ui.theme.NeoContainer
import com.se3092.pulsefinance.ui.theme.NeoErrorContainer
import com.se3092.pulsefinance.ui.theme.NeoGreen
import com.se3092.pulsefinance.ui.theme.NeoGreenDark
import com.se3092.pulsefinance.ui.theme.NeoRose
import com.se3092.pulsefinance.ui.theme.NeoTextPrimary
import com.se3092.pulsefinance.ui.theme.NeoTextSecondary
import com.se3092.pulsefinance.ui.theme.NeoTextTertiary
import com.se3092.pulsefinance.ui.theme.NeoTrack

@Composable
fun OnboardingStep3(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Visual Showcase Stage
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            // Ambient Backdrop Glows
            Box(
                modifier = Modifier
                    .size(230.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(NeoGreen.copy(alpha = 0.20f), Color.Transparent),
                            center = Offset.Unspecified,
                            radius = 250f
                        )
                    )
            )

            // Center Isometric / Curved Phone Shell
            Surface(
                modifier = Modifier
                    .width(268.dp)
                    .shadow(
                        elevation = 22.dp,
                        shape = RoundedCornerShape(38.dp),
                        ambientColor = Color(0x2E0F172A),
                        spotColor = Color(0x380F172A)
                    ),
                shape = RoundedCornerShape(38.dp),
                color = NeoTextPrimary
            ) {
                // Phone Screen Container
                Surface(
                    modifier = Modifier
                        .padding(6.dp)
                        .clip(RoundedCornerShape(32.dp)),
                    shape = RoundedCornerShape(32.dp),
                    color = NeoCard
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Dynamic Island Notch
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .width(64.dp)
                                .height(14.dp)
                                .clip(CircleShape)
                                .background(NeoTextPrimary)
                                .padding(end = 6.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(NeoGreen)
                            )
                        }

                        // Phone Header / PulseEngine Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(NeoGreen),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MonitorHeart,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                                Text(
                                    text = "PulseEngine™",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NeoTextPrimary,
                                    letterSpacing = (-0.3).sp
                                )
                            }

                            Surface(
                                shape = CircleShape,
                                color = NeoGreen.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "AI AUDIT LIVE",
                                    color = NeoGreenDark,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                                )
                            }
                        }

                        // Inner Goal Card: MacBook Pro M4
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = NeoCardLow,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(11.dp),
                                verticalArrangement = Arrangement.spacedBy(7.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Surface(
                                            modifier = Modifier.size(34.dp),
                                            shape = RoundedCornerShape(10.dp),
                                            color = NeoCard,
                                            shadowElevation = 2.dp
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.LaptopMac,
                                                    contentDescription = null,
                                                    tint = NeoTextPrimary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }

                                        Column {
                                            Text(
                                                text = "MacBook Pro M4",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = NeoTextPrimary
                                            )
                                            Text(
                                                text = "Dream Tech Goal",
                                                fontSize = 9.5.sp,
                                                color = NeoTextTertiary
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = CircleShape,
                                        color = NeoGreen.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "76%",
                                            color = NeoGreenDark,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.5.dp)
                                        )
                                    }
                                }

                                // Pacing & Progress Pill
                                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(9.dp)
                                            .clip(CircleShape)
                                            .background(NeoTrack)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(0.76f)
                                                .height(9.dp)
                                                .clip(CircleShape)
                                                .background(NeoGreen)
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "LKR 372,400",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = NeoTextPrimary
                                        )
                                        Text(
                                            text = "of 490,000",
                                            fontSize = 9.5.sp,
                                            color = NeoTextTertiary
                                        )
                                    }
                                }

                                // Auto-Pacing Rate Pill
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = NeoCard,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp, vertical = 5.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                                contentDescription = null,
                                                tint = NeoGreenDark,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Text(
                                                text = "Auto-Pacing",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = NeoTextSecondary
                                            )
                                        }
                                        Text(
                                            text = "LKR 1,330/day",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NeoGreenDark
                                        )
                                    }
                                }
                            }
                        }

                        // Micro Mini Subscriptions Detected Strip
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = NeoCardLow,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Cancel,
                                        contentDescription = null,
                                        tint = NeoRose,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "3 Leaks Uncovered",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeoTextPrimary
                                    )
                                }
                                Text(
                                    text = "-LKR 19,400",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NeoRose
                                )
                            }
                        }
                    }
                }
            }

            // Top-Right Floating Warning Pill ("Zombie Sub Found")
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 6.dp, y = (-10).dp)
                    .zIndex(10f)
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = Color(0x1F0F172A),
                        spotColor = Color(0x280F172A)
                    ),
                shape = RoundedCornerShape(16.dp),
                color = NeoCard,
                border = BorderStroke(1.dp, NeoBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(NeoErrorContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = NeoRose,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "ZOMBIE SUB FOUND",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NeoRose,
                                letterSpacing = 0.5.sp
                            )
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(NeoRose)
                            )
                        }
                        Text(
                            text = "Fitness Club -LKR 8,500/mo",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeoTextPrimary
                        )
                    }
                }
            }

            // Bottom-Left Floating Emerald Badge (Nuke Leaks & Velocity Boost)
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-4).dp, y = 14.dp)
                    .zIndex(10f)
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = Color(0x1F0F172A),
                        spotColor = Color(0x280F172A)
                    ),
                shape = RoundedCornerShape(16.dp),
                color = NeoCard,
                border = BorderStroke(1.dp, NeoBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(NeoGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = "1-Click Nuke Leaks",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeoGreenDark
                            )
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = NeoGreenDark,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                        Text(
                            text = "+40% Faster Goal Velocity",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NeoTextPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Headline & Narrative Description
        Text(
            text = "Kill Zombie Leaks &\nHit Milestone Goals",
            style = MaterialTheme.typography.headlineLarge,
            color = NeoTextPrimary,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 23.sp,
            textAlign = TextAlign.Center,
            lineHeight = 29.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Automatically pinpoint forgotten subscriptions and convert discretionary waste into your dream tech upgrades with robotic precision.",
            style = MaterialTheme.typography.bodyMedium,
            color = NeoTextTertiary,
            fontSize = 13.5.sp,
            textAlign = TextAlign.Center,
            lineHeight = 19.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2-Column Grid Feature Highlights
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Pill 1: Leak Radar
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                color = NeoCard,
                border = BorderStroke(1.dp, NeoBorder),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(NeoGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Radar,
                            contentDescription = null,
                            tint = NeoGreenDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Leak Radar",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeoTextPrimary
                        )
                        Text(
                            text = "Scans 40+ banks",
                            fontSize = 9.5.sp,
                            color = NeoTextTertiary
                        )
                    }
                }
            }

            // Pill 2: Cost Shift AI
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                color = NeoCard,
                border = BorderStroke(1.dp, NeoBorder),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDAE2FD)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF565E74),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Cost Shift AI",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeoTextPrimary
                        )
                        Text(
                            text = "Waste into wealth",
                            fontSize = 9.5.sp,
                            color = NeoTextTertiary
                        )
                    }
                }
            }
        }
    }
}
