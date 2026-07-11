package com.example.callsimulator

import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.callsimulator.data.AppDatabase
import com.example.callsimulator.data.ContactEntity
import kotlinx.coroutines.launch
import java.io.File

class AddContactActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            findViewById<ImageView>(R.id.ivProfile).setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "call-simulator-db")
            .fallbackToDestructiveMigration()
            .build()

        findViewById<ImageView>(R.id.ivProfile).setOnClickListener {
            pickImage.launch("image/*")
        }

        // دکمه ذخیره مخاطب
        findViewById<Button>(R.id.btnSave).setOnClickListener {
            val name = findViewById<EditText>(R.id.etName).text.toString()
            val phone = findViewById<EditText>(R.id.etPhoneNumber).text.toString()

            if (name.isNotEmpty() && phone.isNotEmpty()) {
                // استفاده از lifecycleScope برای عملیات دیتابیس در پس‌زمینه
                lifecycleScope.launch {
                    // ۱. کپی عکس در حافظه دائمی گوشی
                    val imagePath = selectedImageUri?.let { saveImageToInternalStorage(it) }
                    
                    // ۲. ساخت موجودیت مخاطب
                    val contact = ContactEntity(
                        name = name,
                        phoneNumber = phone,
                        profileImageUri = imagePath
                    )
                    
                    // ۳. ذخیره در دیتابیس و بستن صفحه
                    db.contactDao().insertContact(contact)
                    finish()
                }
            } else {
                Toast.makeText(this, "لطفاً نام و شماره را وارد کنید", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // متد کمکی برای ذخیره عکس در حافظه داخلی برنامه
    private fun saveImageToInternalStorage(uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri)
        val file = File(filesDir, "contact_${System.currentTimeMillis()}.jpg")
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file.absolutePath
    }
}
