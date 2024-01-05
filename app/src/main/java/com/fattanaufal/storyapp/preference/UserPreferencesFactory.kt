package com.fattanaufal.storyapp.preference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class UserPreferencesFactory(private val pref: UserPreferences) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserPrefViewModel::class.java)) {
            return UserPrefViewModel(pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}