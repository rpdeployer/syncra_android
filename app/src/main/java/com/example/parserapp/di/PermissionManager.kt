package com.example.parserapp.di

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat.checkSelfPermission
import com.example.parserapp.domain.NotificationListener
import javax.inject.Inject

class PermissionManager @Inject constructor(
    private val context: Context
) {
    private lateinit var smsPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var phonePermissionLauncher: ActivityResultLauncher<String>
    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var networkPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var pushPermissionLauncher: ActivityResultLauncher<Intent>
    private lateinit var batteryOptimizationLauncher: ActivityResultLauncher<Intent>

    private val smsPermission = android.Manifest.permission.RECEIVE_SMS
    private val cameraPermission = android.Manifest.permission.CAMERA
    private val networkStatePermission = android.Manifest.permission.ACCESS_NETWORK_STATE
    private val phoneNumbersPermission = android.Manifest.permission.READ_PHONE_NUMBERS
    private val phoneStateNumbersPermission = android.Manifest.permission.READ_PHONE_STATE

    fun initializePermissionLaunchers(
        phoneLauncher: ActivityResultLauncher<String>,
        smsLauncher: ActivityResultLauncher<String>,
        cameraLauncher: ActivityResultLauncher<String>,
        networkLauncher: ActivityResultLauncher<String>,
        pushLauncher: ActivityResultLauncher<Intent>,
        batteryLauncher: ActivityResultLauncher<Intent>
    ) {
        phonePermissionLauncher = phoneLauncher
        smsPermissionLauncher = smsLauncher
        cameraPermissionLauncher = cameraLauncher
        networkPermissionLauncher = networkLauncher
        pushPermissionLauncher = pushLauncher
        batteryOptimizationLauncher = batteryLauncher
    }

    private fun checkPermissionFor(permission: String) =
        checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED


    fun requestSmsPermission() {
        smsPermissionLauncher.launch(smsPermission)
    }

    fun requestPhonePermission() {
        phonePermissionLauncher.launch(phoneNumbersPermission)
        phonePermissionLauncher.launch(phoneStateNumbersPermission)
    }

    fun requestCameraPermission() {
        cameraPermissionLauncher.launch(cameraPermission)
    }

    fun requestNetworkPermission() {
        networkPermissionLauncher.launch(networkStatePermission)
    }

    fun requestPushPermission() {
        pushPermissionLauncher.launch(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }

    fun requestBatteryOptimizationDisabling() {
        batteryOptimizationLauncher.launch(
            Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                .setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                .setData(Uri.parse("package:" + context.packageName))
        )
    }

    fun isSmsPermissionGranted(): Boolean {
        return checkPermissionFor(smsPermission)
    }

    fun isPhonePermissionGranted(): Boolean {
        return checkPermissionFor(phoneNumbersPermission)
    }

    fun isCameraPermissionGranted(): Boolean {
        return checkPermissionFor(cameraPermission)
    }

    fun isNetworkPermissionGranted(): Boolean {
        return checkPermissionFor(networkStatePermission)
    }

    fun isPushPermissionGranted(): Boolean {
        val componentName = ComponentName(context, NotificationListener::class.java)
        val enabledListeners = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        )
        return enabledListeners?.contains(componentName.flattenToString()) ?: false
    }

    fun isIgnoringBatteryOptimizations(): Boolean {
        val pwrm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val name = context.applicationContext.packageName
        return pwrm.isIgnoringBatteryOptimizations(name)
    }

}

