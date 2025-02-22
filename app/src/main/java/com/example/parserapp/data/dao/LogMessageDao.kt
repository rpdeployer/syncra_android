package com.example.parserapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.parserapp.data.entities.LogMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface LogMessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogMessage(logMessage: LogMessage)

    @Query("SELECT * FROM logs_ui ORDER BY id DESC")
    fun getLogMessages(): Flow<List<LogMessage>>

    @Query("DELETE FROM logs_ui")
    suspend fun clearLogs()

}