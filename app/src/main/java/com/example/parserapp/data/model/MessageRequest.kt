package com.example.parserapp.data.model


import com.google.gson.annotations.SerializedName

data class MessageRequest(
    @SerializedName("from") val from: String,
    @SerializedName("to") val to: String,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("message") val message: String,
)

data class MessageResponse(
    @SerializedName("success") val success: Boolean
)