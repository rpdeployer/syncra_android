package com.example.parserapp.domain

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.parserapp.BuildConfig
import com.example.parserapp.data.entities.LogMessage
import com.example.parserapp.data.repository.SmsRepository
import com.example.parserapp.di.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SmsWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val maxRetries = BuildConfig.INTERVAL.toInt()
        val phoneNumber = inputData.getString("phoneNumber") ?: return Result.failure()
        val sender = inputData.getString("sender") ?: return Result.failure()
        val message = inputData.getString("message") ?: return Result.failure()
        val timestamp = inputData.getString("timestamp") ?: return Result.failure()

        val database = AppDatabase.getDatabase(applicationContext)
        val logDao = database.logMessageDao()

        var attempt = 0

        while (attempt < maxRetries) {
            val timestampLog = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date())

            try {
                SmsRepository.uploadPendingSms(
                    applicationContext,
                    timestamp.toLong(),
                    sender,
                    phoneNumber,
                    message,
                    true
                )

                withContext(Dispatchers.IO) {
                    logDao.insertLogMessage(
                        LogMessage(
                            message = "Сообщение отправлено",
                            dateTime = timestampLog,
                            isSuccess = true
                        )
                    )
                }

              return Result.success()
            } catch (e: Exception) {
                Log.d("ParserSms", "Ошибка при отправке сообщения: $sender - $message\n${e.message}")
                e.printStackTrace()
                attempt++
                if (attempt >= maxRetries) {
                    withContext(Dispatchers.IO) {
                        logDao.insertLogMessage(
                            LogMessage(
                                message = "Сообщение не отправлено",
                                dateTime = timestampLog,
                                isSuccess = false
                            )
                        )
                    }
                    return Result.failure()
                } else {
                    withContext(Dispatchers.IO) {
                        logDao.insertLogMessage(
                            LogMessage(
                                message = "Повтор отправки сообщения",
                                dateTime = timestampLog,
                                isSuccess = false
                            )
                        )
                    }
                }
                delay(60_000)
            }
        }

        return Result.failure()

    }

}
