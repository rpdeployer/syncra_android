package com.example.parserapp.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import com.example.parserapp.BuildConfig
import com.example.parserapp.data.model.LogUploadRequest
import com.example.parserapp.data.model.MessageRequest
import com.example.parserapp.data.network.MessagesService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MessageRepository(context: Context) {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val messageService = retrofit.create(MessagesService::class.java)

    @SuppressLint("HardwareIds")
    private val deviceId: String = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ANDROID_ID
    )

    suspend fun sendSms(timestamp: Long, from: String, to: String, message: String): Boolean {
        val request = MessageRequest(
            from, to, timestamp, message
        )
        return try {
            val response = messageService.sendSms(request)
            response.isSuccessful && response.body()?.success == true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun sendLog(request: LogUploadRequest): Boolean {
        return try {
            val response = messageService.uploadLogs(request)
            response.isSuccessful && response.body()?.success == true
        } catch (e: Exception) {
            false
        }
    }

}