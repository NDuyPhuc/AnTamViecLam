package com.example.antamvieclam

import android.app.Application
import com.cloudinary.android.MediaManager
import com.example.antamvieclam.BuildConfig
import com.trackasia.android.TrackAsia
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AnVuiViecApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // --- Khởi tạo TrackAsia với style URL trực tiếp ---
        val styleUrl = "https://maps.track-asia.com/styles/v1/streets.json?key=52fedb6b306931761836057e5580a05be7"
        TrackAsia.getInstance(applicationContext).equals(styleUrl)

        // --- Khởi tạo Cloudinary ---
        val config = mutableMapOf<String, String>()
        config["cloud_name"] = BuildConfig.CLOUDINARY_CLOUD_NAME
        config["api_key"] = BuildConfig.CLOUDINARY_API_KEY
        config["api_secret"] = BuildConfig.CLOUDINARY_API_SECRET
        MediaManager.init(this, config)
    }
}
