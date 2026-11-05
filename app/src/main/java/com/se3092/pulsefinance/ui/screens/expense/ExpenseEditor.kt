package com.se3092.pulsefinance.ui.screens.expense

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.se3092.pulsefinance.domain.model.Account
import com.se3092.pulsefinance.domain.model.DAY_MILLIS
import com.se3092.pulsefinance.domain.model.Expense
import com.se3092.pulsefinance.domain.model.ExpenseCategory
import com.se3092.pulsefinance.domain.repository.ExpenseRepository
import com.se3092.pulsefinance.ui.common.categoryIllustration
import com.se3092.pulsefinance.ui.common.formatDate
import com.se3092.pulsefinance.ui.common.lkr
import com.se3092.pulsefinance.ui.common.presetIllustration
import com.se3092.pulsefinance.ui.components.NoticeTone
import com.se3092.pulsefinance.ui.components.UiNotice
import com.se3092.pulsefinance.ui.theme.pulseColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

/**
 * The form for correcting a logged expense (FR-EXP-8). It works on the stored [original], so what
 * the form doesn't show (notes, who owns it) survives the save, and the date is the one he logged
 * unless he picks another.
 */
data class ExpenseEditorState(
    val original: Expense,
    val title: String = original.title,
    val amountInput: String = formatAmountInput(original.amountLkr),
    val category: ExpenseCategory = original.category,
    val isCommitted: Boolean = original.isCommitted,
    val accountId: String = original.paymentAccountId,
    val timestamp: Long = original.timestamp,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
) {
    val amountLkr: Double? get() = amountInput.replace(",", "").trim().toDoubleOrNull()

    /** Choosing a category re-suggests committed vs discretionary, as when logging, and stays overridable. */
    fun withCategory(newCategory: ExpenseCategory) =
        copy(category = newCategory, isCommitted = newCategory.isTypicallyCommitted, errorMessage = null)

    /**
     * The date picker answers with midnight UTC of the chosen day. Keep the day he picked and the
     * time of day the expense already had, so the ledger order within a day doesn't jump around.
     */
    fun withPickedDate(utcMidnightMillis: Long): ExpenseEditorState {
        val picked = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = utcMidnightMillis }
        val result = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(picked.get(Calendar.YEAR), picked.get(Calendar.MONTH), picked.get(Calendar.DAY_OF_MONTH))
        }
        return copy(timestamp = result.timeInMillis, errorMessage = null)
    }

    /**
     * Why this can't be saved yet, or null. An expense can't be moved onto a frozen account here:
     * charging one goes through the Freeze Break prompt when logging (FR-ACC-3, FR-ACC-4).
     */
    fun validationError(accounts: List<Account>, now: Long): String? {
        val amount = amountLkr
        val chosen = accounts.find { it.id == accountId }
        return when {
            amount == null || amount <= 0 -> "Enter an amount greater than zero"
            timestamp > now + DAY_MILLIS -> "An expense can't be dated in the future"
            chosen?.isFrozen == true && chosen.id != original.paymentAccountId ->
                "${chosen.name} is frozen. Pick another account, or log a new expense to break the freeze."
            else -> null
        }
    }

    /** The expense to save, or null when [validationError] applies. */
    fun toExpense(accounts: List<Account>, now: Long): Expense? {
        if (validationError(accounts, now) != null) return null
        val account = accounts.find { it.id == accountId }
        return original.copy(
            title = title.trim().ifBlank { category.displayName },
            amountLkr = amountLkr!!,
            category = category,
            isCommitted = isCommitted,
            paymentAccountId = account?.id ?: original.paymentAccountId,
            paymentAccountName = account?.name ?: original.paymentAccountName,
            timestamp = timestamp
        )
    }
}

/** An amount as he would type it: no trailing ".0", no grouping. */
internal fun formatAmountInput(amount: Double): String =
    if (amount == amount.toLong().toDouble()) amount.toLong().toString() else amount.toString()

/**
 * The edit-expense state shared by the screens that list expenses (Dashboard "Lately", Analytics
 * ledger), so the two behave identically. Saving replaces the stored record by id.
 */
