// File: app/src/main/java/com/example/antamvieclam/ui/main/MainScreen.kt

package com.example.antamvieclam.ui.main

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.antamvieclam.data.model.UserType
import com.example.antamvieclam.ui.home.HomeUiState
import com.example.antamvieclam.ui.home.HomeViewModel
import com.example.antamvieclam.ui.navigation.BottomNavGraph
import com.example.antamvieclam.ui.navigation.BottomNavItem
import com.example.antamvieclam.ui.navigation.Routes

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    rootNavController: NavHostController,
    onSignOut: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        // GỌN HƠN RẤT NHIỀU: Chỉ cần một lời gọi hàm
        topBar = {
            MainTopAppBar(
                currentRoute = currentRoute,
                homeUiState = homeUiState
            )
        },
        bottomBar = { BottomBar(navController = bottomNavController) },
        floatingActionButton = {
            // Logic FAB giữ nguyên, đã rất gọn
            if (currentRoute == BottomNavItem.Home.route) {
                if ((homeUiState as? HomeUiState.Success)?.user?.userType == UserType.EMPLOYER) {
                    FloatingActionButton(
                        onClick = { rootNavController.navigate(Routes.CREATE_JOB_SCREEN) }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Đăng tin mới")
                    }
                }
            }
        }
    ) { innerPadding ->
        BottomNavGraph(
            bottomNavController = bottomNavController,
            onSignOut = onSignOut,
            rootNavController = rootNavController,
            paddingValues = innerPadding
        )
    }
}


@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(BottomNavItem.Home, BottomNavItem.Management, BottomNavItem.Profile)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        screens.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopAppBar(
    currentRoute: String?,
    homeUiState: HomeUiState
) {
    // Logic xác định tiêu đề được đóng gói gọn gàng ở đây
    val title = when (currentRoute) {
        BottomNavItem.Home.route -> {
            (homeUiState as? HomeUiState.Success)?.user?.let { user ->
                if (user.userType == UserType.EMPLOYER) "Quản Lý Tin Tuyển Dụng" else "Tìm Việc Quanh Đây"
            }
        }
        BottomNavItem.Management.route -> "Quản Lý"
        BottomNavItem.Profile.route -> "Hồ Sơ Của Tôi"
        else -> null // Không có tiêu đề cho các màn hình khác
    }

    // Chỉ hiển thị TopAppBar khi có tiêu đề
    if (title != null) {
        TopAppBar(title = { Text(title) })
    }
}