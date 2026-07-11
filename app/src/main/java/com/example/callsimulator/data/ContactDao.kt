package com.example.callsimulator.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Insert
    suspend fun insertContact(contact: ContactEntity): Long

    @Query("SELECT * FROM contacts")
    fun getAllContacts(): Flow<List<ContactEntity>>

    @Query("DELETE FROM contacts WHERE id = :contactId")
    suspend fun deleteContact(contactId: Int)
}

