// app/src/main/java/com/example/antamvieclam/ui/navigation/AppNavigation.kt

package com.example.antamvieclam.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.antamvieclam.ui.auth.HomeScreen
import com.example.antamvieclam.ui.auth.LoginScreen
import com.example.antamvieclam.ui.home.JobViewModel
import com.example.antamvieclam.ui.job_details.JobDetailsScreen
import com.example.antamvieclam.ui.main.MainScreen
import com.example.antamvieclam.ui.management.ManagementScreen
import com.example.antamvieclam.ui.posting.CreateJobScreen
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
        composable(Routes.CREATE_JOB_SCREEN) {
            CreateJobScreen(
                onNavigateBack = { navController.popBackStack() },
                onJobPostedSuccessfully = {
                    // Khi đăng bài thành công, gửi tín hiệu về màn hình trước đó
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("new_job_created", true)
                    navController.popBackStack()
                }
            )
        }
    }
}

// Cấu trúc NavHost cho các màn hình BÊN TRONG Bottom Navigation Bar
@Composable
fun BottomNavGraph(
    bottomNavController: NavHostController,
    onSignOut: () -> Unit,
    rootNavController: NavHostController,
    paddingValues: PaddingValues, // <-- Tham số này rất quan trọng
    homeScreenReloadTrigger: Int
) {
    NavHost(
        navController = bottomNavController,
        startDestination = BottomNavItem.Home.route,
        // ÁP DỤNG PADDING VÀO ĐÂY!
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(BottomNavItem.Home.route) {
//            // Lấy viewModel tại đây để có thể gọi hàm của nó
//            val jobViewModel: JobViewModel = hiltViewModel()
//
//            // Lắng nghe kết quả từ màn hình tạo job
//            val newJobCreated = it.savedStateHandle.get<Boolean>("new_job_created")
//            if (newJobCreated == true) {
//                // Nếu có tín hiệu, gọi lại hàm tải dữ liệu và xóa tín hiệu đi
//                jobViewModel.loadJobsForCurrentUser()
//                it.savedStateHandle.remove<Boolean>("new_job_created")
//            }

            HomeScreen(
                onSignOut = onSignOut,
                rootNavController = rootNavController,
                // Truyền viewModel đã có sẵn vào
//                jobViewModel = jobViewModel,
                        reloadTrigger = homeScreenReloadTrigger
            )
        }
        composable(BottomNavItem.Management.route) {
            // KHÔNG cần truyền paddingValues vào ManagementScreen nữa
            ManagementScreen(PaddingValues())
        }
        composable(BottomNavItem.Profile.route) {
            // KHÔNG cần truyền paddingValues vào ProfileScreen nữa
            ProfileScreen(onSignOut = onSignOut)
        }
    }
}