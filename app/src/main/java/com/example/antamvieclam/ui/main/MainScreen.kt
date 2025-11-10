// File: app/src/main/java/com/example/antamvieclam/ui/main/MainScreen.kt

package com.example.antamvieclam.ui.main

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    // "Cò súng" để kích hoạt việc tải lại dữ liệu
    var homeScreenReloadTrigger by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            MainTopAppBar(
                currentRoute = currentRoute,
                homeUiState = homeUiState
            )
        },
        bottomBar = {
            BottomBar(
                navController = bottomNavController,
                onTabReselected = { reselectedScreen ->
                    // Khi người dùng nhấn lại vào một tab đã được chọn
                    if (reselectedScreen.route == BottomNavItem.Home.route) {
                        // Nếu đó là tab Home, tăng giá trị của trigger
                        homeScreenReloadTrigger++
                    }
                    // Bạn có thể thêm logic reload cho các màn hình khác ở đây
                }
            )
        },
        floatingActionButton = {
            // Chỉ hiển thị FAB khi ở màn hình Home và là Nhà Tuyển Dụng
            if (currentRoute == BottomNavItem.Home.route &&
                (homeUiState as? HomeUiState.Success)?.user?.userType == UserType.EMPLOYER) {
                FloatingActionButton(
                    onClick = { rootNavController.navigate(Routes.CREATE_JOB_SCREEN) }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Đăng tin mới")
                }
            }
        }
    ) { innerPadding ->
        BottomNavGraph(
            bottomNavController = bottomNavController,
            onSignOut = onSignOut,
            rootNavController = rootNavController,
            paddingValues = innerPadding,
            // Truyền trigger xuống NavGraph để nó có thể truyền tiếp vào HomeScreen
            homeScreenReloadTrigger = homeScreenReloadTrigger
        )
    }
}

@Composable
fun BottomBar(
    navController: NavHostController,
    onTabReselected: (BottomNavItem) -> Unit // Callback khi nhấn lại tab đã chọn
) {
    val screens = listOf(BottomNavItem.Home, BottomNavItem.Management, BottomNavItem.Profile)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        screens.forEach { screen ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (isSelected) {
                        // Nếu tab đã được chọn sẵn, gọi callback onTabReselected
                        onTabReselected(screen)
                    } else {
                        // Nếu là tab mới, điều hướng như bình thường
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    val icon = if (isSelected) screen.selectedIcon else screen.icon
                    Icon(icon, contentDescription = screen.title)
                },
                label = { Text(screen.title) }
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
    val title = when (currentRoute) {
        BottomNavItem.Home.route ->
            (homeUiState as? HomeUiState.Success)?.user?.let { user ->
                if (user.userType == UserType.EMPLOYER) "Quản Lý Tin Tuyển Dụng" else "Tìm Việc Quanh Đây"
            }
        BottomNavItem.Management.route -> "Quản Lý"
        BottomNavItem.Profile.route -> "Hồ Sơ Của Tôi"
        else -> null
    }

    if (title != null) {
        TopAppBar(title = { Text(title) })
    }
}