package com.se3092.pulsefinance.ui.screens.income

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.se3092.pulsefinance.ui.theme.Dusk
import com.se3092.pulsefinance.ui.theme.InkHigher
import com.se3092.pulsefinance.ui.theme.Mist
import com.se3092.pulsefinance.ui.theme.OnAccent
import com.se3092.pulsefinance.ui.theme.NeoBorder
import com.se3092.pulsefinance.ui.theme.NeoCard
import com.se3092.pulsefinance.ui.theme.NeoCardLow
import com.se3092.pulsefinance.ui.theme.NeoGreen
import com.se3092.pulsefinance.ui.theme.NeoGreenDark
import com.se3092.pulsefinance.ui.theme.NeoGreenGlow
import com.se3092.pulsefinance.ui.theme.NeoRose
import com.se3092.pulsefinance.ui.theme.NeoTextPrimary
import com.se3092.pulsefinance.ui.theme.NeoTextSecondary
import com.se3092.pulsefinance.ui.theme.NeoTextTertiary

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
        focusedBorderColor = NeoGreen,
        unfocusedBorderColor = NeoBorder,
        focusedTextColor = NeoTextPrimary,
        unfocusedTextColor = NeoTextPrimary
    )
    val chipColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = NeoGreen,
        selectedLabelColor = OnAccent,
        containerColor = NeoCardLow,
        labelColor = NeoTextSecondary
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = NeoCard,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(NeoBorder)
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
                        text = "Record Income",
                        style = MaterialTheme.typography.titleLarge,
                        color = NeoTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "Money received, or an invoice you're waiting on",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeoTextSecondary,
                        fontSize = 13.sp
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = NeoTextSecondary)
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
                    accent = NeoRose
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

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = form.amountInput,
                    onValueChange = onAmountChange,
                    label = { Text("Amount (${form.selectedCurrency.name})", fontSize = 13.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    isError = form.errorMessage != null,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1.5f),
                    colors = fieldColors
                )

                if (form.selectedCurrency != Currency.LKR) {
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

            form.errorMessage?.let {
                Text(text = it, color = NeoRose, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
            }

            // The rate actually received is stored with the entry, so history never shifts with the market.
            if (form.selectedCurrency != Currency.LKR && amount > 0 && rate > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(NeoCardLow)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Recorded as ≈ LKR ${String.format("%,.0f", convertedLkr)} at the rate you entered",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeoGreenDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
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
                options.forEach { (offset, label) ->
                    FilterChip(
                        selected = form.dayOffset == offset,
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
                    accent = NeoGreen
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onSubmit,
                enabled = form.amountInput.isNotBlank() && !form.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(10.dp, CircleShape, spotColor = NeoGreenGlow),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeoGreen,
                    contentColor = OnAccent
                )
            ) {
                if (form.isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = OnAccent, strokeWidth = 2.dp)
                } else {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = "Save", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
        color = NeoTextTertiary,
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
            .background(NeoCardLow)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NeoTextPrimary)
            Text(text = subtitle, fontSize = 11.5.sp, color = NeoTextSecondary)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = OnAccent, checkedTrackColor = accent, uncheckedThumbColor = Mist, uncheckedTrackColor = InkHigher, uncheckedBorderColor = Dusk)
        )
    }
}
