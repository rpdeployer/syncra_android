package com.example.parserapp.data.network

import com.example.parserapp.data.model.VersionResponse
import retrofit2.Response
import retrofit2.http.GET

interface UpdateService {

    @GET("/api/mobile/update")
    suspend fun checkUpdate(): Response<VersionResponse>

}