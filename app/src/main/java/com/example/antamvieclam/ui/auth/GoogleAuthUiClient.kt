package com.example.antamvieclam.ui.auth

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.example.antamvieclam.R // <-- Thêm dòng này
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient
) {
    suspend fun signIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                BeginSignInRequest.builder()
                    .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                            .setSupported(true)
                            // ID này lấy từ file google-services.json
                            // Hoặc vào Google Cloud Console -> APIs & Services -> Credentials
                            // -> OAuth 2.0 Client IDs -> Web client (Auto-created by Google Service)
                            .setServerClientId(context.getString(R.string.default_web_client_id))
                            .setFilterByAuthorizedAccounts(false)
                            .build()
                    )
                    .setAutoSelectEnabled(true)
                    .build()
            ).await()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        return result?.pendingIntent?.intentSender
    }

    fun getSignInCredentialFromIntent(intent: Intent) =
        oneTapClient.getSignInCredentialFromIntent(intent).googleIdToken?.let {
            GoogleAuthProvider.getCredential(it, null)
        }
}
