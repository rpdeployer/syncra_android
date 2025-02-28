package com.example.parserapp.di

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.parserapp.domain.StatusWorker
import java.util.concurrent.TimeUnit

object StatusManager {

    fun startStatusWorker(context: Context) {
        val workRequest = OneTimeWorkRequestBuilder<StatusWorker>()
            .addTag("STATUS_SEND_TO_SERVER_JOB")
            .setInitialDelay(3, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueue(
            workRequest
        )
    }

}