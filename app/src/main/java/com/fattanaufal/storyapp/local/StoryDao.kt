package com.fattanaufal.storyapp.local

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStories(stories: List<StoryEntity>)

    @Query("SELECT * FROM stories")
    fun getAllStories(): PagingSource<Int, StoryEntity>

    @Query("SELECT * FROM stories")
    fun getAllLocationStories(): LiveData<List<StoryEntity>>

    @Query("SELECT * FROM stories WHERE lat = :lat AND lon = :lon LIMIT 1")
    fun getItemByLatLong(lat: Double, lon:Double): LiveData<StoryEntity>

    @Query("DELETE FROM stories")
    suspend fun deleteAll()
}