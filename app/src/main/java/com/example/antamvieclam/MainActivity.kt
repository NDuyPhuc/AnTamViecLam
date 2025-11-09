package com.example.antamvieclam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.antamvieclam.ui.navigation.AppNavigation // Import quan trọng
import com.example.antamvieclam.ui.theme.AnTamViecLamTheme
import dagger.hilt.android.AndroidEntryPoint // Import quan trọng

@AndroidEntryPoint // BẮT BUỘC: Đánh dấu Activity là một entry point cho Hilt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnTamViecLamTheme {
                // GỌI AppNavigation() để bắt đầu luồng của ứng dụng
                AppNavigation()
            }
        }
    }
}