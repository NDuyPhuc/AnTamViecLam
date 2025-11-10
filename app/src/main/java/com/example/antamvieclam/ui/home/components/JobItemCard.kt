package com.example.antamvieclam.ui.home.components

// Dán tất cả các import cần thiết vào đây
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.antamvieclam.data.model.Job
import com.example.antamvieclam.data.model.PayType
import java.text.NumberFormat
import java.util.Locale

@Composable
fun JobItemCard(
    job: Job,
    onJobClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onJobClick(job.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = job.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Địa điểm",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = job.addressString,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                // THAY ĐỔI CỐT LÕI NẰM Ở ĐÂY
                text = "${job.payRate.toLong().formatCurrency()} VNĐ / ${job.payType.toVietnamese()}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// Các hàm tiện ích
fun Long.formatCurrency(): String {
    val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    return formatter.format(this)
}

fun PayType.toVietnamese(): String {
    return when (this) {
        PayType.PER_HOUR -> "giờ"
        PayType.PER_DAY -> "ngày"
        PayType.PER_PACKAGE -> "tháng"
    }
}