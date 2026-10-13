package com.se3092.pulsefinance.ui.screens.expense

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.ui.theme.FoodColor
import com.se3092.pulsefinance.ui.theme.NeoBorder
import com.se3092.pulsefinance.ui.theme.NeoCard
import com.se3092.pulsefinance.ui.theme.NeoCardLow
import com.se3092.pulsefinance.ui.theme.NeoGreen
import com.se3092.pulsefinance.ui.theme.NeoGreenDark
import com.se3092.pulsefinance.ui.theme.NeoTextPrimary
import com.se3092.pulsefinance.ui.theme.NeoTextSecondary
import com.se3092.pulsefinance.ui.theme.NeoTextTertiary
import com.se3092.pulsefinance.ui.theme.RentColor
import com.se3092.pulsefinance.ui.util.TransactionVisuals

/**
 * 3-Tap Speed Logger: tap a preset (fills amount, title, category), confirm the account,
 * tap Log. Manual entry stays available underneath for anything else.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddExpenseBottomSheet(
    state: ExpenseScreenUiState,
    onTitleChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onCategoryChange: (ExpenseCategory) -> Unit,
    onCommittedToggle: (Boolean) -> Unit,
    onAccountSelect: (String) -> Unit,
    onPresetSelect: (ExpensePreset) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = NeoGreen,
        unfocusedBorderColor = NeoBorder,
        focusedTextColor = NeoTextPrimary,
        unfocusedTextColor = NeoTextPrimary,
        focusedLabelColor = NeoGreenDark,
        unfocusedLabelColor = NeoTextTertiary
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = NeoCard
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Quick Expense Entry",
                        style = MaterialTheme.typography.titleLarge,
                        color = NeoTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tap a preset or enter manually in seconds",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeoTextSecondary
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = NeoTextTertiary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            SectionLabel("FREQUENT PRESETS", color = NeoGreenDark)
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExpensePresets.defaults.forEach { preset ->
                    val isSelected = state.titleInput == preset.title &&
                        state.amountInput == preset.amountLkr.toInt().toString()
                    PresetChip(label = preset.chipLabel, isSelected = isSelected) { onPresetSelect(preset) }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.amountInput,
                onValueChange = { input -> onAmountChange(input.filter { it.isDigit() || it == '.' }) },
                label = { Text("Amount (LKR)") },
                prefix = { Text("LKR ", color = NeoGreenDark, fontWeight = FontWeight.Bold) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.titleInput,
                onValueChange = onTitleChange,
                label = { Text("Description / Merchant") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(14.dp))

            SectionLabel("CATEGORY")
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ExpenseCategory.entries.forEach { category ->
                    val visual = TransactionVisuals.forCategory(category)
                    FilterChip(
                        selected = state.selectedCategory == category,
                        onClick = { onCategoryChange(category) },
                        label = { Text(category.displayName, fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(visual.icon, contentDescription = null, modifier = Modifier.size(14.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = NeoCardLow,
                            labelColor = NeoTextPrimary,
                            iconColor = visual.tint,
                            selectedContainerColor = NeoGreen,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

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
                    Text(
                        text = if (state.isCommittedInput) "Committed Fixed Cost" else "Discretionary Lifestyle Spend",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (state.isCommittedInput) RentColor else FoodColor
                    )
                    Text(
                        text = if (state.isCommittedInput) "Obligatory (Rent, Bills, Fixed Debits)" else "Flexible (Dining, Rides, Impulses)",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeoTextSecondary
                    )
                }

                Switch(
                    checked = state.isCommittedInput,
                    onCheckedChange = onCommittedToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = RentColor
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            SectionLabel("PAID VIA ACCOUNT")
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                state.accounts.forEach { account ->
                    FilterChip(
                        selected = state.selectedAccountId == account.id,
                        onClick = { onAccountSelect(account.id) },
                        label = { Text(account.name, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = NeoCardLow,
                            labelColor = NeoTextPrimary,
                            selectedContainerColor = NeoTextPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onSubmit,
                enabled = (state.amountInput.toDoubleOrNull() ?: 0.0) > 0 && !state.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeoGreen,
                    contentColor = Color.White
                )
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = "Log Expense", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String, color: Color = NeoTextTertiary) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp
    )
}

@Composable
private fun PresetChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = CircleShape,
        color = if (isSelected) NeoGreen.copy(alpha = 0.12f) else NeoCardLow,
        border = BorderStroke(1.dp, if (isSelected) NeoGreen else NeoBorder),
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            color = NeoTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
        )
    }
}
