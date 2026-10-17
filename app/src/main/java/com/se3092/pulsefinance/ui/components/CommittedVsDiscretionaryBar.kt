package com.se3092.pulsefinance.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.se3092.pulsefinance.ui.theme.NeoBorder
import com.se3092.pulsefinance.ui.theme.NeoCard
import com.se3092.pulsefinance.ui.theme.NeoGreen
import com.se3092.pulsefinance.ui.theme.NeoTextPrimary
import com.se3092.pulsefinance.ui.theme.NeoTextSecondary
import com.se3092.pulsefinance.ui.theme.NeoTextTertiary
import com.se3092.pulsefinance.ui.theme.NeoTrack

@Composable
fun CommittedVsDiscretionaryBar(
    committedAmountLkr: Double,
    discretionaryAmountLkr: Double,
    modifier: Modifier = Modifier
) {
    val total = (committedAmountLkr + discretionaryAmountLkr).coerceAtLeast(1.0)
    val committedRatio = (committedAmountLkr / total).toFloat().coerceIn(0f, 1f)
    val discretionaryRatio = (discretionaryAmountLkr / total).toFloat().coerceIn(0f, 1f)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color(0x0C0F172A),
                spotColor = Color(0x0C0F172A)
            ),
        shape = RoundedCornerShape(24.dp),
        color = NeoCard,
        border = BorderStroke(1.dp, NeoBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "COMMITTED VS. DISCRETIONARY",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeoTextTertiary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Total LKR ${String.format("%,.0f", committedAmountLkr + discretionaryAmountLkr)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = NeoTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dual segmented capsule bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(CircleShape)
                    .background(NeoTrack)
            ) {
                if (committedRatio > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(committedRatio)
                            .height(12.dp)
                            .background(NeoTextPrimary)
                    )
                }
                if (discretionaryRatio > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .background(NeoGreen)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(NeoTextPrimary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "Committed ${(committedRatio * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeoTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "LKR ${String.format("%,.0f", committedAmountLkr)} (Rent/Bills)",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeoTextTertiary,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(NeoGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Discretionary ${(discretionaryRatio * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeoTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "LKR ${String.format("%,.0f", discretionaryAmountLkr)} (Lifestyle)",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeoTextTertiary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
