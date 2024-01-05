package com.fattanaufal.storyapp.story

import com.fattanaufal.storyapp.response.AddStoryResponse
import com.fattanaufal.storyapp.response.ListStoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiStoryService {

    @GET("stories")
    suspend fun getAllStoriesWithLocation(
        @Header("Authorization") token: String,
        @Query("page") page : Int = 1,
        @Query("size") size : Int = 5,
        @Query("location") location : Int? = null ,
    ): ListStoriesResponse


    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float?,
        @Part("lon") lon: Float?,
    ): Response<AddStoryResponse>
}