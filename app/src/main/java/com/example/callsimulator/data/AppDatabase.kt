package com.example.callsimulator.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ContactEntity::class, VoiceRecordEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
}

