package com.example.parserapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "senders")
data class SenderEntity(
    @PrimaryKey val name: String
)