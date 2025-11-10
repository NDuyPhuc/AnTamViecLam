// app/src/main/java/com/example/antamvieclam/ui/home/EmployerHomeScreen.kt
package com.example.antamvieclam.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout // <-- THÊM IMPORT
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.antamvieclam.ui.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployerHomeScreen(
    navigateToCreateJob: () -> Unit,
    onSignOut: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    jobViewModel: JobViewModel = hiltViewModel()
) {
    val jobState by jobViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        jobViewModel.loadJobsForCurrentUser()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản Lý Tin Tuyển Dụng") },
                actions = {
                    IconButton(onClick = {
                        authViewModel.signOut()
                        onSignOut()
                    }) {
                        Icon(imageVector = Icons.Default.Logout, contentDescription = "Đăng xuất")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = navigateToCreateJob) {
                Icon(Icons.Default.Add, contentDescription = "Đăng tin mới")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = jobState) {
                is JobListUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is JobListUiState.Error -> {
                    Text(text = state.message)
                }
                is JobListUiState.Success -> {
                    if (state.jobs.isEmpty()) {
                        Text(
                            text = "Bạn chưa đăng tin tuyển dụng nào.\nNhấn nút '+' để bắt đầu.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    } else {
                        // HIỂN THỊ DANH SÁCH CÔNG VIỆC ĐÃ ĐĂNG
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.jobs) { job ->
                                // Có thể tạo một JobItemCard khác cho NTD với các nút quản lý
                                // Tạm thời dùng lại JobItemCard của NLĐ
                                JobItemCard(job = job, onJobClick = { /* TODO: Mở màn hình quản lý chi tiết */ })
                            }
                        }
                    }
                }
            }
        }
    }

}