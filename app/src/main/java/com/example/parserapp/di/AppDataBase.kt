package com.example.parserapp.di

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.parserapp.data.dao.LogMessageDao
import com.example.parserapp.data.dao.ProcessedMessageDao
import com.example.parserapp.data.dao.SenderDao
import com.example.parserapp.data.entities.LogMessage
import com.example.parserapp.data.entities.ProcessedMessageEntity
import com.example.parserapp.data.entities.SenderEntity

@Database(entities = [LogMessage::class, ProcessedMessageEntity::class, SenderEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun logMessageDao(): LogMessageDao
    abstract fun processedMessageDao(): ProcessedMessageDao
    abstract fun senderDao(): SenderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "parser_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}
