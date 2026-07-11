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

    // متد جدید برای دریافت یک مخاطب خاص جهت ویرایش
    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContactById(id: Int): ContactEntity

    // متد جدید برای ذخیره تغییرات (ویرایش)
    @Update
    suspend fun update(contact: ContactEntity)
}
