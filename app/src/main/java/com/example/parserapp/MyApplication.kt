package com.example.parserapp

import android.app.Application
import androidx.work.WorkManager
import com.example.parserapp.data.store.DataStoreManager
import com.example.parserapp.di.SmsReceiverManager
import com.example.parserapp.di.StatusManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val authManager = DataStoreManager(this)

        CoroutineScope(Dispatchers.IO).launch {
            val isAuthorized = authManager.getKey().first()
            if (!isAuthorized.isNullOrEmpty()) {
                WorkManager.getInstance(applicationContext)
                    .cancelAllWorkByTag("SMS_SEND_TO_SERVER_JOB")
                WorkManager.getInstance(applicationContext)
                    .cancelAllWorkByTag("STATUS_SEND_TO_SERVER_JOB")
                SmsReceiverManager.registerReceiver(this@MyApplication)
                StatusManager.startStatusWorker(this@MyApplication)
            }
        }
    }

}