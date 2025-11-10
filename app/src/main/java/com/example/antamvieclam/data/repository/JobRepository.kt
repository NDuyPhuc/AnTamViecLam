// app/src/main/java/com/example/antamvieclam/data/repository/JobRepository.kt
package com.example.antamvieclam.data.repository

import com.example.antamvieclam.data.model.Job
import com.google.firebase.firestore.DocumentSnapshot

interface JobRepository {
    // Task 2.1
    suspend fun postJob(job: Job): Result<Unit>

    // Task 2.2
    suspend fun getJobs(lastVisibleJob: DocumentSnapshot?): Result<Pair<List<Job>, DocumentSnapshot?>>
    suspend fun getJobDetails(jobId: String): Result<Job>
    suspend fun applyForJob(jobId: String, employerId: String, applicantId: String): Result<Unit>
}