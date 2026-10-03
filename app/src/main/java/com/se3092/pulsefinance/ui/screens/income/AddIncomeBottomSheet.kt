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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.se3092.pulsefinance.ui.theme.NeoBorder
import com.se3092.pulsefinance.ui.theme.NeoCard
import com.se3092.pulsefinance.ui.theme.NeoCardLow
import com.se3092.pulsefinance.ui.theme.NeoGreen
import com.se3092.pulsefinance.ui.theme.NeoGreenDark
import com.se3092.pulsefinance.ui.theme.NeoGreenGlow
import com.se3092.pulsefinance.ui.theme.NeoTextPrimary
import com.se3092.pulsefinance.ui.theme.NeoTextSecondary
import com.se3092.pulsefinance.ui.theme.NeoTextTertiary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddIncomeBottomSheet(
    state: IncomeScreenUiState,
    onTitleChange: (String) -> Unit,
    onClientChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onSourceChange: (IncomeSource) -> Unit,
    onCurrencyChange: (Currency) -> Unit,
    onRateChange: (String) -> Unit,
    onStatusChange: (InvoiceStatus) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val amount = state.amountInput.toDoubleOrNull() ?: 0.0
    val rate = state.exchangeRateInput.toDoubleOrNull() ?: 1.0
    val convertedLkr = amount * rate

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
                        text = "Record Income Stream",
                        style = MaterialTheme.typography.titleLarge,
                        color = NeoTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "Realized cash or milestone invoice",
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

            // Income Source Chips
            Text(
                text = "INCOME SOURCE",
                style = MaterialTheme.typography.labelSmall,
                color = NeoTextTertiary,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IncomeSource.entries.forEach { source ->
                    val isSelected = state.selectedSource == source
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSourceChange(source) },
                        label = { Text(source.displayName, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                        shape = CircleShape,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeoGreen,
                            selectedLabelColor = Color.White,
                            containerColor = NeoCardLow,
                            labelColor = NeoTextSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Project / Invoice Description
            OutlinedTextField(
                value = state.titleInput,
                onValueChange = onTitleChange,
                label = { Text("Income Title (e.g. Milestone 2 / AdSense Sept)", fontSize = 13.sp) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeoGreen,
                    unfocusedBorderColor = NeoBorder,
                    focusedTextColor = NeoTextPrimary,
                    unfocusedTextColor = NeoTextPrimary
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Client or Platform
            OutlinedTextField(
                value = state.clientInput,
                onValueChange = onClientChange,
                label = { Text("Client or Platform (e.g. LankaRetail / Binance)", fontSize = 13.sp) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeoGreen,
                    unfocusedBorderColor = NeoBorder,
                    focusedTextColor = NeoTextPrimary,
                    unfocusedTextColor = NeoTextPrimary
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Currency & Amount Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = state.amountInput,
                    onValueChange = onAmountChange,
                    label = { Text("Amount (${state.selectedCurrency.name})", fontSize = 13.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1.5f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeoGreen,
                        unfocusedBorderColor = NeoBorder,
                        focusedTextColor = NeoTextPrimary,
                        unfocusedTextColor = NeoTextPrimary
                    )
                )

                if (state.selectedCurrency != Currency.LKR) {
                    OutlinedTextField(
                        value = state.exchangeRateInput,
                        onValueChange = onRateChange,
                        label = { Text("Rate to LKR", fontSize = 13.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeoGreen,
                            unfocusedBorderColor = NeoBorder,
                            focusedTextColor = NeoTextPrimary,
                            unfocusedTextColor = NeoTextPrimary
                        )
                    )
                }
            }

            // Converted Preview
            if (state.selectedCurrency != Currency.LKR && amount > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(NeoCardLow)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Normalized in LKR: ≈ LKR ${String.format("%,.0f", convertedLkr)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeoGreenDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Invoice Status Selector
            Text(
                text = "INVOICE STATUS",
                style = MaterialTheme.typography.labelSmall,
                color = NeoTextTertiary,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InvoiceStatus.entries.forEach { status ->
                    val isSelected = state.selectedStatus == status
                    FilterChip(
                        selected = isSelected,
                        onClick = { onStatusChange(status) },
                        label = { Text(status.displayName, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                        shape = CircleShape,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeoGreen,
                            selectedLabelColor = Color.White,
                            containerColor = NeoCardLow,
                            labelColor = NeoTextSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Submit Button
            Button(
                onClick = onSubmit,
                enabled = state.amountInput.isNotBlank() && !state.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(10.dp, CircleShape, spotColor = NeoGreenGlow),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeoGreen,
                    contentColor = Color.White
                )
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = "Save Income Stream", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
