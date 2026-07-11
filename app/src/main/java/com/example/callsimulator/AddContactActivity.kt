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
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.launch
import java.io.File

class AddContactActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null

    // لانچر برای دریافت نتیجه از کراپ
    private val cropImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val resultUri = UCrop.getOutput(result.data!!)
            resultUri?.let {
                selectedImageUri = it
                findViewById<ImageView>(R.id.ivProfile).setImageURI(it)
            }
        }
    }

    // لانچر برای انتخاب عکس از گالری
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val destinationUri = Uri.fromFile(File(cacheDir, "cropped_image_${System.currentTimeMillis()}.jpg"))
            val uCropIntent = UCrop.of(it, destinationUri)
                .withAspectRatio(1f, 1f) // مربع بودن تصویر
                .withMaxResultSize(500, 500)
                .getIntent(this)
            cropImage.launch(uCropIntent)
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

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            val name = findViewById<EditText>(R.id.etName).text.toString()
            val phone = findViewById<EditText>(R.id.etPhoneNumber).text.toString()

            if (name.isNotEmpty() && phone.isNotEmpty()) {
                lifecycleScope.launch {
                    val imagePath = selectedImageUri?.let { saveImageToInternalStorage(it) }
                    val contact = ContactEntity(
                        name = name,
                        phoneNumber = phone,
                        profileImageUri = imagePath,
                        audioPath = null // فعلاً خالی است
                    )
                    db.contactDao().insertContact(contact)
                    finish()
                }
            } else {
                Toast.makeText(this, "لطفاً نام و شماره را وارد کنید", Toast.LENGTH_SHORT).show()
            }
        }
    }

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
