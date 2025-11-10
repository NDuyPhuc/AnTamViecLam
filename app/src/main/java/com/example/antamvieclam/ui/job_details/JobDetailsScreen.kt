// app/src/main/java/com/example/antamvieclam/ui/job_details/JobDetailsScreen.kt
package com.example.antamvieclam.ui.job_details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.antamvieclam.data.model.Job
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailsScreen(
    jobId: String?, // Vẫn giữ để biết route, nhưng ViewModel sẽ tự xử lý
    onNavigateBack: () -> Unit,
    viewModel: JobDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
            // Chỉ hiển thị nút khi đã tải thành công
            if (uiState is JobDetailsUiState.Success) {
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
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is JobDetailsUiState.Loading -> CircularProgressIndicator()
                is JobDetailsUiState.Error -> Text(text = state.message)
                is JobDetailsUiState.Success -> JobDetailsContent(job = state.job)
            }
        }
    }
}

@Composable
fun JobDetailsContent(job: Job) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = job.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Divider()
        JobDetailRow(title = "Nhà tuyển dụng", content = job.employerName)
        JobDetailRow(
            title = "Mức lương",
            content = "${job.payRate.toLong().formatCurrency()} VNĐ / ${job.payType.toVietnamese()}"
        )
        JobDetailRow(title = "Địa chỉ", content = job.addressString)
        Text("Mô tả công việc", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(
            text = job.description,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

// Giữ nguyên Composable này
@Composable
fun JobDetailRow(title: String, content: String) {
    Column {
        Text(text = title, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        Text(text = content, style = MaterialTheme.typography.bodyLarge)
    }
}

// Hàm tiện ích để format lương và loại hình trả lương
fun Long.formatCurrency(): String {
    return NumberFormat.getNumberInstance(Locale.GERMANY).format(this)
}

fun com.example.antamvieclam.data.model.PayType.toVietnamese(): String {
    return when (this) {
        com.example.antamvieclam.data.model.PayType.PER_HOUR -> "giờ"
        com.example.antamvieclam.data.model.PayType.PER_DAY -> "ngày"
        com.example.antamvieclam.data.model.PayType.PER_PACKAGE -> "gói"
    }
}