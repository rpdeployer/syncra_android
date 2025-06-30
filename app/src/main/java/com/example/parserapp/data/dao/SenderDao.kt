package com.example.parserapp.data.dao

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.parserapp.data.entities.SenderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SenderDao {

    @Query("DELETE FROM senders")
    suspend fun clearSenders()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSenders(senders: List<SenderEntity>)

    @Query("SELECT * FROM senders")
    fun getAllSenders(): Flow<List<SenderEntity>>
}
