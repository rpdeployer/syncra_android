package com.example.parserapp.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import com.example.parserapp.BuildConfig
import com.example.parserapp.data.model.ExitRequest
import com.example.parserapp.data.network.ExitService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ExitRepository(context: Context) {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.CORE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val exitService = retrofit.create(ExitService::class.java)

    @SuppressLint("HardwareIds")
    private val deviceId: String = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ANDROID_ID
    )

    suspend fun exit(apiKey: String): Boolean {
        val request = ExitRequest(apiKey, deviceId)
        return try {
            val response = exitService.exit(request)
            response.isSuccessful && response.body()?.isSuccess!!
        } catch (e: Exception) {
            false
        }
    }

}