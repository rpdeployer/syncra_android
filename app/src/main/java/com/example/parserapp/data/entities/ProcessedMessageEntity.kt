package com.example.parserapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "processed_messages")
data class ProcessedMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String,
    val message: String,
    val timestamp: Long
)