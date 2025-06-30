package com.example.parserapp.data.model


import com.google.gson.annotations.SerializedName

data class ExitRequest(
    @SerializedName("key") val key: String,
    @SerializedName("deviceId") val deviceId: String
)