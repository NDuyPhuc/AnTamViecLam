package com.example.antamvieclam.data.repository.impl

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.antamvieclam.data.model.User
import com.example.antamvieclam.data.repository.ProfileResult
import com.example.antamvieclam.data.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val mediaManager: MediaManager // Đã được provide từ AppModule
) : UserRepository {

    private val usersCollection = firestore.collection("users")

    override suspend fun getUserProfile(uid: String): User? {
        return try {
            usersCollection.document(uid).get().await().toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun createUserProfile(user: User, imageUri: Uri?): ProfileResult {
        return try {
            if (imageUri != null) {
                // 1. Tải ảnh lên Cloudinary
                val uploadedImageUrl = uploadImage(imageUri)
                if (uploadedImageUrl != null) {
                    // 2. Lưu thông tin user với URL ảnh vào Firestore
                    usersCollection.document(user.uid).set(user.copy(profileImageUrl = uploadedImageUrl)).await()
                    ProfileResult.Success
                } else {
                    ProfileResult.Error("Tải ảnh lên thất bại.")
                }
            } else {
                // 2. Chỉ lưu thông tin user (không có ảnh)
                usersCollection.document(user.uid).set(user).await()
                ProfileResult.Success
            }
        } catch (e: Exception) {
            ProfileResult.Error(e.localizedMessage ?: "Tạo hồ sơ thất bại.")
        }
    }

    private suspend fun uploadImage(imageUri: Uri): String? {
        return suspendCoroutine { continuation ->
            mediaManager.upload(imageUri)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val secureUrl = resultData["secure_url"] as? String
                        continuation.resume(secureUrl)
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        continuation.resume(null)
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {}
                }).dispatch()
        }
    }
    override suspend fun doesProfileExist(uid: String): Boolean {
        return try {
            usersCollection.document(uid).get().await().exists()
        } catch (e: Exception) {
            false
        }
    }
}