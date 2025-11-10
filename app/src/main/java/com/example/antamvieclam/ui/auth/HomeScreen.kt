// File: app/src/main/java/com/example/antamvieclam/ui/auth/HomeScreen.kt

package com.example.antamvieclam.ui.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.antamvieclam.data.model.UserType
import com.example.antamvieclam.ui.home.*
import com.example.antamvieclam.ui.navigation.Routes

@Composable
fun HomeScreen(
    onSignOut: () -> Unit,
    rootNavController: NavHostController,
    reloadTrigger: Int,
    // HomeScreen sẽ tạo và sở hữu cả hai ViewModel này
    homeViewModel: HomeViewModel = hiltViewModel(),
    jobViewModel: JobViewModel = hiltViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    // Effect này chỉ chạy khi reloadTrigger thay đổi, để tải lại thông tin user
    LaunchedEffect(key1 = reloadTrigger) {
        homeViewModel.loadCurrentUser()
    }

    when (val state = uiState) {
        is HomeUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is HomeUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.message)
            }
        }
        is HomeUiState.Success -> {
            // Effect này chạy khi vai trò user thay đổi HOẶC khi trigger thay đổi
            // Nó sẽ ra lệnh cho JobViewModel tải đúng loại dữ liệu
            LaunchedEffect(key1 = state.user.userType, key2 = reloadTrigger) {
                if (state.user.userType == UserType.EMPLOYER) {
                    jobViewModel.loadJobsForCurrentUser()
                } else {
                    jobViewModel.loadAllJobs()
                }
            }

            // Dựa vào vai trò user để hiển thị màn hình con tương ứng
            when (state.user.userType) {
                UserType.WORKER -> WorkerHomeScreen(
                    navigateToJobDetails = { jobId ->
                        rootNavController.navigate("${Routes.JOB_DETAILS_SCREEN}/$jobId")
                    },
                    onSignOut = onSignOut,
                    // Truyền xuống cùng một instance của jobViewModel
                    jobViewModel = jobViewModel
                )
                UserType.EMPLOYER -> EmployerHomeScreen(
                    onNavigateToJobDetails = { jobId ->
                        rootNavController.navigate("${Routes.JOB_DETAILS_SCREEN}/$jobId")
                    },
                    // Truyền xuống cùng một instance của jobViewModel
                    jobViewModel = jobViewModel
                )
            }
        }
    }
}