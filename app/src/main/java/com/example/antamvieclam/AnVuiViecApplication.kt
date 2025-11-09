package com.example.antamvieclam

import android.app.Application
import com.cloudinary.android.MediaManager
import dagger.hilt.android.HiltAndroidApp
import com.example.antamvieclam.BuildConfig

@HiltAndroidApp
class AnVuiViecApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Tạo một Map để chứa thông tin cấu hình
        val config = mutableMapOf<String, String>()
        config["cloud_name"] = BuildConfig.CLOUDINARY_CLOUD_NAME
        config["api_key"] = BuildConfig.CLOUDINARY_API_KEY
        config["api_secret"] = BuildConfig.CLOUDINARY_API_SECRET

        // Gọi hàm init với Context và Map, đây là phiên bản hợp lệ
        MediaManager.init(this, config)
    }
}