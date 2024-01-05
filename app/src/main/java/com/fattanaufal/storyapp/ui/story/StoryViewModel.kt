package com.fattanaufal.storyapp.ui.story

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.fattanaufal.storyapp.local.StoryEntity
import com.fattanaufal.storyapp.response.AddStoryResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException


class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    val locationStories: LiveData<List<StoryEntity>> = storyRepository.getAllStoriesFromDB()

    val imageUri = MutableLiveData<Uri>()

    private val _uploadResponse = MutableLiveData<AddStoryResponse>()
    val uploadResponse: LiveData<AddStoryResponse> = _uploadResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage


    fun listStories(): LiveData<PagingData<StoryEntity>> = storyRepository.getAllStories().cachedIn(viewModelScope)

    fun getDetailStoryByLatLong(lat: Double, lon: Double) = storyRepository.getOneStoryByLatLong(lat, lon)

    fun uploadStory(file: MultipartBody.Part, description: RequestBody, lat: Float? = null, lon: Float? = null){
        viewModelScope.launch {
            try {
                //get user data
                val response = storyRepository.addStory(file, description, lat, lon)
                if (response.isSuccessful) {
                    _uploadResponse.value = response.body()
                }else{
                    _errorMessage.value = response.message()
                }

                Log.e(TAG, "onFailure: $response")

            } catch (e: HttpException) {
                //get error message
                _errorMessage.value = e.message()
            }
        }
    }



    companion object {
        private val TAG = StoryViewModel::class.java.simpleName
    }
}