// File: app/src/main/java/com/example/antamvieclam/ui/auth/HomeScreen.kt

package com.example.antamvieclam.ui.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.antamvieclam.data.model.UserType
import com.example.antamvieclam.ui.home.EmployerHomeScreen
import com.example.antamvieclam.ui.home.HomeUiState
import com.example.antamvieclam.ui.home.HomeViewModel
import com.example.antamvieclam.ui.home.WorkerHomeScreen
import com.example.antamvieclam.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSignOut: () -> Unit,
    rootNavController: NavHostController,
    // Tham số này được truyền từ MainScreen
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize().padding(), // Áp dụng padding ở đây
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is HomeUiState.Loading -> CircularProgressIndicator()
            is HomeUiState.Error -> Text(text = state.message)
            is HomeUiState.Success -> {
                when (state.user.userType) {
                    UserType.WORKER -> WorkerHomeScreen(
                        // THAY ĐỔI Ở ĐÂY: Sử dụng `paddingValues` thay cho `innerPadding`
                        navigateToJobDetails = { jobId ->
                            rootNavController.navigate("${Routes.JOB_DETAILS_SCREEN}/$jobId")
                        },
                        onSignOut = onSignOut
                    )
                    UserType.EMPLOYER -> EmployerHomeScreen(
                        // THAY ĐỔI Ở ĐÂY: Sử dụng `paddingValues` thay cho `innerPadding`
                        onNavigateToJobDetails = { jobId ->
                            rootNavController.navigate("${Routes.JOB_DETAILS_SCREEN}/$jobId")
                        }
                    )
                }
            }
        }
    }
}