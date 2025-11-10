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
    object Idle : ProfileUiState()
    object Loading : ProfileUiState()
    object SaveSuccess : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun saveUserProfile(fullName: String, userType: UserType, imageUri: Uri?) {
        _uiState.value = ProfileUiState.Loading

        if (fullName.isBlank()) {
            _uiState.value = ProfileUiState.Error("Vui lòng nhập họ và tên.")
            return
        }

        val currentUserId = authRepository.getCurrentUserId()
        if (currentUserId == null) {
            _uiState.value = ProfileUiState.Error("Người dùng chưa đăng nhập.")
            return
        }

        viewModelScope.launch {
            val user = User(
                uid = currentUserId,
                phoneNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber,
                fullName = fullName,
                userType = userType
            )

            val result = userRepository.createUserProfile(user, imageUri)
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
}