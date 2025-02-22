package com.example.parserapp.data.model


import com.google.gson.annotations.SerializedName

data class LogUploadRequest(
    @SerializedName("logs") val logs: List<String>
)

data class LogUploadResponse(
    @SerializedName("success") val success: Boolean
)