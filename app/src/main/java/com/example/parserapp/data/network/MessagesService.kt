package com.example.parserapp.data.network

import com.example.parserapp.data.model.LogUploadRequest
import com.example.parserapp.data.model.LogUploadResponse
import com.example.parserapp.data.model.MessageRequest
import com.example.parserapp.data.model.MessageResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MessagesService {

    @POST("/api/mobile/sms")
    suspend fun sendSms(@Body request: MessageRequest): Response<MessageResponse>


    @POST("/api/mobile/log")
    suspend fun uploadLogs(@Body logs: LogUploadRequest): Response<LogUploadResponse>

}