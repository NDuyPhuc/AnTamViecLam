// app/src/main/java/com/example/antamvieclam/data/model/Job.kt
package com.example.antamvieclam.data.model

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

enum class PayType {
    PER_HOUR, PER_DAY, PER_PACKAGE
}

enum class JobStatus {
    OPEN, IN_PROGRESS, COMPLETED, CANCELLED
}

data class Job(
    var id: String = "",
    val employerId: String = "",
    val employerName: String = "",
    val employerProfileUrl: String? = null,

    val title: String = "",
    val description: String = "",
    val payRate: Double = 0.0,
    val payType: PayType = PayType.PER_HOUR,
    val location: GeoPoint? = null, // Vị trí chính xác trên bản đồ
    val addressString: String = "", // Địa chỉ dạng text để hiển thị

    var status: JobStatus = JobStatus.OPEN,
    var hiredWorkerId: String? = null,

    @ServerTimestamp
    val createdAt: Date? = null
)

// Dữ liệu cho đơn ứng tuyển
data class JobApplication(
    var id: String = "",
    val jobId: String = "",
    val applicantId: String = "", // workerId
    val employerId: String = "",
    val status: String = "PENDING", // PENDING, ACCEPTED, REJECTED
    @ServerTimestamp
    val appliedAt: Date? = null
)