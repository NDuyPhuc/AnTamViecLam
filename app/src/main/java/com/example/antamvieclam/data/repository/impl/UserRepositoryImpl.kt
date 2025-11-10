package com.example.antamvieclam.data.repository.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.antamvieclam.data.model.User
import com.example.antamvieclam.data.repository.ProfileResult
import com.example.antamvieclam.data.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.roundToInt

interface UserRepository {
    // Sửa lại hàm để nhận User object và Uri
    suspend fun createUserProfile(user: User, imageUri: Uri?): ProfileResult
    suspend fun getUserProfile(uid: String): User?
    suspend fun doesProfileExist(uid: String): Boolean
}


class UserRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
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

    // THAY ĐỔI TRONG HÀM NÀY
    override suspend fun createUserProfile(user: User, imageUri: Uri?): ProfileResult {
        return try {
            if (imageUri != null) {
                // 1. Nén ảnh từ Uri thành một ByteArray
                val compressedImageData = compressImage(imageUri)
                if (compressedImageData == null) {
                    return ProfileResult.Error("Không thể xử lý ảnh được chọn.")
                }

                // 2. Tải lên dữ liệu ảnh đã nén
                val uploadedImageUrl = uploadImage(compressedImageData)
                if (uploadedImageUrl != null) {
                    user.profileImageUrl = uploadedImageUrl
                } else {
                    return ProfileResult.Error("Tải ảnh lên thất bại.")
                }
            }

            usersCollection.document(user.uid).set(user).await()
            ProfileResult.Success

        } catch (e: Exception) {
            ProfileResult.Error(e.localizedMessage ?: "Tạo hồ sơ thất bại.")
        }
    }

    // HÀM MỚI: Để nén ảnh
    private fun compressImage(uri: Uri): ByteArray? {
        // Sử dụng ContentResolver để lấy luồng dữ liệu từ Uri
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null

        // 1. Decode luồng dữ liệu thành một đối tượng Bitmap
        val originalBitmap = BitmapFactory.decodeStream(inputStream)

        // 2. Resize ảnh nếu nó quá lớn (ví dụ: giữ chiều lớn nhất là 1080px)
        val maxHeight = 1080.0
        val maxWidth = 1080.0
        val ratio = (originalBitmap.width.toDouble() / originalBitmap.height.toDouble()).coerceAtLeast(1.0)

        val newWidth = (maxWidth / ratio).roundToInt()
        val newHeight = (maxHeight / ratio).roundToInt()

        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)

        // 3. Nén ảnh đã resize thành định dạng JPEG với chất lượng 80%
        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)

        return outputStream.toByteArray()
    }


    // THAY ĐỔI HÀM NÀY: Giờ sẽ nhận vào ByteArray thay vì Uri
    private suspend fun uploadImage(imageData: ByteArray): String? {
        return suspendCoroutine { continuation ->
            // Sử dụng phương thức upload nhận ByteArray
            mediaManager.upload(imageData)
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