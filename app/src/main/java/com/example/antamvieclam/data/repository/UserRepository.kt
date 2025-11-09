package com.example.antamvieclam.data.repository

import android.net.Uri
import com.example.antamvieclam.data.model.User

sealed class ProfileResult {
    object Success : ProfileResult()
    data class Error(val message: String) : ProfileResult()
}

interface UserRepository {
    suspend fun createUserProfile(user: User, imageUri: Uri?): ProfileResult
    suspend fun getUserProfile(uid: String): User?
    // Thêm các hàm update, delete nếu cần
    suspend fun doesProfileExist(uid: String): Boolean

}