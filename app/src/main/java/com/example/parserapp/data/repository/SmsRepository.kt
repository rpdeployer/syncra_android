package com.example.parserapp.data.repository

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.parserapp.domain.SmsWorker

object SmsRepository  {

    fun scheduleSmsUpload(context: Context, phoneNumber: String, sender: String, message: String,
                          timestamp: String, timestampLog: String, isSms: Boolean) {
        val inputData = workDataOf(
            "phoneNumber" to phoneNumber,
            "sender" to sender,
            "message" to message,
            "timestamp" to timestamp,
            "timestampLog" to timestampLog,
            "isSms" to isSms
        )

        val request = OneTimeWorkRequestBuilder<SmsWorker>()
            .addTag("SMS_SEND_TO_SERVER_JOB")
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueue(
            request
        )
    }

    suspend fun uploadPendingSms(context: Context, timestamp: Long, from: String, to: String, message: String,
                                 isSms: Boolean) {
        val result = MessageRepository(context).sendSms(timestamp, from, to, message, isSms)
        if (!result) {
            throw RuntimeException("Message not sent!")
        }
    }

}