package com.example.parserapp.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.util.Base64
import com.example.parserapp.BuildConfig
import com.example.parserapp.data.model.LogUploadRequest
import com.example.parserapp.data.model.MessageRequest
import com.example.parserapp.data.network.MessagesService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class MessageRepository(context: Context) {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.ROUTER_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val messageService = retrofit.create(MessagesService::class.java)

    @SuppressLint("HardwareIds")
    private val deviceId: String = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ANDROID_ID
    )

    suspend fun sendSms(timestamp: Long, from: String, to: String, message: String, isSms: Boolean): Boolean {
        val guid = UUID.randomUUID().toString()
        val timestampSign = System.currentTimeMillis() / 1000
        val salt = generateSalt()
        val signature = generateSignature(timestampSign, salt)

        val request = MessageRequest(guid, from, to, timestamp, message, isSms, deviceId)

        return try {
            val response = messageService.sendSms(
                signature,
                salt,
                timestampSign,
                request
            )
            response.isSuccessful && response.body()?.success == true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun sendLog(log: List<String>): Boolean {
        val timestamp = System.currentTimeMillis() / 1000
        val salt = generateSalt()
        val signature = generateSignature(timestamp, salt)

        return try {
            val request = LogUploadRequest(deviceId, log)
            val response = messageService.uploadLogs(
                signature,
                salt,
                timestamp,
                request
            )
            response.isSuccessful && response.body()?.success == true
        } catch (e: Exception) {
            false
        }
    }

    private val key = "LLSDaslwNJ#J!@K#JKeSjI@#I"

    private fun generateSignature(timestamp: Long, salt: String): String {
        val payload = "$timestamp$salt"
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(key.toByteArray(), "HmacSHA256"))
        val hmacBytes = mac.doFinal(payload.toByteArray())
        return Base64.encodeToString(hmacBytes, Base64.NO_WRAP)
    }

    private fun generateSalt(): String = UUID.randomUUID().toString()

}