package com.example.parserapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.parserapp.data.entities.ProcessedMessageEntity

@Dao
interface ProcessedMessageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMessage(message: ProcessedMessageEntity)

    @Query("""
        SELECT COUNT(*) FROM processed_messages 
        WHERE sender = :sender 
        AND message = :message 
        AND ABS(:timestamp - timestamp) <= 3000
    """)
    suspend fun isMessageProcessed(sender: String, message: String, timestamp: Long): Int

    @Query("DELETE FROM processed_messages WHERE timestamp < :expiryTime")
    suspend fun deleteOldMessages(expiryTime: Long)
}