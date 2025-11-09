package com.example.antamvieclam.ui.auth

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    navigateToHome: () -> Unit,
    navigateToCreateProfile: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Khởi tạo Google Auth Client
    val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = context,
            oneTapClient = Identity.getSignInClient(context)
        )
    }

    // Launcher để xử lý kết quả trả về từ Google Sign-In
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            val credential = googleAuthUiClient.getSignInCredentialFromIntent(result.data ?: return@rememberLauncherForActivityResult)
            if (credential != null) {
                viewModel.signInWithGoogle(credential)
            }
        }
    )

    // Lắng nghe trạng thái từ ViewModel
    LaunchedEffect(uiState) {
        when (val currentState = uiState) { // Используем currentState для удобства
            is AuthUiState.LoginSuccess -> {
                Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                if (currentState.profileExists) { // <-- Исправлено
                    navigateToHome()
                } else {
                    navigateToCreateProfile()
                }
                viewModel.resetState()
            }
            is AuthUiState.Error -> {
                Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show() // Также улучшено для консистентности
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                coroutineScope.launch {
                    val signInIntentSender = googleAuthUiClient.signIn()
                    launcher.launch(
                        IntentSenderRequest.Builder(
                            signInIntentSender ?: return@launch
                        ).build()
                    )
                }
            },
            enabled = uiState != AuthUiState.Loading
        ) {
            if (uiState == AuthUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Đăng nhập với Google")
            }
        }
    }
}