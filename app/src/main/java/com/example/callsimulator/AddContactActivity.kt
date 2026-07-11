package com.example.callsimulator

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.callsimulator.data.AppDatabase
import com.example.callsimulator.data.ContactEntity
import kotlinx.coroutines.launch

class AddContactActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "call-simulator-db").build()

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            val name = findViewById<EditText>(R.id.etName).text.toString()
            val phone = findViewById<EditText>(R.id.etPhoneNumber).text.toString()

            if (name.isNotEmpty() && phone.isNotEmpty()) {
                lifecycleScope.launch {
                    val contact = ContactEntity(name = name, phoneNumber = phone, profileImageUri = null)
                    db.contactDao().insertContact(contact)
                    finish()
                }
            } else {
                Toast.makeText(this, "لطفاً نام و شماره را وارد کنید", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

