package com.se3092.pulsefinance.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.se3092.pulsefinance.domain.model.SavingsGoal
import com.se3092.pulsefinance.ui.common.formatDate
import com.se3092.pulsefinance.ui.theme.NeoBorder
import com.se3092.pulsefinance.ui.theme.NeoCard
import com.se3092.pulsefinance.ui.theme.NeoCardLow
import com.se3092.pulsefinance.ui.theme.NeoContainer
import com.se3092.pulsefinance.ui.theme.NeoGreen
import com.se3092.pulsefinance.ui.theme.NeoGreenDark
import com.se3092.pulsefinance.ui.theme.NeoGreenFixed
import com.se3092.pulsefinance.ui.theme.NeoOnSecondaryFixed
import com.se3092.pulsefinance.ui.theme.NeoSecondaryContainer
import com.se3092.pulsefinance.ui.theme.NeoSurfaceHighest
import com.se3092.pulsefinance.ui.theme.NeoTextPrimary
import com.se3092.pulsefinance.ui.theme.NeoTextSecondary

@Composable
fun MacBookGoalCard(
    goal: SavingsGoal,
    modifier: Modifier = Modifier,
    onMoreClick: () -> Unit = {}
) {
    val progress = (goal.currentSavedLkr / goal.targetAmountLkr).coerceIn(0.0, 1.0).toFloat()
    val percentageStr = String.format("%.1f%%", progress * 100)

    Surface(
        modifier = modifier
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
        Box(modifier = Modifier.fillMaxWidth()) {
            // Ambient Radial Gradient in Top-Right
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.TopEnd)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                NeoGreenFixed.copy(alpha = 0.20f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Top Header Row
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
                        // Laptop Glyphs Container
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(NeoCardLow),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LaptopMac,
                                contentDescription = "Hardware Milestone",
                                tint = NeoGreen,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Column {
                            // Category Tag Chip
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(NeoSecondaryContainer)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Savings Goal",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeoOnSecondaryFixed
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = goal.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleMedium,
                                color = NeoTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                            Text(
                                text = goal.notes.ifBlank { "Deadline ${formatDate(goal.targetDateMillis)}" },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodySmall,
                                color = NeoTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // More Settings Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NeoContainer)
                            .clickable(onClick = onMoreClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Target settings",
                            tint = NeoTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Inner Progress Container
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = NeoCardLow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Progress to Target",
                                style = MaterialTheme.typography.labelMedium,
                                color = NeoTextSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = percentageStr,
                                style = MaterialTheme.typography.titleMedium,
                                color = NeoGreenDark,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Glowing Capsule Progress Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(CircleShape)
                                .background(NeoSurfaceHighest)
                                .padding(1.dp)
                        ) {
                            val fillWidthFraction = progress.coerceIn(0.015f, 1f)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fillWidthFraction)
                                    .height(10.dp)
                                    .clip(CircleShape)
                                    .background(NeoGreen)
                            ) {
                                // Subtle highlight edge at the right
                                Box(
                                    modifier = Modifier
                                        .size(width = 8.dp, height = 10.dp)
                                        .align(Alignment.CenterEnd)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.5f))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Saved so far",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeoTextSecondary,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "LKR ${String.format("%,.0f", goal.currentSavedLkr)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = NeoTextPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Total Target",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeoTextSecondary,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "LKR ${String.format("%,.0f", goal.targetAmountLkr)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = NeoTextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Required Pace Pill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .background(NeoContainer)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Pace",
                            tint = NeoGreenDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(color = NeoTextSecondary, fontSize = 13.sp)) {
                                    append("Required Pace: ")
                                }
                                withStyle(SpanStyle(color = NeoTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)) {
                                    append("LKR ${String.format("%,.0f", goal.requiredDailySavingsLkr)}")
                                }
                                withStyle(SpanStyle(color = NeoTextSecondary, fontSize = 13.sp)) {
                                    append("/day ")
                                }
                                withStyle(SpanStyle(color = NeoTextSecondary, fontSize = 12.sp)) {
                                    append("(~LKR ${String.format("%,.0f", goal.requiredMonthlySavingsLkr)}/mo)")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
