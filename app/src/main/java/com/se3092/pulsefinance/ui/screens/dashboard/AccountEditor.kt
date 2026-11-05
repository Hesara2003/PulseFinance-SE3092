package com.se3092.pulsefinance.ui.screens.dashboard

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.se3092.pulsefinance.domain.model.Account
import com.se3092.pulsefinance.domain.model.AccountType

/**
 * The add/edit form for one account (FR-ACC-8, FR-ACC-9). [original] is the account being edited,
 * so what the form doesn't show (Spending Freeze, Freeze Breaks) survives saving.
 */
data class AccountEditorState(
    val original: Account? = null,
    val name: String = AccountType.BANK_PRIMARY.displayName,
    val type: AccountType = AccountType.BANK_PRIMARY,
    val balanceInput: String = "",
    val numberInput: String = "",
    val isAutoDebitSource: Boolean = false,
    val isConfirmingDelete: Boolean = false,
    val errorMessage: String? = null
) {
    val isNew: Boolean get() = original == null

    /** Accounts at a bank have a number and can be an auto-debit source; cash and a wallet do not. */
    val isBank: Boolean get() = type.isBank()

    /** A blank balance means LKR 0. It may be negative (a card he owes on); it is his own record (C-13). */
    val balanceLkr: Double?
        get() = if (balanceInput.isBlank()) 0.0 else balanceInput.replace(",", "").trim().toDoubleOrNull()

    val validationError: String?
        get() = when {
            name.isBlank() -> "Give the account a name"
            name.trim().length > MAX_NAME_LENGTH -> "Keep the name under $MAX_NAME_LENGTH characters"
            balanceLkr == null -> "Enter the balance as a number, or leave it blank for LKR 0"
            isBank && numberInput.trim().length > MAX_NUMBER_LENGTH -> "An account number is at most $MAX_NUMBER_LENGTH characters"
            else -> null
        }

    /**
     * Choosing a type renames the account to match while the name is still the previous type's
     * default, so adding a Cash account doesn't leave it called "Primary Bank Account".
     */
    fun withType(newType: AccountType): AccountEditorState {
        val nameIsDefault = name.isBlank() || name == type.displayName
        return copy(
            type = newType,
            name = if (nameIsDefault) newType.displayName else name,
            // A secondary account is the auto-debit account by definition; other bank types keep his choice.
            isAutoDebitSource = newType == AccountType.BANK_SECONDARY || (isAutoDebitSource && newType.isBank()),
            errorMessage = null
        )
    }

    /** The account to save, or null when the form isn't valid. */
    fun toAccount(userId: String): Account? {
        if (validationError != null) return null
        val base = original ?: Account(userId = userId, name = name, type = type)
        return base.copy(
            userId = userId,
            name = name.trim(),
            type = type,
            balanceLkr = balanceLkr!!,
            accountNumber = if (isBank) numberInput.trim() else "",
            isAutoDebitSource = isBank && isAutoDebitSource
        )
    }

    companion object {
        const val MAX_NAME_LENGTH = 60
        const val MAX_NUMBER_LENGTH = 34

        fun forNewAccount() = AccountEditorState()

        fun editing(account: Account) = AccountEditorState(
            original = account,
            name = account.name,
            type = account.type,
            balanceInput = if (account.balanceLkr == 0.0) "" else "%.2f".format(java.util.Locale.US, account.balanceLkr).removeSuffix(".00"),
            numberInput = account.accountNumber,
            isAutoDebitSource = account.isAutoDebitSource
        )
    }
}

private fun AccountType.isBank(): Boolean = this == AccountType.BANK_PRIMARY || this == AccountType.BANK_SECONDARY

/** Add or edit an account, and set what it holds (FR-ACC-8, FR-ACC-9). Deleting asks first. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AccountEditorSheet(
    state: AccountEditorState,
    onChange: ((AccountEditorState) -> AccountEditorState) -> Unit,
    onSave: () -> Unit,
    onRequestDelete: () -> Unit,
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (state.isNew) "Add an account" else "Edit ${state.original?.name.orEmpty()}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Balances are your own record. Nothing is read from a bank, and logging an expense doesn't change them.",
                fontSize = 12.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AccountType.entries.forEach { type ->
                    FilterChip(
                        selected = state.type == type,
                        onClick = { onChange { it.withType(type) } },
                        label = { Text(type.displayName, fontSize = 12.sp) },
                        colors = chipColors
                    )
                }
            }

            OutlinedTextField(
                value = state.name,
                onValueChange = { v -> onChange { it.copy(name = v, errorMessage = null) } },
                label = { Text("Name") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )
            OutlinedTextField(
                value = state.balanceInput,
                onValueChange = { v -> onChange { it.copy(balanceInput = v, errorMessage = null) } },
                label = { Text("What it holds now (optional)") },
                prefix = { Text("LKR ", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )
            if (state.isBank) {
                OutlinedTextField(
                    value = state.numberInput,
                    onValueChange = { v -> onChange { it.copy(numberInput = v, errorMessage = null) } },
                    label = { Text("Account number (optional)") },
                    supportingText = { Text("Shown masked; revealing it needs your fingerprint or PIN") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Pays my subscriptions", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                        Text("An auto-debit account, checked first in the subscription audit", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = state.isAutoDebitSource,
                        onCheckedChange = { v -> onChange { it.copy(isAutoDebitSource = v) } }
                    )
                }
            }

            state.errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(if (state.isNew) "Add account" else "Save changes", fontWeight = FontWeight.Bold)
            }

            if (!state.isNew) {
                if (state.isConfirmingDelete) {
                    Text(
                        text = "Delete ${state.original?.name.orEmpty()}? Expenses you logged against it stay, and keep showing its name.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = { onChange { it.copy(isConfirmingDelete = false) } },
                            modifier = Modifier.weight(1f)
                        ) { Text("Keep it") }
                        Button(
                            onClick = onConfirmDelete,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) { Text("Delete", fontWeight = FontWeight.Bold) }
                    }
                } else {
                    TextButton(onClick = onRequestDelete, modifier = Modifier.fillMaxWidth()) {
                        Text("Delete this account", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
