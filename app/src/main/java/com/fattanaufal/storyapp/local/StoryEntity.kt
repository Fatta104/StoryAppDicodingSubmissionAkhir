package com.fattanaufal.storyapp.local

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity("stories")
data class StoryEntity(
    @PrimaryKey
    val id: String,

    val name: String,

    val description: String,

    val photoUrl: String,

    val createdAt: String,

    val lat: Double,

    val lon: Double,

    val cityAndProvince: String,

    ) : Parcelable
