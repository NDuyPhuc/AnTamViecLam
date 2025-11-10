// app/src/main/java/com/example/antamvieclam/ui/job_details/JobDetailsScreen.kt
package com.example.antamvieclam.ui.job_details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailsScreen(
    jobId: String?,
    onNavigateBack: () -> Unit
) {
    // TODO: Kết nối ViewModel để tải chi tiết công việc bằng jobId

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi Tiết Công Việc") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { /* TODO: Gọi viewModel.applyForJob() */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp)
            ) {
                Text("ỨNG TUYỂN NGAY", fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Đây là dữ liệu giả, sẽ được thay thế bằng dữ liệu thật từ ViewModel
            Text(
                text = "Phụ hồ công trình Q7",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Divider()
            JobDetailRow(title = "Nhà tuyển dụng", content = "Anh Bảy")
            JobDetailRow(title = "Mức lương", content = "300,000 VNĐ / ngày")
            JobDetailRow(title = "Địa chỉ", content = "123 Nguyễn Thị Thập, P. Tân Phong, Quận 7, TP.HCM")
            Text("Mô tả công việc", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(
                text = "Cần người phụ hồ, biết trộn vữa, siêng năng, chịu khó. Làm việc từ 8h sáng đến 5h chiều, có nghỉ trưa.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun JobDetailRow(title: String, content: String) {
    Column {
        Text(text = title, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        Text(text = content, style = MaterialTheme.typography.bodyLarge)
    }
}