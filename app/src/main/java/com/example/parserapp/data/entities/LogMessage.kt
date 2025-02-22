package com.example.parserapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "logs_ui")
data class LogMessage (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val message: String,
    val dateTime: String,
    val isSuccess: Boolean
)