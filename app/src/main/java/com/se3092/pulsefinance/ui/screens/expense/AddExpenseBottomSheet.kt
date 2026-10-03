package com.se3092.pulsefinance.ui.screens.expense

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.se3092.pulsefinance.ui.theme.EmeraldDark
import com.se3092.pulsefinance.ui.theme.EmeraldPrimary
import com.se3092.pulsefinance.ui.theme.FoodColor
import com.se3092.pulsefinance.ui.theme.RentColor
import com.se3092.pulsefinance.ui.theme.SurfaceDark
import com.se3092.pulsefinance.ui.theme.SurfaceElevatedDark

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddExpenseBottomSheet(
    state: ExpenseScreenUiState,
    onTitleChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onCategoryChange: (ExpenseCategory) -> Unit,
    onCommittedToggle: (Boolean) -> Unit,
    onAccountSelect: (String) -> Unit,
    onPresetSelect: (String, Double, ExpenseCategory, Boolean) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quick Expense Entry",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
            }

            Text(
                text = "Tap a preset or enter manually in seconds",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Presets solving Kavindu's friction
            Text(
                text = "FREQUENT PRESETS",
                style = MaterialTheme.typography.labelSmall,
                color = EmeraldPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PresetChip(label = "☕ Cafe LKR 850") {
                    onPresetSelect("Coffee & Pastry", 850.0, ExpenseCategory.FOOD_DINING, false)
                }
                PresetChip(label = "🚗 PickMe LKR 1,450") {
                    onPresetSelect("PickMe Office", 1450.0, ExpenseCategory.TRANSPORT, false)
                }
                PresetChip(label = "🍔 UberEats LKR 2,400") {
                    onPresetSelect("UberEats Dinner", 2400.0, ExpenseCategory.FOOD_DINING, false)
                }
                PresetChip(label = "🏠 Rent LKR 34,000") {
                    onPresetSelect("Apartment Rent", 34000.0, ExpenseCategory.RENT, true)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Amount Field
            OutlinedTextField(
                value = state.amountInput,
                onValueChange = onAmountChange,
                label = { Text("Amount (LKR)") },
                prefix = { Text("LKR ", color = EmeraldPrimary, fontWeight = FontWeight.Bold) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EmeraldPrimary,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Title Field
            OutlinedTextField(
                value = state.titleInput,
                onValueChange = onTitleChange,
                label = { Text("Description / Merchant") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EmeraldPrimary,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Category Selector
            Text(
                text = "CATEGORY",
                style = MaterialTheme.typography.labelSmall,
                color = Color.LightGray,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ExpenseCategory.entries.forEach { category ->
                    FilterChip(
                        selected = state.selectedCategory == category,
                        onClick = { onCategoryChange(category) },
                        label = { Text(category.displayName, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Committed vs Discretionary Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceElevatedDark)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (state.isCommittedInput) "Committed Fixed Cost" else "Discretionary Lifestyle Spend",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (state.isCommittedInput) RentColor else FoodColor
                    )
                    Text(
                        text = if (state.isCommittedInput) "Obligatory (Rent, Bills, Fixed Debits)" else "Flexible (Dining, Rides, Impulses)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Switch(
                    checked = state.isCommittedInput,
                    onCheckedChange = onCommittedToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = RentColor,
                        checkedTrackColor = RentColor.copy(alpha = 0.4f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Account Selector (Cash, Primary Bank, Secondary Account)
            Text(
                text = "PAID VIA ACCOUNT",
                style = MaterialTheme.typography.labelSmall,
                color = Color.LightGray,
                fontWeight = FontWeight.Bold
            )
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
                            selectedContainerColor = Color(0xFF38BDF8),
                            selectedLabelColor = Color.Black
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
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldPrimary,
                    contentColor = Color.Black
                )
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = "Log Expense", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PresetChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceElevatedDark)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text = label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
