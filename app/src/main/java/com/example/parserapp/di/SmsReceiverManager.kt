package com.example.parserapp.di

import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.provider.Telephony
import androidx.annotation.RequiresApi
import com.example.parserapp.domain.SmsReceiver

object SmsReceiverManager {
    private var smsReceiver: SmsReceiver? = null
    private var isRegistered = false

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun registerReceiver(context: Context) {
        if (smsReceiver == null && !isRegistered) {
            smsReceiver = SmsReceiver()
            val intentFilter = IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
            context.registerReceiver(smsReceiver, intentFilter, Context.RECEIVER_EXPORTED)
            isRegistered = true
        }
    }

    fun unregisterReceiver(context: Context) {
        if (isRegistered && smsReceiver != null) {
            try {
                context.unregisterReceiver(smsReceiver)
                isRegistered = false
                smsReceiver = null
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        }
    }


}