// app/src/main/java/com/example/antamvieclam/ui/home/WorkerHomeScreen.kt
package com.example.antamvieclam.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.antamvieclam.data.model.Job
import com.example.antamvieclam.ui.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerHomeScreen(
    navigateToJobDetails: (String) -> Unit,
    navigateToLogin: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    // Dữ liệu giả để hiển thị UI
    val jobs = remember {
        listOf(
            Job(id = "1", title = "Phụ hồ công trình Quận 7", payRate = 300000.0, addressString = "Quận 7, TP. HCM"),
            Job(id = "2", title = "Bốc vác kho hàng Giaohangtietkiem", payRate = 25000.0, addressString = "Quận 12, TP. HCM"),
            Job(id = "3", title = "Giao hàng bán thời gian", payRate = 28000.0, addressString = "Bình Thạnh, TP. HCM"),
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tìm Việc Quanh Đây") },
                actions = {
                    IconButton(onClick = {
                        authViewModel.signOut()
                        navigateToLogin()
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Đăng xuất")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(jobs.size) { index ->
                JobItemCard(job = jobs[index], onJobClick = navigateToJobDetails)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobItemCard(
    job: Job,
    onJobClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onJobClick(job.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // Sử dụng màu nền của theme
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = job.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = job.addressString,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${job.payRate.toLong()} VNĐ / giờ", // Cần format payType sau
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}