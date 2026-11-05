package com.se3092.pulsefinance.ui.screens.income

import com.se3092.pulsefinance.ui.theme.pulseColors
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.se3092.pulsefinance.domain.model.Currency
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.InvoiceStatus
import com.se3092.pulsefinance.ui.common.formatDate

private val receivedDateOptions = listOf(0 to "Today", -1 to "Yesterday", -7 to "Last week", -30 to "Last month")
private val dueDateOptions = listOf(7 to "In 1 week", 14 to "In 2 weeks", 30 to "In 30 days", -1 to "Already due")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddIncomeBottomSheet(
    form: IncomeFormState,
    onTitleChange: (String) -> Unit,
    onClientChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onSourceChange: (IncomeSource) -> Unit,
    onCurrencyChange: (Currency) -> Unit,
    onRateChange: (String) -> Unit,
    onCreditedLkrChange: (String) -> Unit = {},
    onToggleCreditedLkrMode: (Boolean) -> Unit = {},
    onStatusChange: (InvoiceStatus) -> Unit,
    onDayOffsetChange: (Int) -> Unit,
    onCryptoLossToggle: (Boolean) -> Unit,
    onRecurringToggle: (Boolean) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val amount = form.amountInput.replace(",", "").toDoubleOrNull() ?: 0.0
    val rate = form.exchangeRateInput.toDoubleOrNull() ?: 0.0
    val convertedLkr = amount * rate

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
    )
    val chipColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (form.isEditing) "Edit income" else "Record Income",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = if (form.isEditing) "The rate you recorded stays unless you change it" else "Money received, or an invoice you're waiting on",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            SectionLabel("INCOME SOURCE")
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                IncomeSource.entries.forEach { source ->
                    val isSelected = form.selectedSource == source
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSourceChange(source) },
                        label = { Text(source.displayName, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                        shape = CircleShape,
                        colors = chipColors
                    )
                }
            }

            // Crypto trades can lose money — record losses so the net position is honest.
            if (form.selectedSource == IncomeSource.CRYPTO) {
                Spacer(modifier = Modifier.height(6.dp))
                ToggleRow(
                    title = if (form.isCryptoLoss) "Realised loss" else "Realised gain",
                    subtitle = "Losses reduce your crypto net and this month's income",
                    checked = form.isCryptoLoss,
                    onCheckedChange = onCryptoLossToggle,
                    accent = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = form.titleInput,
                onValueChange = onTitleChange,
                label = { Text("Title (e.g. Milestone 2 / AdSense Sept)", fontSize = 13.sp) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = form.clientInput,
                onValueChange = onClientChange,
                label = { Text("Client or platform (e.g. LankaRetail / Binance)", fontSize = 13.sp) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(12.dp))

            SectionLabel("CURRENCY")
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Currency.entries.forEach { currency ->
                    FilterChip(
                        selected = form.selectedCurrency == currency,
                        onClick = { onCurrencyChange(currency) },
                        label = { Text(currency.name, fontSize = 12.sp) },
                        shape = CircleShape,
                        colors = chipColors
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = form.amountInput,
                    onValueChange = onAmountChange,
                    label = { Text("Amount (${form.selectedCurrency.name})", fontSize = 13.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    isError = form.errorMessage != null,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1.3f),
                    colors = fieldColors
                )

                if (form.selectedCurrency != Currency.LKR) {
                    if (form.isCreditedLkrMode) {
                        OutlinedTextField(
                            value = form.receivedLkrInput,
                            onValueChange = onCreditedLkrChange,
                            label = { Text("Bank LKR Credited", fontSize = 13.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1.3f),
                            colors = fieldColors
                        )
                    } else {
                        OutlinedTextField(
                            value = form.exchangeRateInput,
                            onValueChange = onRateChange,
                            label = { Text("Rate to LKR", fontSize = 13.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f),
                            colors = fieldColors
                        )
                    }
                }
            }

            if (form.selectedCurrency != Currency.LKR) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (form.rateSourceInfo.isNotBlank()) "Suggested: ${form.rateSourceInfo}" else "Suggested: 305 LKR/USD",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.pulseColors.textTertiary,
                        fontSize = 11.5.sp
                    )
                    Text(
                        text = if (form.isCreditedLkrMode) "Switch to Rate" else "Type Bank LKR",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.5.sp,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onToggleCreditedLkrMode(!form.isCreditedLkrMode) }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            form.errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
            }

            // The rate actually received is stored with the entry, so history never shifts with the market (ADR-0001).
            if (form.selectedCurrency != Currency.LKR && amount > 0 && rate > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Effective rate: ${String.format("%.2f", rate)} LKR/${form.selectedCurrency.name} → Recorded as ≈ LKR ${String.format("%,.0f", convertedLkr)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            SectionLabel("STATUS")
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(InvoiceStatus.RECEIVED to "Received", InvoiceStatus.PENDING_INVOICE to "Invoiced, not paid").forEach { (status, label) ->
                    FilterChip(
                        selected = form.selectedStatus == status,
                        onClick = { onStatusChange(status) },
                        label = { Text(label, fontSize = 12.sp) },
                        shape = CircleShape,
                        colors = chipColors
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            SectionLabel(if (form.selectedStatus == InvoiceStatus.RECEIVED) "RECEIVED" else "PAYMENT DUE")
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                val options = if (form.selectedStatus == InvoiceStatus.RECEIVED) receivedDateOptions else dueDateOptions
                form.editing?.let { original ->
                    FilterChip(
                        selected = form.usesOriginalDate,
                        onClick = {},
                        label = { Text("Keep ${formatDate(original.dateReceivedOrExpected)}", fontSize = 12.sp) },
                        shape = CircleShape,
                        colors = chipColors
                    )
                }
                options.forEach { (offset, label) ->
                    FilterChip(
                        selected = !form.usesOriginalDate && form.dayOffset == offset,
                        onClick = { onDayOffsetChange(offset) },
                        label = { Text(label, fontSize = 12.sp) },
                        shape = CircleShape,
                        colors = chipColors
                    )
                }
            }

            if (form.selectedSource == IncomeSource.SALARY) {
                Spacer(modifier = Modifier.height(6.dp))
                ToggleRow(
                    title = "Monthly salary",
                    subtitle = "Marks this as recurring income",
                    checked = form.isRecurringMonthly,
                    onCheckedChange = onRecurringToggle,
                    accent = MaterialTheme.colorScheme.primaryContainer
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onSubmit,
                enabled = form.amountInput.isNotBlank() && !form.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
                    .shadow(10.dp, CircleShape, spotColor = MaterialTheme.pulseColors.primaryGlow),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                if (form.isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimaryContainer, strokeWidth = 2.dp)
                } else {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = if (form.isEditing) "Save changes" else "Save", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.pulseColors.textTertiary,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp
    )
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accent: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(text = subtitle, fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.onPrimaryContainer, checkedTrackColor = accent, uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant, uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHigh, uncheckedBorderColor = MaterialTheme.pulseColors.textQuiet)
        )
    }
}
