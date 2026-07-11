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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler = findViewById(R.id.recyclerContacts)
        recycler.layoutManager = LinearLayoutManager(this)

        // راه‌اندازی دکمه افزودن که در لایه تعریف کردیم
        findViewById<ImageButton>(R.id.btnAddContact).setOnClickListener {
            startActivity(Intent(this, AddContactActivity::class.java))
        }

        loadContactsFromDatabase()
    }

    private fun loadContactsFromDatabase() {
        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "call-simulator-db").build()

        lifecycleScope.launch {
            // خواندن زنده داده‌ها از دیتابیس
            db.contactDao().getAllContacts().collect { contactList ->
                // در مرحله بعد باید آداپتر مخصوص خودت را اینجا مقداردهی کنی
                // فعلاً لیست از دیتابیس دریافت می‌شود
            }
        }
    }
}