class ExpenseEditSession(
    private val scope: CoroutineScope,
    private val expenses: ExpenseRepository,
    private val notify: (UiNotice) -> Unit
) {
    private val _state = MutableStateFlow<ExpenseEditorState?>(null)
    val state: StateFlow<ExpenseEditorState?> = _state.asStateFlow()

    fun start(userId: String, expenseId: String) {
        scope.launch {
            val expense = expenses.getExpenses(userId).first().find { it.id == expenseId } ?: run {
                notify(UiNotice("Couldn't open that expense", "It may have been deleted", NoticeTone.ERROR))
                return@launch
            }
            _state.value = ExpenseEditorState(expense)
        }
    }

    fun change(transform: (ExpenseEditorState) -> ExpenseEditorState) = _state.update { it?.let(transform) }

    fun dismiss() {
        _state.value = null
    }

    fun save(accounts: List<Account>) {
        val current = _state.value ?: return
        val updated = current.toExpense(accounts, System.currentTimeMillis()) ?: run {
            _state.update { it?.copy(errorMessage = current.validationError(accounts, System.currentTimeMillis())) }
            return
        }
        scope.launch {
            _state.update { it?.copy(isSubmitting = true) }
            expenses.saveExpense(updated).fold(
                onSuccess = {
                    _state.value = null
                    notify(UiNotice("Expense updated", "${lkr(updated.amountLkr)} · ${updated.title}", illustration = presetIllustration(updated.title, updated.category)))
                },
                onFailure = { e ->
                    _state.update { it?.copy(isSubmitting = false, errorMessage = e.localizedMessage ?: "Couldn't save the changes") }
                }
            )
        }
    }
}

/** Correct an expense's amount, title, category, date or account (FR-EXP-8). */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExpenseEditorSheet(
    state: ExpenseEditorState,
    accounts: List<Account>,
    onChange: ((ExpenseEditorState) -> ExpenseEditorState) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDatePicker by remember { mutableStateOf(false) }
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
    )
    val chipColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 22.dp)
                .padding(bottom = 28.dp)
        ) {
            Text(
                text = "Edit expense",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Totals, Safe-to-Spend and your goal pace update to match",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.amountInput,
                onValueChange = { v -> onChange { it.copy(amountInput = v, errorMessage = null) } },
                label = { Text("Amount") },
                prefix = { Text("LKR ", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                isError = state.errorMessage != null,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )
            state.errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.title,
                onValueChange = { v -> onChange { it.copy(title = v) } },
                label = { Text("What was it?") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(16.dp))
            EditorLabel("CATEGORY")
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                ExpenseCategory.entries.forEach { category ->
                    FilterChip(
                        selected = state.category == category,
                        onClick = { onChange { it.withCategory(category) } },
                        label = { Text(category.displayName, fontSize = 12.sp) },
                        leadingIcon = {
                            Image(painter = painterResource(categoryIllustration(category)), contentDescription = null, modifier = Modifier.size(22.dp))
                        },
                        colors = chipColors
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (state.isCommitted) "Committed cost" else "Discretionary spend",
                    fontWeight = FontWeight.Bold,
                    color = if (state.isCommitted) MaterialTheme.pulseColors.rent else MaterialTheme.colorScheme.primary
                )
                Switch(
                    checked = state.isCommitted,
                    onCheckedChange = { v -> onChange { it.copy(isCommitted = v) } },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        checkedTrackColor = MaterialTheme.pulseColors.rent
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            EditorLabel("WHEN")
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(formatDate(state.timestamp), color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                TextButton(onClick = { showDatePicker = true }) { Text("Change date") }
            }

            if (accounts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                EditorLabel("PAID FROM")
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    accounts.forEach { account ->
                        FilterChip(
                            selected = state.accountId == account.id,
                            onClick = { onChange { it.copy(accountId = account.id, errorMessage = null) } },
                            label = { Text(account.name, fontSize = 12.sp) },
                            colors = chipColors
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onSave,
                enabled = state.amountInput.isNotBlank() && !state.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                if (state.isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimaryContainer, strokeWidth = 2.dp)
                } else {
                    Text("Save changes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }

    if (showDatePicker) {
        val now = System.currentTimeMillis()
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = utcMidnightOf(state.timestamp),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis <= utcMidnightOf(now)
            }
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { picked -> onChange { it.withPickedDate(picked) } }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = pickerState) }
    }
}

/** The date picker works in UTC: the local calendar day of [millis], as midnight UTC. */
private fun utcMidnightOf(millis: Long): Long {
    val local = Calendar.getInstance().apply { timeInMillis = millis }
    return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        clear()
        set(local.get(Calendar.YEAR), local.get(Calendar.MONTH), local.get(Calendar.DAY_OF_MONTH))
    }.timeInMillis
}

@Composable
private fun EditorLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.pulseColors.textTertiary,
        fontWeight = FontWeight.Bold,
        fontSize = 10.5.sp,
        letterSpacing = 0.6.sp
    )
}
