// app/src/main/java/com/example/antamvieclam/ui/navigation/AppNavigation.kt

package com.example.antamvieclam.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.antamvieclam.ui.auth.HomeScreen
import com.example.antamvieclam.ui.auth.LoginScreen
import com.example.antamvieclam.ui.job_details.JobDetailsScreen
import com.example.antamvieclam.ui.posting.CreateJobScreen
import com.example.antamvieclam.ui.profile.CreateProfileScreen
import com.google.firebase.auth.FirebaseAuth

// Xóa hàm HomeScreen() giả ở đây nếu có

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // SỬA LẠI LOGIC: Nếu đã đăng nhập, vào thẳng HOME_SCREEN
    val startDestination = if (FirebaseAuth.getInstance().currentUser != null) {
        Routes.HOME_SCREEN
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

        // SỬA LẠI HOÀN TOÀN KHỐI NÀY
        composable(Routes.HOME_SCREEN) {
            HomeScreen(
                navigateToLogin = {
                    navController.navigate(Routes.LOGIN_SCREEN) {
                        // Xóa hết back stack
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                // Đã thay thế TODO() bằng hành động điều hướng thật
                navigateToCreateJob = {
                    navController.navigate(Routes.CREATE_JOB_SCREEN)
                },
                // Đã thay thế TODO() bằng hành động điều hướng thật, có truyền tham số
                navigateToJobDetails = { jobId ->
                    // Tạo route cụ thể cho công việc được chọn, ví dụ: "job_details/abc-123"
                    navController.navigate("${Routes.JOB_DETAILS_SCREEN}/$jobId")
                }
                // Không cần truyền viewModel vào đây vì HomeScreen sẽ tự lấy bằng hiltViewModel()
            )
        }

        // THÊM COMPOSABLE CHO MÀN HÌNH TẠO VIỆC
        composable(Routes.CREATE_JOB_SCREEN) {
            CreateJobScreen(
                onJobPosted = {
                    // Sau khi đăng tin thành công, quay lại màn hình trước đó
                    navController.popBackStack()
                }
            )
        }

        // THÊM COMPOSABLE CHO MÀN HÌNH CHI TIẾT CÔNG VIỆC
        composable(
            route = "${Routes.JOB_DETAILS_SCREEN}/{jobId}", // Định nghĩa route với tham số "jobId"
            arguments = listOf(navArgument("jobId") { type = NavType.StringType })
        ) { backStackEntry ->
            // Lấy jobId từ arguments
            JobDetailsScreen(
                jobId = backStackEntry.arguments?.getString("jobId"),
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}