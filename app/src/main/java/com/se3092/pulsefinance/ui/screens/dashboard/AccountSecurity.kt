package com.se3092.pulsefinance.ui.screens.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException

/**
 * What the account menu needs (FR-AUTH-9, FR-AUTH-10): whether the email is verified, and the
 * state of the delete-my-account confirmation.
 */
data class AccountSecurityState(
    val email: String? = null,
    val isEmailVerified: Boolean = true,
    /** Email-and-password accounts confirm with the password; Google accounts pick the account again. */
    val usesPassword: Boolean = true,
    val isDeleteOpen: Boolean = false,
    val passwordInput: String = "",
    val isDeleting: Boolean = false,
    val errorMessage: String? = null
) {
    val canConfirmDelete: Boolean get() = !isDeleting && (!usesPassword || passwordInput.isNotEmpty())
}

/** A sentence for why deleting the account failed. Nothing has been erased if the proof failed. */
fun deleteAccountFailureMessage(error: Throwable): String = when (error) {
    is FirebaseNetworkException -> "No internet connection. Your account is untouched; try again when you're online."
    is FirebaseAuthInvalidCredentialsException -> "That password isn't right. Your account is untouched."
    is FirebaseAuthInvalidUserException -> "This account no longer exists. Sign out and back in."
    is FirebaseAuthRecentLoginRequiredException -> "For safety, sign out and sign in again, then retry."
    else -> error.localizedMessage ?: "Couldn't delete the account. Try again."
}

/** Confirms the permanent deletion of the account and everything stored for it (FR-AUTH-9). */
@Composable
internal fun DeleteAccountDialog(
    state: AccountSecurityState,
    onPasswordChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!state.isDeleting) onDismiss() },
        title = { Text("Delete your account?", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
        text = {
            Column {
                Text(
                    "This permanently erases your income, expenses, accounts, goals and settings from PulseFinance and from this phone, " +
                        "and deletes your sign-in${state.email?.let { " ($it)" }.orEmpty()}. It can't be undone.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                if (state.usesPassword) {
                    OutlinedTextField(
                        value = state.passwordInput,
                        onValueChange = onPasswordChange,
                        label = { Text("Your password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        enabled = !state.isDeleting,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text("You'll be asked to pick your Google account again to confirm.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                state.errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = state.canConfirmDelete) {
                Text(if (state.isDeleting) "Deleting…" else "Delete everything", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !state.isDeleting) { Text("Keep my account") }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp)
    )
}
