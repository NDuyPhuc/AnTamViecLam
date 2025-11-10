// app/src/main/java/com/example/antamvieclam/ui/home/WorkerHomeScreen.kt
package com.example.antamvieclam.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.antamvieclam.data.model.Job
import com.example.antamvieclam.ui.auth.AuthViewModel
import com.example.antamvieclam.ui.home.components.JobItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerHomeScreen(
    navigateToJobDetails: (String) -> Unit,
    onSignOut: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    jobViewModel: JobViewModel = hiltViewModel()
) {
    val jobState by jobViewModel.uiState.collectAsStateWithLifecycle()

    // Dữ liệu giả để hiển thị UI
    val jobs = remember {
        listOf(
            Job(id = "1", title = "Phụ hồ công trình Quận 7", payRate = 300000.0, addressString = "Quận 7, TP. HCM"),
            Job(id = "2", title = "Bốc vác kho hàng Giaohangtietkiem", payRate = 25000.0, addressString = "Quận 12, TP. HCM"),
            Job(id = "3", title = "Giao hàng bán thời gian", payRate = 28000.0, addressString = "Bình Thạnh, TP. HCM"),
        )
    }


            when (val state = jobState) {
                is JobListUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is JobListUiState.Error -> {
                    Text(text = state.message)
                }
                is JobListUiState.Success -> {
                    if (state.jobs.isEmpty()){
                        Text(text = "Chưa có công việc nào được đăng.")
                    } else {
                        LazyColumn(
                            // Modifier chỉ cần fillMaxSize
                            modifier = Modifier.fillMaxSize(),
                            // Áp dụng padding từ Scaffold và thêm padding ngang 16.dp
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.jobs) { job ->
                                JobItemCard(job = job, onJobClick = navigateToJobDetails)
                            }
                        }
                    }
                }
            }


}

