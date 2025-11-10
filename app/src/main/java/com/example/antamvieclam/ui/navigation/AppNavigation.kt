// app/src/main/java/com/example/antamvieclam/ui/navigation/AppNavigation.kt

package com.example.antamvieclam.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.antamvieclam.ui.auth.HomeScreen
import com.example.antamvieclam.ui.auth.LoginScreen
import com.example.antamvieclam.ui.job_details.JobDetailsScreen
import com.example.antamvieclam.ui.main.MainScreen
import com.example.antamvieclam.ui.management.ManagementScreen
import com.example.antamvieclam.ui.profile.CreateProfileScreen
import com.example.antamvieclam.ui.profile.ProfileScreen
import com.google.firebase.auth.FirebaseAuth


// Cấu trúc NavHost chính của toàn bộ ứng dụng
@Composable
fun RootNavigation() {

    val navController = rememberNavController()

    val startDestination = if (FirebaseAuth.getInstance().currentUser != null) {
        "main_screen"
    } else {
        Routes.LOGIN_SCREEN
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LOGIN_SCREEN) {
            LoginScreen(
                navigateToHome = {
                    navController.navigate("main_screen") {
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
                    navController.navigate("main_screen") {
                        popUpTo(Routes.CREATE_PROFILE_SCREEN) { inclusive = true }
                    }
                }
            )
        }
        composable("main_screen") {
            MainScreen(
                rootNavController = navController,
                // THÊM MỚI: Định nghĩa hành động đăng xuất tại đây
                onSignOut = {
                    navController.navigate(Routes.LOGIN_SCREEN) {
                        popUpTo(0) // Xóa toàn bộ back stack
                    }
                }
            )
        }
        // THÊM MỚI: Định nghĩa route cho màn hình chi tiết công việc
        composable(
            route = "${Routes.JOB_DETAILS_SCREEN}/{jobId}",
            arguments = listOf(navArgument("jobId") { type = NavType.StringType })
        ) { backStackEntry ->
            JobDetailsScreen(
                jobId = backStackEntry.arguments?.getString("jobId"),
                // Hành động để quay lại màn hình trước đó
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

// Cấu trúc NavHost cho các màn hình BÊN TRONG Bottom Navigation Bar
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BottomNavGraph(

    bottomNavController: NavHostController,
    onSignOut: () -> Unit,
    rootNavController: NavHostController
) {
    NavHost(bottomNavController, startDestination = BottomNavItem.Home.route) {
        composable(BottomNavItem.Home.route) {
            HomeScreen(
                onSignOut = onSignOut,
                navigateToCreateJob = {
                    rootNavController.navigate(Routes.CREATE_JOB_SCREEN)
                },
                navigateToJobDetails = { jobId ->
                    rootNavController.navigate("${Routes.JOB_DETAILS_SCREEN}/$jobId")
                }
                // XÓA các tham số thừa, bao gồm cả lỗi 'navigateToLogin' ở đây
            )
        }
        composable(BottomNavItem.Management.route) {
            ManagementScreen()
        }
        composable(BottomNavItem.Profile.route) {
            ProfileScreen(onSignOut = onSignOut)
        }
    }
}