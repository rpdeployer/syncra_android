package com.example.parserapp.data.repository

import android.util.Log
import com.example.parserapp.data.dao.SenderDao
import com.example.parserapp.data.entities.SenderEntity
import com.example.parserapp.data.model.StatusResponse
import kotlinx.coroutines.flow.Flow

class SenderRepository(private val senderDao: SenderDao) {

    suspend fun updateSenders(response: StatusResponse) {
        senderDao.clearSenders()

        if (response.senderNames.isNullOrEmpty()) {
            Log.d("SenderUpdate", "Список пуст, БД очищена.")
            return
        }

        val senderEntities: List<SenderEntity> = response.senderNames.mapNotNull { raw ->
            val name = String(
                raw.replace("'", "")
                    .replace(Regex("[\\u0000-\\u001F\\u007F-\\u009F\\u200B\\uFEFF\\u00A0]+"), "")
                    .trim()
                    .toCharArray()
            )

            if (name.isNotEmpty()) SenderEntity(name) else null
        }

        senderDao.insertSenders(senderEntities)
        Log.d("SenderUpdate", "Обновлен список отправителей: $senderEntities")
    }


    fun getSenders(): Flow<List<SenderEntity>> = senderDao.getAllSenders()
}