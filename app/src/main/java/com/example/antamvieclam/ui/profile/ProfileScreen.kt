package com.example.antamvieclam.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.antamvieclam.data.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onSignOut: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Bỏ Scaffold, chỉ giữ lại Box chứa nội dung
    Box(
        modifier = Modifier.fillMaxSize().padding(),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is ProfileUiState.Loading -> CircularProgressIndicator()
            is ProfileUiState.Error -> Text(text = state.message)
            // Sửa lại tên State cho khớp với code của bạn
            is ProfileUiState.Success -> ProfileContent(
                user = state.user,
                onSignOut = {
                    viewModel.signOut()
                    onSignOut()
                }
            )
            is ProfileUiState.LoadSuccess -> ProfileContent(
                user = state.user,
                onSignOut = {
                    viewModel.signOut()
                    onSignOut()
                }
            )
            else -> {} // Bỏ qua các state khác
        }
    }
}

@Composable
fun ProfileContent(user: User, onSignOut: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(16.dp)
    ) {
        // Avatar và Tên
        item {
            Image(
                painter = rememberAsyncImagePainter(
                    model = user.profileImageUrl ?: "https://via.placeholder.com/150"
                ),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = user.fullName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = user.userType.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Thông tin chi tiết
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    InfoRow(
                        icon = Icons.Default.Person,
                        title = "Mã người dùng",
                        subtitle = user.uid
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    InfoRow(
                        icon = Icons.Default.Email,
                        title = "Số điện thoại",
                        subtitle = user.phoneNumber ?: "Chưa cập nhật"
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Các hành động
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    ListItem(
                        headlineContent = { Text("Chỉnh sửa hồ sơ") },
                        leadingContent = { Icon(Icons.Default.Edit, contentDescription = null) },
                        modifier = Modifier.clickable { /* TODO: Navigate to Edit Profile */ }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    ListItem(
                        headlineContent = { Text("Đăng xuất") },
                        leadingContent = { Icon(Icons.Default.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                        modifier = Modifier.clickable(onClick = onSignOut)
                    )
                }
            }
        }
    }
}

@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    ListItem(
        headlineContent = { Text(subtitle) },
        overlineContent = { Text(title) },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(8.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    )
}