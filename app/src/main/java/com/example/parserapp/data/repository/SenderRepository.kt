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

        val senderEntities = response.senderNames.map { SenderEntity(it) }
        senderDao.insertSenders(senderEntities)
        Log.d("SenderUpdate", "Обновлен список отправителей: ${response.senderNames}")
    }

    fun getSenders(): Flow<List<SenderEntity>> = senderDao.getAllSenders()
}