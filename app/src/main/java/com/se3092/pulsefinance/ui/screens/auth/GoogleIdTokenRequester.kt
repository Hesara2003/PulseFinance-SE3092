package com.se3092.pulsefinance.ui.screens.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.se3092.pulsefinance.R

/**
 * Shows the system Google account picker and returns the account's ID token.
 * Lives in the UI layer because Credential Manager needs an Activity context to show its sheet;
 * the token is handed straight to [AuthViewModel], which owns the actual Firebase sign-in.
 */
object GoogleIdTokenRequester {

    /** Null when the user dismissed the picker — not an error worth showing. */
    suspend fun request(activityContext: Context): Result<String?> = runCatching {
        val option = GetSignInWithGoogleOption.Builder(activityContext.getString(R.string.default_web_client_id)).build()
        val request = GetCredentialRequest.Builder().addCredentialOption(option).build()
        try {
            val credential = CredentialManager.create(activityContext).getCredential(activityContext, request).credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                GoogleIdTokenCredential.createFrom(credential.data).idToken
            } else {
                throw IllegalStateException("Unexpected credential type returned by Google")
            }
        } catch (e: GetCredentialCancellationException) {
            null
        }
    }
}
