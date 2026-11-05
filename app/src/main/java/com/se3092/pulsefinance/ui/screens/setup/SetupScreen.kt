package com.se3092.pulsefinance.ui.screens.setup

import com.se3092.pulsefinance.ui.theme.pulseColors
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeVariability
import com.se3092.pulsefinance.domain.model.MAX_RUNWAY_MONTHS
import com.se3092.pulsefinance.domain.model.MIN_RUNWAY_MONTHS
import com.se3092.pulsefinance.ui.common.lkr

/** Three short steps after a new account's first sign-in (FR-AUTH-4, FR-AUTH-5). */
@Composable
fun SetupScreen(
    viewModel: SetupViewModel,
    onFinished: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isDone) {
        if (state.isDone) onFinished()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .imePadding()
            .padding(horizontal = 20.dp)
    ) {
        // Progress + skip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (state.step > 0) {
                IconButton(onClick = viewModel::back) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(SetupUiState.LAST_STEP + 1) { index ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(5.dp)
                            .clip(CircleShape)
                            .background(if (index <= state.step) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.pulseColors.track)
                    )
                }
            }
            TextButton(onClick = viewModel::skip, enabled = !state.isSaving) {
                Text("Skip", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "STEP ${state.step + 1} OF ${SetupUiState.LAST_STEP + 1}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            when (state.step) {
                0 -> SalaryStep(state, viewModel)
                1 -> SourcesStep(state, viewModel)
                else -> CommitmentsStep(state, viewModel)
            }
            state.errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = viewModel::next,
            enabled = !state.isSaving,
            shape = CircleShape,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .heightIn(min = 56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
        ) {
            if (state.isSaving) {
                CircularProgressIndicator(modifier = Modifier.size(22.dp), color = MaterialTheme.colorScheme.onPrimaryContainer, strokeWidth = 2.5.dp)
            } else {
                Text(
                    text = if (state.isLastStep) "Finish setup" else "Continue",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun StepHeader(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        Text(text = subtitle, fontSize = 13.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 19.sp)
    }
}

@Composable
private fun SalaryStep(state: SetupUiState, viewModel: SetupViewModel) {
    StepHeader(
        title = "What's your monthly salary?",
        subtitle = "Your take-home pay after EPF and APIT. Leave it blank if you don't have a salaried job."
    )
    OutlinedTextField(
        value = state.salaryInput,
        onValueChange = viewModel::onSalaryChanged,
        label = { Text("Monthly take-home") },
        prefix = { Text("LKR ", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = setupFieldColors()
    )
}

@Composable
private fun SourcesStep(state: SetupUiState, viewModel: SetupViewModel) {
    StepHeader(
        title = "Where does your money come from?",
        subtitle = "Pick every source you earn from, and say how predictable each one is. " +
            "Volatile sources are treated more cautiously."
    )
    IncomeSource.entries.forEach { source ->
        val variability = state.sources[source]
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, if (variability != null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(start = 6.dp, end = 14.dp, top = 4.dp, bottom = 12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { viewModel.toggleSource(source) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = variability != null,
                        onCheckedChange = { viewModel.toggleSource(source) },
                        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primaryContainer, checkmarkColor = MaterialTheme.colorScheme.onPrimaryContainer)
                    )
                    Text(source.displayName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }
                if (variability != null) {
                    Row(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IncomeVariability.entries.forEach { option ->
                            SetupPill(
                                label = option.displayName,
                                isSelected = option == variability,
                                onClick = { viewModel.setVariability(source, option) }
                            )
                        }
                    }
                    Text(
                        text = variability.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.pulseColors.textTertiary,
                        modifier = Modifier.padding(start = 10.dp, top = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CommitmentsStep(state: SetupUiState, viewModel: SetupViewModel) {
    StepHeader(
        title = "What do you pay every month?",
        subtitle = "Rent, bills and memberships you pay no matter what. They're logged as this month's " +
            "committed spending, so Safe-to-Spend is right from day one."
    )
    state.commitments.forEach { draft ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = draft.title,
                onValueChange = { viewModel.onCommitmentTitleChanged(draft.id, it) },
                placeholder = { Text("e.g. Rent", color = MaterialTheme.pulseColors.textTertiary) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1.2f),
                colors = setupFieldColors()
            )
            OutlinedTextField(
                value = draft.amountInput,
                onValueChange = { viewModel.onCommitmentAmountChanged(draft.id, it) },
                placeholder = { Text("34,000", color = MaterialTheme.pulseColors.textTertiary) },
                prefix = { Text("LKR ", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f),
                colors = setupFieldColors()
            )
            IconButton(onClick = { viewModel.removeCommitment(draft.id) }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Remove ${draft.title.ifBlank { "commitment" }}", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }

    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val existing = state.commitments.map { it.title.trim().lowercase() }.toSet()
        CommitmentSuggestion.entries
            .filterNot { it.title.lowercase() in existing }
            .forEach { suggestion ->
                SetupPill(label = "+ ${suggestion.title}", isSelected = false, onClick = { viewModel.addCommitment(suggestion) })
            }
        SetupPill(label = "+ Other", isSelected = false, onClick = { viewModel.addCommitment() })
    }

    if (state.commitmentsTotalLkr > 0) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Committed every month", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                Text(lkr(state.commitmentsTotalLkr), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }

    RunwaySection(state, viewModel)
}

/** Emergency buffer: how many months of the costs above to keep aside in case income stops (FR-AUTH-6). */
@Composable
private fun RunwaySection(state: SetupUiState, viewModel: SetupViewModel) {
    Text(
        text = "Your safety buffer",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )
    Text(
        text = "If your income stopped, how many months of these costs would you like set aside? " +
            "You can change this later from Today.",
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        (MIN_RUNWAY_MONTHS..MAX_RUNWAY_MONTHS).forEach { months ->
            SetupPill(
                label = if (months == 1) "1 month" else "$months months",
                isSelected = months == state.runwayMonths,
                onClick = { viewModel.onRunwayMonthsChanged(months) }
            )
        }
    }
    if (state.commitmentsTotalLkr > 0) {
        Text(
            text = "${state.runwayMonths} months of your costs is ${lkr(state.runwayTargetLkr)}.",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
    OutlinedTextField(
        value = state.runwaySavedInput,
        onValueChange = viewModel::onRunwaySavedChanged,
        label = { Text("Already set aside (optional)") },
        placeholder = { Text("11,200", color = MaterialTheme.pulseColors.textTertiary) },
        prefix = { Text("LKR ", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = setupFieldColors()
    )
}

@Composable
private fun SetupPill(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun setupFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
)
