package com.example.antamvieclam.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.antamvieclam.data.repository.AuthRepository
import com.example.antamvieclam.data.repository.AuthResult
import com.google.firebase.auth.AuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.antamvieclam.data.repository.UserRepository

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class LoginSuccess(val profileExists: Boolean) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun signInWithGoogle(credential: AuthCredential) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            when (val result = authRepository.signInWithGoogle(credential)) {
                is AuthResult.Success -> {
                    // Đăng nhập thành công, giờ kiểm tra profile
                    val uid = authRepository.getCurrentUserId()
                    if (uid != null) {
                        // Dùng userRepository để kiểm tra
                        val profileExists = userRepository.doesProfileExist(uid)
                        _uiState.value = AuthUiState.LoginSuccess(profileExists)
                    } else {
                        _uiState.value = AuthUiState.Error("Không lấy được thông tin người dùng.")
                    }
                }
                is AuthResult.Error -> {
                    _uiState.value = AuthUiState.Error(
                        result.exception.localizedMessage ?: "Đăng nhập thất bại."
                    )
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    fun signOut() {
        authRepository.signOut()
    }
}