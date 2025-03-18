package com.example.parserapp.data.model


import com.google.gson.annotations.SerializedName

data class StatusRequest(
    @SerializedName("deviceId") var deviceId: String,
    @SerializedName("battery") val battery: Int,
    @SerializedName("network") val network: Double
)

data class StatusResponse(
    @SerializedName("name") val name: String,
    @SerializedName("senderNames") val senderNames: List<String>
)