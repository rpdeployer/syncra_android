package com.example.parserapp.domain

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Log
import com.example.parserapp.domain.service.SmsService

class SmsReceiver : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            if (messages.isNotEmpty()) {
                val sender = messages[0].displayOriginatingAddress
                val fullMessage = StringBuilder()

                for (msg in messages) {
                    fullMessage.append(msg.messageBody)
                }

                val phoneNumber = getPhoneNumber(context) ?: "Неизвестный номер"

                Log.d("ParserSms", "Получено SMS от: $sender, текст: $fullMessage")

                val smsIntent = Intent(context, SmsService::class.java).apply {
                    putExtra("phoneNumber", phoneNumber)
                    putExtra("sender", sender)
                    putExtra("message", fullMessage.toString()) // Передаем объединенное сообщение
                    putExtra("isSms", true)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(smsIntent)
                } else {
                    context.startService(smsIntent)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getPhoneNumber(context: Context): String? {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        var phoneNumber: String? = telephonyManager.line1Number

        Log.d("PhoneNumber", "TelephonyManager вернул: $phoneNumber")

        if (phoneNumber.isNullOrEmpty() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            val subscriptionManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
            val subscriptionInfoList = subscriptionManager.activeSubscriptionInfoList

            if (!subscriptionInfoList.isNullOrEmpty()) {
                phoneNumber = subscriptionInfoList[0].number
                Log.d("PhoneNumber", "SubscriptionManager вернул: $phoneNumber")
            }
        }

        if (phoneNumber.isNullOrEmpty()) {
            Log.d("PhoneNumber", "Не удалось получить номер телефона.")
            return null
        }

        return phoneNumber
    }
}