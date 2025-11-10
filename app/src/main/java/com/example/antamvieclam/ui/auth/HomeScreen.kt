// app/src/main/java/com/example/antamvieclam/ui/auth/HomeScreen.kt
package com.example.antamvieclam.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.antamvieclam.data.model.UserType
import com.example.antamvieclam.ui.home.EmployerHomeScreen
import com.example.antamvieclam.ui.home.HomeUiState
import com.example.antamvieclam.ui.home.HomeViewModel
import com.example.antamvieclam.ui.home.WorkerHomeScreen

@Composable
fun HomeScreen(
    // Các callback điều hướng cần thiết cho các màn hình con
    navigateToLogin: () -> Unit,
    navigateToCreateJob: () -> Unit,
    navigateToJobDetails: (String) -> Unit,
    // ViewModel để lấy thông tin user
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is HomeUiState.Error -> {
                    Text(text = state.message)
                }
                is HomeUiState.Success -> {
                    // Dựa vào userType để quyết định hiển thị màn hình nào
                    when (state.user.userType) {
                        UserType.EMPLOYER -> EmployerHomeScreen(
                            navigateToCreateJob = navigateToCreateJob,
                            navigateToLogin = navigateToLogin
                        )
                        UserType.WORKER -> WorkerHomeScreen(
                            navigateToJobDetails = navigateToJobDetails,
                            navigateToLogin = navigateToLogin
                        )
                    }
                }
            }
        }
    }
}