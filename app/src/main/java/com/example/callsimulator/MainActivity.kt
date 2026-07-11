package com.example.callsimulator

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.callsimulator.data.AppDatabase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler = findViewById(R.id.recyclerContacts)
        recycler.layoutManager = LinearLayoutManager(this)
        
        // مقداردهی اولیه آداپتر با لیست خالی
        adapter = ContactAdapter(emptyList())
        recycler.adapter = adapter

        // دکمه افزودن مخاطب
        findViewById<ImageButton>(R.id.btnAddContact).setOnClickListener {
            startActivity(Intent(this, AddContactActivity::class.java))
        }

        loadContactsFromDatabase()
    }

    private fun loadContactsFromDatabase() {
        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "call-simulator-db").build()

        lifecycleScope.launch {
            db.contactDao().getAllContacts().collect { contactList ->
                adapter.updateList(contactList)
            }
        }
    }
}
