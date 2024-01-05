package com.fattanaufal.storyapp.response

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @field:SerializedName("error")
    var error: Boolean?,

    @field:SerializedName("message")
    val message: String
)
