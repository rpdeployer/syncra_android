package com.example.parserapp.data.model

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("failures") val failures: List<Failure> = emptyList(),
    @SerializedName("value") val value: T?,
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("isFailure") val isFailure: Boolean
)

data class Failure(
    @SerializedName("id") val id: String,
    @SerializedName("description") val description: String
)