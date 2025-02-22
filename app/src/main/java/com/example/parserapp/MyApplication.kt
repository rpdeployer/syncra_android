package com.example.parserapp

import android.app.Application
import com.example.parserapp.data.store.DataStoreManager
import com.example.parserapp.di.SmsReceiverManager
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
                SmsReceiverManager.registerReceiver(this@MyApplication)
            }
        }
    }
}