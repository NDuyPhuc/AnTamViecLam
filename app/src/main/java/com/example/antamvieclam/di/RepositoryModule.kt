package com.example.antamvieclam.di

import com.example.antamvieclam.data.repository.AuthRepository
import com.example.antamvieclam.data.repository.JobRepository
import com.example.antamvieclam.data.repository.UserRepository
import com.example.antamvieclam.data.repository.impl.AuthRepositoryImpl
import com.example.antamvieclam.data.repository.impl.JobRepositoryImpl
import com.example.antamvieclam.data.repository.impl.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Cài đặt module này vào ApplicationComponent
abstract class RepositoryModule {

    @Binds
    @Singleton // Đảm bảo chỉ có một instance duy nhất của repository trong toàn app
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository // Khi ai đó yêu cầu AuthRepository, Hilt sẽ cung cấp AuthRepositoryImpl

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository // Tương tự, yêu cầu UserRepository -> cung cấp UserRepositoryImpl

    @Binds
    @Singleton
    abstract fun bindJobRepository(
        jobRepositoryImpl: JobRepositoryImpl
    ): JobRepository // Khi ai đó yêu cầu JobRepository, Hilt sẽ cung cấp JobRepositoryImpl
}