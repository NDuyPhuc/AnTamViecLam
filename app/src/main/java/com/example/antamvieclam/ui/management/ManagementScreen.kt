package com.example.antamvieclam.ui.management

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.antamvieclam.data.model.Job
import com.example.antamvieclam.data.model.JobApplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagementScreen(
    viewModel: ManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản Lý") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is ManagementUiState.Loading -> CircularProgressIndicator()
                is ManagementUiState.Error -> Text(text = state.message)
                is ManagementUiState.Success -> {
                    when (val data = state.data) {
                        is ManagementData.WorkerData -> WorkerManagementContent(data.applications)
                        is ManagementData.EmployerData -> EmployerManagementContent(data.postedJobs)
                    }
                }
            }
        }
    }
}

@Composable
fun WorkerManagementContent(applications: List<JobApplication>) {
    if (applications.isEmpty()) {
        Text("Bạn chưa ứng tuyển công việc nào.")
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(applications) { application ->
                ApplicationItemCard(application = application)
            }
        }
    }
}

@Composable
fun EmployerManagementContent(jobs: List<Job>) {
    if (jobs.isEmpty()) {
        Text("Bạn chưa đăng tin tuyển dụng nào.")
    } else {
        // Có thể dùng lại JobItemCard từ màn hình Home
        // Hoặc tạo một card mới với các nút quản lý (xem ứng viên, sửa, xóa)
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(jobs) { job ->
                // TODO: Tạo JobManagementCard hoặc dùng JobItemCard
                Text(text = "Công việc đã đăng: ${job.title}")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationItemCard(application: JobApplication) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { /* TODO: Navigate to job details or chat */ }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // TODO: Lấy tên công việc từ jobId để hiển thị, tạm thời dùng jobId
                Text(
                    text = "ID công việc: ${application.jobId}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "ID nhà tuyển dụng: ${application.employerId}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            StatusBadge(status = application.status)
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (backgroundColor, textColor) = when (status.uppercase()) {
        "PENDING" -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        "ACCEPTED" -> Color(0xFFDFFFE0) to Color(0xFF228B22) // Xanh lá
        "REJECTED" -> Color(0xFFFFE1E1) to Color(0xFFD32F2F) // Đỏ
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = status,
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}