package com.example.callsimulator

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ContactConfigActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CONTACT_ID = "contact_id"
        const val EXTRA_CONTACT_NAME = "contact_name"
    }

    private lateinit var contactId: String
    private val voiceUris = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var listVoices: ListView

    private val pickAudioLauncher =
        registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val clipData = data?.clipData
                if (clipData != null) {
                    for (i in 0 until clipData.itemCount) {
                        addUri(clipData.getItemAt(i).uri)
                    }
                } else {
                    data?.data?.let { addUri(it) }
                }
                refreshList()
            }
        }

    private fun addUri(uri: Uri) {
        try {
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (e: Exception) {
            // برخی providerها اجازه persistable نمی‌دهند؛ همچنان اضافه می‌کنیم
        }
        voiceUris.add(uri.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_config)

        contactId = intent.getStringExtra(EXTRA_CONTACT_ID) ?: return finish()
        val contactName = intent.getStringExtra(EXTRA_CONTACT_NAME) ?: ""

        findViewById<android.widget.TextView>(R.id.txtContactName).text = contactName
        listVoices = findViewById(R.id.listVoices)

        voiceUris.addAll(VoiceStorage.getVoicesFor(this, contactId))
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayNames())
        listVoices.adapter = adapter

        listVoices.setOnItemLongClickListener { _, _, position, _ ->
            voiceUris.removeAt(position)
            refreshList()
            Toast.makeText(this, "حذف شد", Toast.LENGTH_SHORT).show()
            true
        }

        findViewById<android.widget.Button>(R.id.btnAddVoice).setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "audio/*"
                addCategory(Intent.CATEGORY_OPENABLE)
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            pickAudioLauncher.launch(intent)
        }

        findViewById<android.widget.Button>(R.id.btnSave).setOnClickListener {
            VoiceStorage.setVoicesFor(this, contactId, voiceUris)
            Toast.makeText(this, "ذخیره شد", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun displayNames(): List<String> =
        voiceUris.mapIndexed { index, uri -> "وویس ${index + 1}: ${Uri.parse(uri).lastPathSegment}" }

    private fun refreshList() {
        adapter.clear()
        adapter.addAll(displayNames())
        adapter.notifyDataSetChanged()
    }
}
