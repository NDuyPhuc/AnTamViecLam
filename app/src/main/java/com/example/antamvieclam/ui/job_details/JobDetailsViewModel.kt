package com.example.antamvieclam.ui.job_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.antamvieclam.data.model.Job
import com.example.antamvieclam.data.repository.JobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Lớp trạng thái cho màn hình chi tiết
sealed class JobDetailsUiState {
    object Loading : JobDetailsUiState()
    data class Success(val job: Job) : JobDetailsUiState()
    data class Error(val message: String) : JobDetailsUiState()
}

@HiltViewModel
class JobDetailsViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    // SavedStateHandle giúp lấy argument (jobId) từ navigation route
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<JobDetailsUiState>(JobDetailsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        // Lấy jobId từ arguments và tải dữ liệu
        savedStateHandle.get<String>("jobId")?.let { jobId ->
            loadJobDetails(jobId)
        }
    }

    private fun loadJobDetails(jobId: String) {
        viewModelScope.launch {
            _uiState.value = JobDetailsUiState.Loading
            val result = jobRepository.getJobDetails(jobId)
            if (result.isSuccess) {
                result.getOrNull()?.let { job ->
                    _uiState.value = JobDetailsUiState.Success(job)
                } ?: run {
                    _uiState.value = JobDetailsUiState.Error("Không tìm thấy công việc.")
                }
            } else {
                _uiState.value = JobDetailsUiState.Error(
                    result.exceptionOrNull()?.message ?: "Lỗi khi tải dữ liệu."
                )
            }
        }
    }
}