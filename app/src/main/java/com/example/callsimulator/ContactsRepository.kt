package com.example.callsimulator

import android.content.Context
import android.provider.ContactsContract
import com.example.callsimulator.model.Contact

object ContactsRepository {

    fun loadContacts(context: Context): List<Contact> {
        val result = mutableListOf<Contact>()
        val seenIds = mutableSetOf<String>()

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI
        )

        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
        )

        cursor?.use {
            val idIdx = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameIdx = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIdx = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val photoIdx = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)

            while (it.moveToNext()) {
                val id = it.getString(idIdx) ?: continue
                if (!seenIds.add(id)) continue // جلوگیری از تکرار مخاطب با چند شماره
                val name = it.getString(nameIdx) ?: "بدون نام"
                val number = it.getString(numberIdx) ?: ""
                val photo = it.getString(photoIdx)
                result.add(Contact(id, name, number, photo))
            }
        }
        return result
    }

    fun findById(context: Context, contactId: String): Contact? =
        loadContacts(context).find { it.id == contactId }
}
