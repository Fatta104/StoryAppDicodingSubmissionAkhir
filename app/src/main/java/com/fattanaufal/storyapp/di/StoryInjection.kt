package com.fattanaufal.storyapp.di

import android.content.Context
import android.location.Geocoder
import com.fattanaufal.storyapp.R
import com.fattanaufal.storyapp.local.StoryDatabase
import com.fattanaufal.storyapp.preference.UserPreferences
import com.fattanaufal.storyapp.preference.dataStore
import com.fattanaufal.storyapp.story.ApiStoryConfig
import com.fattanaufal.storyapp.ui.story.StoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object StoryInjection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = UserPreferences.getInstance(context.dataStore)
        val apiService = ApiStoryConfig.getApiService()
        val storyDB = StoryDatabase.getInstance(context)
        val geocoder = Geocoder(context)
        val locationDefault = context.getString(R.string.no_valid_location)
        return StoryRepository.getInstance(apiService, storyDB, pref, geocoder, locationDefault)

    }
}