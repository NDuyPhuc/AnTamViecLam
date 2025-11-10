package com.example.antamvieclam.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

// Sealed class định nghĩa các màn hình trong Bottom Navigation
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector, // Icon khi chưa được chọn
    val selectedIcon: ImageVector // Icon khi đã được chọn
) {
    object Home : BottomNavItem(
        route = "home_screen",
        title = "Trang chủ",
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home
    )

    object Management : BottomNavItem(
        route = "management_screen",
        title = "Quản lý",
        icon = Icons.Outlined.List,
        selectedIcon = Icons.Filled.List
    )

    object Profile : BottomNavItem(
        route = "profile_screen",
        title = "Hồ sơ",
        icon = Icons.Outlined.Person,
        selectedIcon = Icons.Filled.Person
    )
}