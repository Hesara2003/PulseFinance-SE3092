package com.se3092.pulsefinance.ui.screens.expense

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.se3092.pulsefinance.ui.common.presetIllustration
import com.se3092.pulsefinance.ui.common.expenseCategoryVisual
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
import com.se3092.pulsefinance.domain.model.Account
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.ui.common.compactAmount
import com.se3092.pulsefinance.ui.theme.OnAccent
import com.se3092.pulsefinance.ui.theme.NeoContainer
import com.se3092.pulsefinance.ui.theme.NeoBorder
import com.se3092.pulsefinance.ui.theme.NeoCard
import com.se3092.pulsefinance.ui.theme.NeoCardLow
import com.se3092.pulsefinance.ui.theme.NeoGreen
import com.se3092.pulsefinance.ui.theme.NeoGreenDark
import com.se3092.pulsefinance.ui.theme.NeoRose
import com.se3092.pulsefinance.ui.theme.NeoTextPrimary
import com.se3092.pulsefinance.ui.theme.NeoTextSecondary
import com.se3092.pulsefinance.ui.theme.NeoTextTertiary
import com.se3092.pulsefinance.ui.theme.RentColor

private val backdateOptions = listOf(0 to "Today", 1 to "Yesterday", 2 to "2 days ago", 7 to "Last week")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddExpenseBottomSheet(
    form: AddExpenseFormState,
    presets: List<ExpensePreset>,
    accounts: List<Account>,
    onTitleChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onCategoryChange: (ExpenseCategory) -> Unit,
    onCommittedToggle: (Boolean) -> Unit,
    onAccountSelect: (String) -> Unit,
    onDaysAgoSelect: (Int) -> Unit,
    onPresetSelect: (ExpensePreset) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = NeoGreen,
        unfocusedBorderColor = NeoBorder,
        focusedTextColor = NeoTextPrimary,
        unfocusedTextColor = NeoTextPrimary
    )
    val chipColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = NeoGreen,
        selectedLabelColor = OnAccent,
        labelColor = NeoTextSecondary
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
                .padding(horizontal = 22.dp)
                .padding(bottom = 28.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Log an Expense",
                        style = MaterialTheme.typography.titleLarge,
                        color = NeoTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "Tap a habit or type the amount — done in seconds",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeoTextSecondary,
                        fontSize = 13.sp
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = NeoTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SectionLabel("YOUR FREQUENT SPENDS")
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEach { preset ->
                    Surface(
                        shape = CircleShape,
                        color = NeoCardLow,
                        border = BorderStroke(1.dp, NeoBorder),
                        modifier = Modifier.clickable { onPresetSelect(preset) }
                    ) {
                        Row(
                            modifier = Modifier.padding(start = 6.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Image(
                                painter = painterResource(presetIllustration(preset.title, preset.category)),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "${preset.title} · ${compactAmount(preset.amountLkr)}",
                                color = NeoTextPrimary,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = form.amountInput,
                onValueChange = onAmountChange,
                label = { Text("Amount") },
                prefix = { Text("LKR ", color = NeoGreenDark, fontWeight = FontWeight.Bold) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                isError = form.errorMessage != null,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )

            form.errorMessage?.let {
                Text(text = it, color = NeoRose, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = form.titleInput,
                onValueChange = onTitleChange,
                label = { Text("What was it? (optional)") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionLabel("CATEGORY")
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                ExpenseCategory.entries.forEach { category ->
                    FilterChip(
                        selected = form.category == category,
                        onClick = { onCategoryChange(category) },
                        label = { Text(category.displayName, fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = expenseCategoryVisual(category).icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = chipColors
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Committed vs discretionary — pre-set from the category, overridable.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NeoCardLow)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (form.isCommitted) "Committed cost" else "Discretionary spend",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (form.isCommitted) RentColor else NeoGreenDark
                    )
                    Text(
                        text = if (form.isCommitted) "Must pay: rent, bills, fixed debits" else "Your choice: dining, rides, treats",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeoTextSecondary
                    )
                }
                Switch(
                    checked = form.isCommitted,
                    onCheckedChange = onCommittedToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = OnAccent,
                        checkedTrackColor = RentColor,
                        uncheckedThumbColor = NeoTextSecondary,
                        uncheckedTrackColor = NeoContainer,
                        uncheckedBorderColor = NeoTextTertiary
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            SectionLabel("WHEN")
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                backdateOptions.forEach { (days, label) ->
                    FilterChip(
                        selected = form.daysAgo == days,
                        onClick = { onDaysAgoSelect(days) },
                        label = { Text(label, fontSize = 12.sp) },
                        colors = chipColors
                    )
                }
            }

            if (accounts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                SectionLabel("PAID FROM")
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    accounts.forEach { account ->
                        FilterChip(
                            selected = form.accountId == account.id,
                            onClick = { onAccountSelect(account.id) },
                            label = { Text(account.name, fontSize = 12.sp) },
                            colors = chipColors
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onSubmit,
                enabled = form.amountInput.isNotBlank() && !form.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = NeoGreen, contentColor = OnAccent)
            ) {
                if (form.isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = OnAccent, strokeWidth = 2.dp)
                } else {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = "Log Expense", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
        fontSize = 10.5.sp,
        letterSpacing = 0.6.sp
    )
}
