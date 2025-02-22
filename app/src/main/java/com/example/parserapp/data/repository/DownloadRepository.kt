package com.example.parserapp.data.repository

import android.content.Context
import android.os.Environment
import android.util.Log
import com.example.parserapp.BuildConfig
import com.example.parserapp.data.network.DownloadService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class DownloadRepository(context: Context) {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val downloadService = retrofit.create(DownloadService::class.java)

    private val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)

    suspend fun downloadApkFile(url: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val responseBody = downloadService.downloadFile(url)
                saveFile(responseBody, "update.apk")
            } catch (e: Exception) {
                Log.e("DownloadRepository", "Ошибка загрузки: ${e.message}")
                false
            }
        }
    }

    private fun saveFile(body: ResponseBody, fileName: String): Boolean {
        val file = File(downloadsDir, fileName)
        return try {
            val inputStream: InputStream = body.byteStream()
            val outputStream = FileOutputStream(file)

            val buffer = ByteArray(4096)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            outputStream.flush()
            inputStream.close()
            outputStream.close()

            Log.d("DownloadRepository", "Файл сохранён: ${file.absolutePath}")
            true
        } catch (e: Exception) {
            Log.e("DownloadRepository", "Ошибка сохранения файла: ${e.message}")
            false
        }
    }
}