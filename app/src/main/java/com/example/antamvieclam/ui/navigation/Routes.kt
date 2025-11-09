package com.example.antamvieclam.ui.navigation

object Routes {
    const val LOGIN_SCREEN = "login"
    const val OTP_SCREEN = "otp/{verificationId}" // Chúng ta cần truyền verificationId
    const val CREATE_PROFILE_SCREEN = "create_profile"
    const val HOME_SCREEN = "home" // Màn hình chính sau khi đăng nhập và có hồ sơ
}