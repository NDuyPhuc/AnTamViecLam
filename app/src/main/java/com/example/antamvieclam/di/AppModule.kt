package com.example.antamvieclam.di

import com.cloudinary.android.MediaManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Dependencies sẽ sống sót suốt vòng đời ứng dụng
object AppModule {

    @Provides
    @Singleton // Chỉ tạo một instance duy nhất
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideCloudinary(): MediaManager {
        // Vì MediaManager đã được khởi tạo trong Application class,
        // ở đây chúng ta chỉ cần lấy ra instance đã tồn tại đó.
        return MediaManager.get()
    }
}