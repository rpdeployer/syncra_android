package com.example.parserapp.data.model


import com.google.gson.annotations.SerializedName

data class StatusRequest(
    @SerializedName("deviceId") var deviceId: String,
    @SerializedName("battery") val battery: String,
    @SerializedName("network") val network: String
)