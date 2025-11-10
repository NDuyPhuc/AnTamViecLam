// app/src/main/java/com/example/antamvieclam/data/repository/impl/JobRepositoryImpl.kt
package com.example.antamvieclam.data.repository.impl

import com.example.antamvieclam.data.model.Job
import com.example.antamvieclam.data.model.JobApplication
import com.example.antamvieclam.data.repository.JobRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class JobRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : JobRepository {

    private val jobsCollection = firestore.collection("jobs")
    private val applicationsCollection = firestore.collection("job_applications")
    companion object {
        private const val PAGE_SIZE = 10L
    }

    override suspend fun postJob(job: Job): Result<Unit> {
        return try {
            // Tạo một document mới và lấy ID của nó
            val newJobRef = jobsCollection.document()
            job.id = newJobRef.id
            newJobRef.set(job).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getJobs(lastVisibleJob: DocumentSnapshot?): Result<Pair<List<Job>, DocumentSnapshot?>> {
        return try {
            val query = jobsCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(PAGE_SIZE)

            val finalQuery = if (lastVisibleJob != null) {
                query.startAfter(lastVisibleJob)
            } else {
                query
            }

            val querySnapshot = finalQuery.get().await()
            val jobs = querySnapshot.toObjects(Job::class.java)
            val newLastVisible = querySnapshot.documents.lastOrNull()

            Result.success(Pair(jobs, newLastVisible))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getJobDetails(jobId: String): Result<Job> {
        return try {
            val document = jobsCollection.document(jobId).get().await()
            val job = document.toObject(Job::class.java)
            if (job != null) {
                Result.success(job)
            } else {
                Result.failure(Exception("Không tìm thấy công việc."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun applyForJob(jobId: String, employerId: String, applicantId: String): Result<Unit> {
        return try {
            val newApplicationRef = applicationsCollection.document()
            val application = JobApplication(
                id = newApplicationRef.id,
                jobId = jobId,
                employerId = employerId,
                applicantId = applicantId
            )
            newApplicationRef.set(application).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}