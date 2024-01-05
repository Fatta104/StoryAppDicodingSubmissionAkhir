package com.fattanaufal.storyapp.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fattanaufal.storyapp.model.RegisterRensponse
import com.fattanaufal.storyapp.preference.UserSession
import com.fattanaufal.storyapp.response.ErrorResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AuthViewModel(private val authRepository: AuthRepository): ViewModel() {
    private val _registerResponse = MutableLiveData<RegisterRensponse?>()
    val registerResponse: LiveData<RegisterRensponse?> = _registerResponse

    private val _errorLoginResponse = MutableLiveData<ErrorResponse?>(null)
    val errorLoginResponse: LiveData<ErrorResponse?> = _errorLoginResponse

    private val _loginResponse = MutableLiveData<UserSession?>(null)
    val loginResponse: LiveData<UserSession?> = _loginResponse


    // kebutuhan login
    fun getUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                //get user data
                val response = authRepository.signInUser(email, password)
                val userSession = UserSession()
                response.loginResult.apply {
                    userSession.userId = userId
                    userSession.name = name
                    userSession.token = "Bearer $token"
                }
                _loginResponse.value = userSession

            } catch (e: HttpException) {
                //get error message
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                _errorLoginResponse.value = errorBody
            }


        }
    }

    fun restoreErrorLogin() {
        _errorLoginResponse.value = null
    }

    fun restoreLoginResponse() {
        _loginResponse.value = null
    }

    // akhir kebutuhan login


    // kebutuhan register
    fun insertUser(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                //get success message
                val message = authRepository.createUser(name, email, password)
                _registerResponse.value = message


            } catch (e: HttpException) {
                //get error message
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, RegisterRensponse::class.java)
                _registerResponse.value = errorBody
            }
        }
    }

    fun restoreError(error: Boolean?) {
        _registerResponse.value?.error = error
    }
    // akhir kebutuhan register


    companion object {
        private val TAG = AuthViewModel::class.java.simpleName
    }

}