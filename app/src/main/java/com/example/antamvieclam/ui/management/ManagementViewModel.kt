package com.example.antamvieclam.ui.management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.antamvieclam.data.model.Job
import com.example.antamvieclam.data.model.JobApplication
import com.example.antamvieclam.data.model.UserType
import com.example.antamvieclam.data.repository.AuthRepository
import com.example.antamvieclam.data.repository.JobRepository
import com.example.antamvieclam.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Lớp trạng thái phức tạp hơn để chứa dữ liệu cho cả 2 vai trò
sealed class ManagementUiState {
    object Loading : ManagementUiState()
    data class Success(val data: ManagementData) : ManagementUiState()
    data class Error(val message: String) : ManagementUiState()
}

// Dùng sealed interface để định nghĩa các loại dữ liệu có thể có
sealed interface ManagementData {
    data class WorkerData(val applications: List<JobApplication>) : ManagementData
    data class EmployerData(val postedJobs: List<Job>) : ManagementData
}

@HiltViewModel
class ManagementViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ManagementUiState>(ManagementUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadDataForCurrentUser()
    }

    fun loadDataForCurrentUser() {
        viewModelScope.launch {
            _uiState.value = ManagementUiState.Loading
            val userId = authRepository.getCurrentUserId()
            if (userId == null) {
                _uiState.value = ManagementUiState.Error("Không thể xác thực người dùng.")
                return@launch
            }
            val user = userRepository.getUserProfile(userId)
            if (user == null) {
                _uiState.value = ManagementUiState.Error("Không tìm thấy hồ sơ người dùng.")
                return@launch
            }

            when (user.userType) {
                UserType.WORKER -> loadApplicationsForWorker(userId)
                UserType.EMPLOYER -> loadJobsForEmployer(userId)
            }
        }
    }

    private suspend fun loadApplicationsForWorker(workerId: String) {
        val result = jobRepository.getApplicationsForWorker(workerId)
        if (result.isSuccess) {
            _uiState.value = ManagementUiState.Success(
                ManagementData.WorkerData(result.getOrNull() ?: emptyList())
            )
        } else {
            _uiState.value = ManagementUiState.Error(result.exceptionOrNull()?.message ?: "Lỗi")
        }
    }

    private suspend fun loadJobsForEmployer(employerId: String) {
        val result = jobRepository.getJobsByEmployer(employerId)
        if (result.isSuccess) {
            _uiState.value = ManagementUiState.Success(
                ManagementData.EmployerData(result.getOrNull() ?: emptyList())
            )
        } else {
            _uiState.value = ManagementUiState.Error(result.exceptionOrNull()?.message ?: "Lỗi")
        }
    }
}