// File: app/src/main/java/com/example/antamvieclam/ui/home/EmployerHomeScreen.kt
package com.example.antamvieclam.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// BỎ @OptIn và Scaffold
@Composable
fun EmployerHomeScreen(
    // BỎ tham số paddingValues
    onNavigateToJobDetails: (String) -> Unit,
    jobViewModel: JobViewModel = hiltViewModel()
) {
    val jobState by jobViewModel.uiState.collectAsStateWithLifecycle()

    // Không còn Box bao ngoài. `when` là cấp cao nhất.
    when (val state = jobState) {
        is JobListUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is JobListUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.message)
            }
        }
        is JobListUiState.Success -> {
            if (state.jobs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Bạn chưa đăng tin tuyển dụng nào.\nNhấn nút '+' để bắt đầu.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    // contentPadding giờ chỉ dùng cho mục đích thẩm mỹ
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.jobs) { job ->
                        JobItemCard(job = job, onJobClick = onNavigateToJobDetails)
                    }
                }
            }
        }
    }
}