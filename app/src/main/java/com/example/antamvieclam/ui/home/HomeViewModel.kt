// app/src/main/java/com/example/antamvieclam/ui/home/HomeViewModel.kt
package com.example.antamvieclam.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.antamvieclam.data.model.User
import com.example.antamvieclam.data.repository.AuthRepository
import com.example.antamvieclam.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Lớp trạng thái cho màn hình Home
sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val user: User) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        // Tải thông tin người dùng ngay khi ViewModel được tạo
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            val userId = authRepository.getCurrentUserId()

            if (userId == null) {
                _uiState.value = HomeUiState.Error("Không thể xác thực người dùng.")
                return@launch
            }

            val user = userRepository.getUserProfile(userId)
            if (user != null) {
                _uiState.value = HomeUiState.Success(user)
            } else {
                // Trường hợp hiếm gặp: đã đăng nhập nhưng không có profile
                _uiState.value = HomeUiState.Error("Không tìm thấy hồ sơ người dùng.")
            }
        }
    }
}