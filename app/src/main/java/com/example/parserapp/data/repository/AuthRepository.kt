package com.example.parserapp.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import com.example.parserapp.BuildConfig
import com.example.parserapp.data.model.AuthRequest
import com.example.parserapp.data.network.AuthService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthRepository(context: Context) {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.CORE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val authService = retrofit.create(AuthService::class.java)

    @SuppressLint("HardwareIds")
    private val deviceId: String = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ANDROID_ID
    )

    suspend fun validateKey(apiKey: String): Pair<String, Boolean> {
        val request = AuthRequest(apiKey, deviceId)
        return try {
            val response = authService.validateKey(request)
            response.isSuccessful && response.body()?.success == true
            Pair(response.body()!!.name, response.body()!!.success)
        } catch (e: Exception) {
             Pair("", false)
        }
    }

}