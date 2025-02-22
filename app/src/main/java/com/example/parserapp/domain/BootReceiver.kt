package com.example.parserapp.domain

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.parserapp.data.store.DataStoreManager
import com.example.parserapp.di.SmsReceiverManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (Intent.ACTION_BOOT_COMPLETED == intent?.action) {
            val authManager = DataStoreManager(context)

            val isAuthorized = runBlocking { authManager.getKey().first() }
            if (!isAuthorized.isNullOrEmpty()) {
                SmsReceiverManager.registerReceiver(context)
            }
        }
    }
}