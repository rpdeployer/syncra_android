package com.example.parserapp.domain

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.parserapp.data.repository.StatusRepository
import com.example.parserapp.di.StatusManager

class StatusWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        try {
            StatusRepository.sendStatus(applicationContext)
            return Result.success()
        } catch (e: Exception) {
            Log.e("StatusWorker", e.message!!)
            return Result.success()
        } finally {
            StatusManager.startStatusWorker(applicationContext)
        }
    }

}