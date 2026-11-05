package com.se3092.pulsefinance.ui.screens.income

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.se3092.pulsefinance.domain.model.ApitTaxBand
import com.se3092.pulsefinance.domain.usecase.CalculatePayslipUseCase
import com.se3092.pulsefinance.ui.common.lkr
import com.se3092.pulsefinance.ui.theme.NeoBorder
import com.se3092.pulsefinance.ui.theme.NeoCard
import com.se3092.pulsefinance.ui.theme.NeoCardLow
import com.se3092.pulsefinance.ui.theme.NeoGreen
import com.se3092.pulsefinance.ui.theme.NeoGreenDark
import com.se3092.pulsefinance.ui.theme.NeoRose
import com.se3092.pulsefinance.ui.theme.NeoTextPrimary
import com.se3092.pulsefinance.ui.theme.NeoTextSecondary
import com.se3092.pulsefinance.ui.theme.NeoTextTertiary
import com.se3092.pulsefinance.ui.theme.OnAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayslipBottomSheet(
    calculator: CalculatePayslipUseCase,
    apitBands: List<ApitTaxBand>,
    onSaveSalary: (gross: Double, takeHome: Double, employeeEpf: Double, employerEpf: Double) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var grossInput by remember { mutableStateOf("140000") }
    var employeeEpfInput by remember { mutableStateOf("") }
    var apitTaxInput by remember { mutableStateOf("") }
    var otherDeductionsInput by remember { mutableStateOf("") }

    val grossAmount = grossInput.replace(",", "").toDoubleOrNull() ?: 0.0
    val customEpf = employeeEpfInput.replace(",", "").toDoubleOrNull()
    val customApit = apitTaxInput.replace(",", "").toDoubleOrNull()
    val otherDeductions = otherDeductionsInput.replace(",", "").toDoubleOrNull() ?: 0.0

    val payslip = remember(grossAmount, customEpf, customApit, otherDeductions, apitBands) {
        calculator.calculate(
            grossSalaryLkr = grossAmount,
            customEmployeeEpf = customEpf,
            customApitTax = customApit,
            otherDeductions = otherDeductions,
            bands = apitBands
        )
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = NeoGreen,
        unfocusedBorderColor = NeoBorder,
        focusedTextColor = NeoTextPrimary,
        unfocusedTextColor = NeoTextPrimary
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
                        text = "Sri Lankan Payslip & EPF",
                        style = MaterialTheme.typography.titleLarge,
                        color = NeoTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "Gross salary, APIT tax bands, and EPF accumulation",
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

            // Gross Salary Input
            OutlinedTextField(
                value = grossInput,
                onValueChange = { grossInput = it },
                label = { Text("Gross Monthly Salary (LKR)", fontSize = 13.sp) },
                prefix = { Text("LKR ", color = NeoGreenDark, fontWeight = FontWeight.Bold) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Deductions & Tax (Editable per FR-LK-1)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = employeeEpfInput.ifBlank { payslip.employeeEpfLkr.toLong().toString() },
                    onValueChange = { employeeEpfInput = it },
                    label = { Text("EPF Employee (8%)", fontSize = 12.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f),
                    colors = fieldColors
                )
                OutlinedTextField(
                    value = apitTaxInput.ifBlank { payslip.apitTaxLkr.toLong().toString() },
                    onValueChange = { apitTaxInput = it },
                    label = { Text("APIT Income Tax", fontSize = 12.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f),
                    colors = fieldColors
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Take Home Net Pay Callout
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = NeoCardLow,
                border = BorderStroke(1.dp, NeoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ESTIMATED TAKE-HOME PAY",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeoTextTertiary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = lkr(payslip.takeHomeSalaryLkr),
                        style = MaterialTheme.typography.headlineMedium,
                        color = NeoGreenDark,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp
                    )
                    Text(
                        text = "Credited to bank checking account on the 25th",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeoTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // EPF Accumulation Wealth Card (FR-LK-3)
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = NeoGreen.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, NeoGreen.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(NeoGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Savings, contentDescription = null, tint = OnAccent, modifier = Modifier.size(22.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "EPF Wealth Building (+20%)",
                            style = MaterialTheme.typography.titleSmall,
                            color = NeoTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "+${lkr(payslip.monthlyTotalEpfLkr)} / mo (${lkr(payslip.employeeEpfLkr)} employee + ${lkr(payslip.employerEpfLkr)} employer 12%) accumulates in your government EPF account.",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeoTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Button: Save to Inflows
            Button(
                onClick = {
                    onSaveSalary(
                        payslip.grossSalaryLkr,
                        payslip.takeHomeSalaryLkr,
                        payslip.employeeEpfLkr,
                        payslip.employerEpfLkr
                    )
                },
                enabled = payslip.takeHomeSalaryLkr > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeoGreen,
                    contentColor = OnAccent,
                    disabledContainerColor = NeoBorder
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text(
                        text = "Record Take-Home Salary (${lkr(payslip.takeHomeSalaryLkr)})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
