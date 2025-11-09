package com.example.antamvieclam.ui.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.antamvieclam.data.model.UserType

@Composable
fun CreateProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onProfileCreated: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var fullName by remember { mutableStateOf("") }
    var selectedUserType by remember { mutableStateOf(UserType.WORKER) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is ProfileUiState.SaveSuccess -> {
                Toast.makeText(context, "Tạo hồ sơ thành công!", Toast.LENGTH_SHORT).show()
                onProfileCreated()
            }
            is ProfileUiState.Error -> {
                Toast.makeText(context, (uiState as ProfileUiState.Error).message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text("Tạo Hồ Sơ", style = MaterialTheme.typography.headlineMedium)

        Image(
            painter = rememberAsyncImagePainter(
                model = imageUri ?: "https://via.placeholder.com/150" // Placeholder
            ),
            contentDescription = "Ảnh đại diện",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .clickable { imagePickerLauncher.launch("image/*") },
            contentScale = ContentScale.Crop
        )

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Họ và Tên") },
            modifier = Modifier.fillMaxWidth()
        )

        // Lựa chọn vai trò
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedUserType == UserType.WORKER,
                onClick = { selectedUserType = UserType.WORKER }
            )
            Text("Người lao động", Modifier.clickable { selectedUserType = UserType.WORKER })
            Spacer(Modifier.width(16.dp))
            RadioButton(
                selected = selectedUserType == UserType.EMPLOYER,
                onClick = { selectedUserType = UserType.EMPLOYER }
            )
            Text("Nhà tuyển dụng", Modifier.clickable { selectedUserType = UserType.EMPLOYER })
        }

        Button(
            onClick = { viewModel.saveUserProfile(fullName, selectedUserType, imageUri) },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState != ProfileUiState.Loading
        ) {
            if (uiState == ProfileUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Lưu Hồ Sơ")
            }
        }
    }
}