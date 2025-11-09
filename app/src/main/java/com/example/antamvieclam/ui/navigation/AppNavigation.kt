package com.example.antamvieclam.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.antamvieclam.ui.auth.LoginScreen
import com.example.antamvieclam.ui.profile.CreateProfileScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen() {
    androidx.compose.material3.Text("Welcome to Home Screen!")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // LẤY USER ĐĂNG NHẬP HIỆN TẠI
    val startDestination = if (FirebaseAuth.getInstance().currentUser != null) {
        // Nếu đã đăng nhập, có thể nhảy thẳng vào Home hoặc màn hình chờ
        // Tạm thời, chúng ta vẫn bắt đầu từ Login để test luồng
        Routes.LOGIN_SCREEN
    } else {
        Routes.LOGIN_SCREEN
    }
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN_SCREEN) {
            LoginScreen(
                navigateToHome = {
                    navController.navigate(Routes.HOME_SCREEN) {
                        popUpTo(Routes.LOGIN_SCREEN) { inclusive = true }
                    }
                },
                navigateToCreateProfile = {
                    navController.navigate(Routes.CREATE_PROFILE_SCREEN) {
                        popUpTo(Routes.LOGIN_SCREEN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.CREATE_PROFILE_SCREEN) {
            CreateProfileScreen(
                onProfileCreated = {
                    navController.navigate(Routes.HOME_SCREEN) {
                        popUpTo(Routes.CREATE_PROFILE_SCREEN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME_SCREEN) {
            HomeScreen()
        }
    }
}