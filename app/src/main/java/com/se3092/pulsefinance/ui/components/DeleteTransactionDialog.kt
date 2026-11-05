package com.se3092.pulsefinance.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.se3092.pulsefinance.ui.common.TransactionItem
import com.se3092.pulsefinance.ui.common.lkr
import com.se3092.pulsefinance.ui.common.transactionIllustration
import kotlin.math.abs

/** Asks before an expense is deleted: deleting can't be undone, and one stray tap shouldn't do it. */
@Composable
fun DeleteTransactionDialog(
    transaction: TransactionItem,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Image(
                painter = painterResource(transactionIllustration(transaction)),
                contentDescription = null,
                modifier = Modifier.size(56.dp)
            )
        },
        title = { Text("Delete this expense?", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
        text = {
            Text(
                "${transaction.title} · ${lkr(abs(transaction.amountLkr))}. It will be removed from your totals, " +
                    "Safe-to-Spend and goal pace. This can't be undone.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp)
    )
}
