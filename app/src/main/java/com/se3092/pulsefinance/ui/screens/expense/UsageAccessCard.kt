package com.se3092.pulsefinance.ui.screens.expense

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Explains why the app would like usage access before sending the user to system Settings (NFR-12).
 * "Not now" hides it for good; the audit still works without usage data.
 */
@Composable
internal fun UsageAccessCard(onAllow: () -> Unit, onDecline: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PhoneAndroid,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "See when you last opened your apps",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "Allowing usage access lets PulseFinance show when you last opened apps " +
                    "like Netflix or Spotify, so a subscription you never use is flagged sooner. " +
                    "It stays on your phone and is only used for this audit.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onAllow) { Text("Open settings") }
                TextButton(onClick = onDecline) {
                    Text("Not now", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

/** "today", "yesterday" or a date such as "12 Oct", from days since the app was last opened. */
internal fun lastOpenedLabel(daysAgo: Int): String = when (daysAgo) {
    0 -> "today"
    1 -> "yesterday"
    else -> LocalDate.now().minusDays(daysAgo.toLong()).format(DateTimeFormatter.ofPattern("d MMM"))
}
