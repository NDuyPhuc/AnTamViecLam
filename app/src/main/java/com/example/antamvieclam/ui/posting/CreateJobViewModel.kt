// app/src/main/java/com/example/antamvieclam/ui/posting/CreateJobViewModel.kt
package com.example.antamvieclam.ui.posting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.antamvieclam.data.model.Job
import com.example.antamvieclam.data.model.PayType
import com.example.antamvieclam.data.repository.AuthRepository
import com.example.antamvieclam.data.repository.JobRepository
import com.example.antamvieclam.data.repository.UserRepository
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CreateJobState {
    object Idle : CreateJobState()
    object Loading : CreateJobState()
    object Success : CreateJobState()
    data class Error(val message: String) : CreateJobState()
}

@HiltViewModel
class CreateJobViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateJobState>(CreateJobState.Idle)
    val uiState = _uiState.asStateFlow()

    fun postJob(
        title: String,
        description: String,
        payRate: String,
        payType: PayType,
        address: String,
        location: GeoPoint?
    ) {
        _uiState.value = CreateJobState.Loading

        // --- Validation ---
        if (title.isBlank() || description.isBlank() || payRate.isBlank() || address.isBlank()) {
            _uiState.value = CreateJobState.Error("Vui lòng điền đầy đủ thông tin.")
            return
        }
        if (location == null) {
            _uiState.value = CreateJobState.Error("Vui lòng chọn vị trí trên bản đồ.")
            return
        }
        val payRateDouble = payRate.toDoubleOrNull()
        if (payRateDouble == null || payRateDouble <= 0) {
            _uiState.value = CreateJobState.Error("Mức lương không hợp lệ.")
            return
        }

        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId()
            if (userId == null) {
                _uiState.value = CreateJobState.Error("Không thể xác thực người dùng.")
                return@launch
            }

            // Lấy thông tin NTD để nhúng vào job object
            val employer = userRepository.getUserProfile(userId)
            if (employer == null) {
                _uiState.value = CreateJobState.Error("Không tìm thấy hồ sơ nhà tuyển dụng.")
                return@launch
            }

            val newJob = Job(
                employerId = userId,
                employerName = employer.fullName,
                employerProfileUrl = employer.profileImageUrl,
                title = title,
                description = description,
                payRate = payRateDouble,
                payType = payType,
                addressString = address,
                location = location
            )

            jobRepository.postJob(newJob)
                .onSuccess { _uiState.value = CreateJobState.Success }
                .onFailure { _uiState.value = CreateJobState.Error(it.message ?: "Đã xảy ra lỗi") }
        }
    }
}