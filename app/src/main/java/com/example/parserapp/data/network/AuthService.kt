package com.example.parserapp.data.network

import com.example.parserapp.data.model.AuthRequest
import com.example.parserapp.data.model.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("/api/mobile/connect")
    suspend fun validateKey(@Body request: AuthRequest): Response<AuthResponse>

}