package com.fattanaufal.storyapp.di

import android.content.Context
import com.fattanaufal.storyapp.api.ApiAuthConfig
import com.fattanaufal.storyapp.auth.AuthRepository

object AuthInjection {
    fun provideRepository(context: Context): AuthRepository {
        val apiService = ApiAuthConfig.getApiService()
        return AuthRepository.getInstance(apiService)
    }
}