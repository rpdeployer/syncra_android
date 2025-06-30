package com.example.parserapp.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import com.example.parserapp.BuildConfig
import com.example.parserapp.data.model.StatusRequest
import com.example.parserapp.data.network.StatusService
import com.example.parserapp.data.store.DataStoreManager
import com.example.parserapp.di.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.random.Random

object StatusRepository {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.CORE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val statusService = retrofit.create(StatusService::class.java)

    @SuppressLint("HardwareIds")
    suspend fun sendStatus(context: Context): Boolean {
        val senderDao = AppDatabase.getDatabase(context).senderDao()
        val senderRepository = SenderRepository(senderDao)

        val deviceId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        val batteryLevel = getBatteryLevel(context)
        val internetSpeed = getInternetSpeed(context)

        val statusRequest = StatusRequest(
            deviceId,
            batteryLevel,
            internetSpeed
        )

        return withContext(Dispatchers.IO) {
            try {
                val response = statusService.sendStatus(statusRequest)
                if (response.isSuccessful && response.body()!!.isSuccess) {
                    response.body()?.let { statusResponse ->
                        DataStoreManager(context).saveName(statusResponse.value!!.name)
                        senderRepository.updateSenders(statusResponse.value!!)
                    }
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                Log.e("StatusRepository", "Ошибка отправки статуса", e)
                false
            }
        }
    }

    private fun getBatteryLevel(context: Context): Int {
        val batteryIntent = context.registerReceiver(null, android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        return if (level != -1 && scale != -1) (level * 100 / scale.toFloat()).toInt() else -1
    }

    fun getInternetSpeed(context: Context): Double {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return 0.0
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return 0.0

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> getWifiSpeed(context)
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> getMobileDataSpeed(context)
            else -> 0.0
        }
    }

    fun getWifiSpeed(context: Context): Double {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.connectionInfo.linkSpeed.toDouble() // Скорость в Mbps
    }

    @SuppressLint("MissingPermission")
    fun getMobileDataSpeed(context: Context): Double {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val networkType = telephonyManager.dataNetworkType

        return when (networkType) {
            TelephonyManager.NETWORK_TYPE_NR -> Random.nextDouble(150.0, 500.0) // 5G (гипотетически 1000+ Mbps)
            TelephonyManager.NETWORK_TYPE_LTE -> Random.nextDouble(30.0, 150.0) // 4G (обычно 30-150 Mbps)
            TelephonyManager.NETWORK_TYPE_HSPAP -> Random.nextDouble(3.0, 25.0) // 3G+ (HSPA+ до 42 Mbps)
            TelephonyManager.NETWORK_TYPE_UMTS -> Random.nextDouble(0.3, 3.0) // 3G (UMTS до 3 Mbps)
            TelephonyManager.NETWORK_TYPE_EDGE -> Random.nextDouble(0.1, 0.2) // 2G (EDGE 0.1-0.3 Mbps)
            TelephonyManager.NETWORK_TYPE_GPRS -> Random.nextDouble(0.05, 0.1)  // 2G (GPRS 0.05-0.1 Mbps)
            else -> 0.0
        }
    }

}