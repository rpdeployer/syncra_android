package com.example.parserapp.data.network

import com.example.parserapp.data.model.StatusRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface StatusService{

    @POST("/api/mobile/status")
    suspend fun sendStatus(@Body status: StatusRequest)

}