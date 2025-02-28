package com.example.parserapp.domain

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.TelephonyManager
import android.util.Log
import com.example.parserapp.domain.service.SmsService

class SmsReceiver : BroadcastReceiver() {

    @SuppressLint("ServiceCast", "MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {
            for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                val sender = smsMessage.displayOriginatingAddress
                val body = smsMessage.messageBody

                val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val phoneNumber = telephonyManager.line1Number

                Log.d("ParserSms", "Получено SMS от: $sender, текст: $body")

                val smsIntent = Intent(context, SmsService::class.java).apply {
                    putExtra("phoneNumber", phoneNumber)
                    putExtra("sender", sender)
                    putExtra("message", body)
                    putExtra("isSms", true)
                }

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    context.startForegroundService(smsIntent)
                } else {
                    context.startService(smsIntent)
                }
            }
        }
    }
}