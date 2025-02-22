package com.example.parserapp.data.model


import com.google.gson.annotations.SerializedName

data class AuthRequest(
    @SerializedName("key") val key: String,
    @SerializedName("deviceId") val deviceId: String
)

data class AuthResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("name") val name: String
)