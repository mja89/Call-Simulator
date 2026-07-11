package com.example.callsimulator.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "voice_records")
data class VoiceRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val contactOwnerId: Int,
    val filePath: String
)
