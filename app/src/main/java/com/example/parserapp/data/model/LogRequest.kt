package com.example.parserapp.data.model


import com.google.gson.annotations.SerializedName

data class LogUploadRequest(
    @SerializedName("deviceId") var deviceId: String,
    @SerializedName("logs") val logs: List<String>
)

data class LogUploadResponse(
    @SerializedName("success") val success: Boolean
)