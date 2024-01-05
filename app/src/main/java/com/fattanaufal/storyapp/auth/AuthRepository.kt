package com.fattanaufal.storyapp.auth

import com.fattanaufal.storyapp.api.ApiAuthService
import com.fattanaufal.storyapp.model.LoginResponse
import com.fattanaufal.storyapp.utils.wrapEspressoIdlingResource

class AuthRepository private constructor(private val apiAuthService: ApiAuthService) {

    suspend fun signInUser(
        email: String,
        password: String,
    ) : LoginResponse {
        wrapEspressoIdlingResource {
            return apiAuthService.loginUser(email, password)
        }
    }

    suspend fun createUser(
        name: String,
        email: String,
        password: String,
    ) = apiAuthService.registerUser(name, email, password)


    companion object {
        private val TAG = AuthRepository::class.java.simpleName

        @Volatile
        private var instance: AuthRepository? = null
        fun getInstance(
            apiAuthService: ApiAuthService,
        ): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(apiAuthService)
            }.also { instance = it }
    }
}