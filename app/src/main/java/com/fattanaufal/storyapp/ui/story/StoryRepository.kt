package com.fattanaufal.storyapp.ui.story

import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.fattanaufal.storyapp.local.StoryDatabase
import com.fattanaufal.storyapp.local.StoryEntity
import com.fattanaufal.storyapp.paging.StoryRemoteMediator
import com.fattanaufal.storyapp.preference.UserPreferences
import com.fattanaufal.storyapp.response.AddStoryResponse
import com.fattanaufal.storyapp.story.ApiStoryService
import com.fattanaufal.storyapp.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response


class StoryRepository private constructor(
    private val apiStoryService: ApiStoryService,
    private val storyDatabase: StoryDatabase,
    private val userPreferences: UserPreferences,
    private val geocoder: Geocoder,
    private val locationDefault: String
) {
    fun getAllStories(): LiveData<PagingData<StoryEntity>> {
        wrapEspressoIdlingResource {
            @OptIn(ExperimentalPagingApi::class)
            return Pager(
                config = PagingConfig(
                    pageSize = 2
                ),
                remoteMediator = StoryRemoteMediator(storyDatabase, apiStoryService, userPreferences, geocoder, locationDefault),
                pagingSourceFactory = {
                    storyDatabase.storyDao().getAllStories()
                }
            ).liveData
        }
    }


    fun getAllStoriesFromDB() = storyDatabase.storyDao().getAllLocationStories()

    fun getOneStoryByLatLong(lat: Double, lon: Double) =
        storyDatabase.storyDao().getItemByLatLong(lat, lon)

    suspend fun addStory(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Float? = null,
        lon: Float? = null,
    ): Response<AddStoryResponse> {
        wrapEspressoIdlingResource {
            return apiStoryService.uploadStory(userPreferences.getSessionUser().first().token,file, description, lat, lon)
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(
            apiStoryService: ApiStoryService,
            storyDatabase: StoryDatabase,
            userPreferences: UserPreferences,
            geocoder: Geocoder,
            locationDefault: String
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiStoryService, storyDatabase, userPreferences, geocoder, locationDefault)
            }.also { instance = it }
    }
}