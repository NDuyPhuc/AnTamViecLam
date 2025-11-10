package com.example.antamvieclam.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.antamvieclam.data.model.User
import com.example.antamvieclam.data.model.UserType
import com.example.antamvieclam.data.repository.AuthRepository
import com.example.antamvieclam.data.repository.ProfileResult
import com.example.antamvieclam.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProfileUiState {

    object Loading : ProfileUiState()
    data class LoadSuccess(val user: User) : ProfileUiState()
    object SaveSuccess : ProfileUiState()

    data class Success(val user: User) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            val userId = authRepository.getCurrentUserId()
            if (userId == null) {
                _uiState.value = ProfileUiState.Error("Không thể xác thực người dùng.")
                return@launch
            }

            val user = userRepository.getUserProfile(userId)
            if (user != null) {
                _uiState.value = ProfileUiState.Success(user)
            } else {
                _uiState.value = ProfileUiState.Error("Không tìm thấy hồ sơ người dùng.")
            }
        }
    }

    fun saveUserProfile(fullName: String, userType: UserType, imageUri: Uri?) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            if (fullName.isBlank()) {
                _uiState.value = ProfileUiState.Error("Vui lòng nhập họ và tên.")
                return@launch
            }
            val currentUserId = authRepository.getCurrentUserId()
            if (currentUserId == null) {
                _uiState.value = ProfileUiState.Error("Người dùng chưa đăng nhập.")
                return@launch
            }

            // Tạo đối tượng User mới
            val newUser = User(
                uid = currentUserId,
                fullName = fullName,
                userType = userType,
                phoneNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber
            )

            // Gọi repository để tạo profile
            val result = userRepository.createUserProfile(newUser, imageUri)
            when (result) {
                is ProfileResult.Success -> {
                    _uiState.value = ProfileUiState.SaveSuccess
                }
                is ProfileResult.Error -> {
                    _uiState.value = ProfileUiState.Error(result.message)
                }
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
    }
}