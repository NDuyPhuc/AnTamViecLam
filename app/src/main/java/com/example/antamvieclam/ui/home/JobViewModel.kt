package com.example.antamvieclam.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.antamvieclam.data.model.Job
import com.example.antamvieclam.data.repository.AuthRepository
import com.example.antamvieclam.data.repository.JobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Lớp trạng thái cho màn hình danh sách công việc
sealed class JobListUiState {
    object Loading : JobListUiState()
    data class Success(val jobs: List<Job>) : JobListUiState()
    data class Error(val message: String) : JobListUiState()
}

@HiltViewModel
class JobViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<JobListUiState>(JobListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadAllJobs()
    }

    // Hàm để tải tất cả công việc cho NLĐ
    fun loadAllJobs() {
        viewModelScope.launch {
            _uiState.value = JobListUiState.Loading
            jobRepository.getJobs(null).let { result ->
                if (result.isSuccess) {
                    val jobs = result.getOrNull()?.first ?: emptyList()
                    _uiState.value = JobListUiState.Success(jobs)
                } else {
                    _uiState.value = JobListUiState.Error(
                        result.exceptionOrNull()?.message ?: "Đã có lỗi xảy ra"
                    )
                }
            }
        }
    }

    fun loadJobsForCurrentUser() {
        viewModelScope.launch {
            _uiState.value = JobListUiState.Loading
            val currentUserId = authRepository.getCurrentUserId()
            if (currentUserId == null) {
                _uiState.value = JobListUiState.Error("Không thể xác thực người dùng.")
                return@launch
            }

            jobRepository.getJobsByEmployer(currentUserId as String).let { result ->
                if (result.isSuccess) {
                    val jobs = result.getOrNull() ?: emptyList()
                    _uiState.value = JobListUiState.Success(jobs)
                } else {
                    _uiState.value = JobListUiState.Error(
                        result.exceptionOrNull()?.message ?: "Đã có lỗi xảy ra"
                    )
                }
            }
        }
    }
}