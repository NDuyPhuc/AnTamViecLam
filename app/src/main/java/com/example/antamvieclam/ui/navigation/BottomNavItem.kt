package com.example.antamvieclam.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

// Sealed class định nghĩa các màn hình trong Bottom Navigation
sealed class BottomNavItem(val route: String, val icon: ImageVector, val title: String) {
    object Home : BottomNavItem("home_screen", Icons.Default.Home, "Trang chủ")
    object Management : BottomNavItem("management_screen", Icons.Default.List, "Quản lý")
    object Profile : BottomNavItem("profile_screen", Icons.Default.Person, "Hồ sơ")
}