package com.example.parserapp.data.repository

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.work.WorkManager
import com.example.parserapp.BuildConfig
import com.example.parserapp.data.network.UpdateService
import com.example.parserapp.domain.ApkInstaller
import com.example.parserapp.helper.RestartHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class UpdateRepository(context: Context) {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val updateService = retrofit.create(UpdateService::class.java)

    suspend fun checkForUpdate(context: Context) {
        try {
            if (ApkInstaller.hasInstallPermission(context)) {
                val response = withContext(Dispatchers.IO) { updateService.checkUpdate() }
                val currentVersion = BuildConfig.VERSION_NAME

                if (compareVersions(currentVersion, response.body()!!.latestVersion) != -1) {
                    Toast.makeText(context, "У вас уже последняя версия", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(
                        context,
                        "Обновление доступно! Начинаем загрузку...",
                        Toast.LENGTH_SHORT
                    ).show()
                    var result =
                        DownloadRepository(context).downloadApkFile(response.body()!!.apkUrl)
                    if (result) {
                        ApkInstaller.installApk(context)
                    } else {
                        Toast.makeText(
                            context,
                            "Ошибка загрузки обновления",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                ApkInstaller.requestInstallPermission(context)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Ошибка проверки обновлений", Toast.LENGTH_SHORT).show()
        }
    }

    fun compareVersions(version1: String, version2: String): Int {
        val parts1 = version1.split(".").map { it.toIntOrNull() ?: 0 }
        val parts2 = version2.split(".").map { it.toIntOrNull() ?: 0 }

        val maxLength = maxOf(parts1.size, parts2.size)
        val padded1 = parts1 + List(maxLength - parts1.size) { 0 }
        val padded2 = parts2 + List(maxLength - parts2.size) { 0 }

        return padded1.zip(padded2).firstOrNull { (v1, v2) -> v1 != v2 }
            ?.let { (v1, v2) -> v1.compareTo(v2) } ?: 0
    }

    private fun downloadAndInstallApk(context: Context, apkUrl: String) {
        val fileName = "app-latest.apk"
        val destination = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )

        val request = DownloadManager.Request(Uri.parse(apkUrl))
            .setTitle("Syncra обновление")
            .setDescription("Скачивание новой версии...")
            .setDestinationUri(Uri.fromFile(destination))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)

        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = manager.enqueue(request)

        Thread {
            var downloading = true
            while (downloading) {
                val query = DownloadManager.Query().setFilterById(downloadId)
                val cursor = manager.query(query)
                if (cursor.moveToFirst()) {
                    try {
                        val status =
                            cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            downloading = false
                            installApk(context, destination)
                        }
                        if (status == DownloadManager.STATUS_FAILED) {
                            downloading = false
                            WorkManager.getInstance(context).cancelAllWorkByTag("DOWNLOAD_JOB")

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("DownloadReceiver", "Ошибка загрузки")
                        downloading = false
                        WorkManager.getInstance(context).cancelAllWorkByTag("DOWNLOAD_JOB")
                    }
                }
                cursor.close()
            }
        }.start()
    }

    private fun installApk(context: Context, file: File) {
        if (!file.exists()) {
            Log.e("FileCheck", "Файл НЕ найден: ${file.absolutePath}")
        } else {
            Log.d("FileCheck", "Файл найден: ${file.absolutePath}, размер: ${file.length()} байт")
        }

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)


        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        context.startActivity(intent)

        Handler(Looper.getMainLooper()).postDelayed({
            RestartHelper.restartApp(context)
        }, 5000)
    }

}