package com.se3092.pulsefinance.ui.screens.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.se3092.pulsefinance.R

/** Thrown when the user closes the Google account picker; the UI should stay quiet. */
class GoogleSignInCancelledException : Exception("Google sign-in was cancelled")

/**
 * Obtains a Google ID token through Android Credential Manager for Firebase sign-in.
 *
 * Must be created with an Activity context because Credential Manager shows its bottom
 * sheet on top of the calling activity.
 */
class GoogleIdTokenRequester(private val activityContext: Context) {

    private val credentialManager = CredentialManager.create(activityContext)

    suspend fun requestIdToken(): Result<String> {
        val serverClientId = resolveWebClientId()
            ?: return Result.failure(
                IllegalStateException(
                    "Google Sign-In isn't configured. Add the Web OAuth client to google-services.json " +
                        "or set google_web_client_id in strings.xml."
                )
            )

        return try {
            // One-tap style picker first; falls back to the full "Sign in with Google" sheet
            // on devices that have no eligible Google account cached.
            val response = try {
                getCredential(
                    GetGoogleIdOption.Builder()
                        .setServerClientId(serverClientId)
                        .setFilterByAuthorizedAccounts(false)
                        .setAutoSelectEnabled(false)
                        .build()
                )
            } catch (e: NoCredentialException) {
                getCredential(GetSignInWithGoogleOption.Builder(serverClientId).build())
            }
            Result.success(extractIdToken(response))
        } catch (e: GetCredentialCancellationException) {
            Result.failure(GoogleSignInCancelledException())
        } catch (e: GetCredentialException) {
            Log.w(TAG, "Credential Manager failed", e)
            Result.failure(e)
        } catch (e: GoogleIdTokenParsingException) {
            Log.w(TAG, "Invalid Google ID token response", e)
            Result.failure(e)
        }
    }

    private suspend fun getCredential(option: androidx.credentials.CredentialOption): GetCredentialResponse =
        credentialManager.getCredential(
            context = activityContext,
            request = GetCredentialRequest.Builder().addCredentialOption(option).build()
        )

    private fun extractIdToken(response: GetCredentialResponse): String {
        val credential = response.credential
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            return GoogleIdTokenCredential.createFrom(credential.data).idToken
        }
        throw IllegalStateException("Unexpected credential type: ${credential.type}")
    }

    /**
     * Prefers the `default_web_client_id` that the google-services plugin generates from the
     * Web OAuth client in google-services.json, then the manual override in strings.xml.
     */
    private fun resolveWebClientId(): String? {
        val resources = activityContext.resources
        val generatedId = resources.getIdentifier("default_web_client_id", "string", activityContext.packageName)
        val fromPlugin = if (generatedId != 0) resources.getString(generatedId) else null
        val fromOverride = activityContext.getString(R.string.google_web_client_id)
        return listOf(fromPlugin, fromOverride).firstOrNull { !it.isNullOrBlank() }
    }

    private companion object {
        const val TAG = "GoogleIdTokenRequester"
    }
}
