package com.fattanaufal.storyapp.preference

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class UserPrefViewModel(private val pref: UserPreferences) : ViewModel() {
    fun saveSessionUser(userData: UserSession) {
        viewModelScope.launch {
            pref.saveSessionUser(userData)
        }
    }

    fun getSessionUser(): LiveData<UserSession> {
        return pref.getSessionUser().asLiveData()
    }

    fun logoutUser(){
        viewModelScope.launch {
            pref.removeSessionUser()
        }
    }
}