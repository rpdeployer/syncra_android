package com.example.parserapp.data.network

import com.example.parserapp.data.model.ExitRequest
import com.example.parserapp.data.model.ExitResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ExitService {

    @POST("/api/mobile/disconnect")
    suspend fun exit(@Body request: ExitRequest): Response<ExitResponse>

}