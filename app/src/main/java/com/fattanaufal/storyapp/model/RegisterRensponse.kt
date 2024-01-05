package com.fattanaufal.storyapp.model

import com.google.gson.annotations.SerializedName

data class RegisterRensponse(

    @field:SerializedName("error")
    var error: Boolean?,

    @field:SerializedName("message")
    val message: String?,
)
