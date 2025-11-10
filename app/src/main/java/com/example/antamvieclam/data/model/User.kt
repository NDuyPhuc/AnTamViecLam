package com.example.antamvieclam.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

// Phân loại người dùng
enum class UserType {
    WORKER, EMPLOYER
}

data class User(
    val uid: String = "",
    val phoneNumber: String? = "",
    val userType: UserType = UserType.WORKER,
    val fullName: String = "",
    var profileImageUrl: String? = null, // URL ảnh từ Cloudinary
    val address: String? = null,
    // Thêm các trường khác theo cấu trúc của bạn
    @ServerTimestamp
    val createdAt: Date? = null
)