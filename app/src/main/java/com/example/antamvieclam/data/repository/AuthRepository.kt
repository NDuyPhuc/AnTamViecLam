package com.example.antamvieclam.data.repository

import com.google.firebase.auth.AuthCredential

// Đơn giản hóa sealed class
sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val exception: Exception) : AuthResult()
}

interface AuthRepository {
    // Hàm mới để đăng nhập với Google credential
    suspend fun signInWithGoogle(credential: AuthCredential): AuthResult
    fun getCurrentUserId(): String?
    fun signOut()
}