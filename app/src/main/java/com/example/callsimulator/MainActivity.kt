package com.example.callsimulator

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.callsimulator.model.Contact

class MainActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private val REQUEST_CODE_PERMISSIONS = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler = findViewById(R.id.recyclerContacts)
        recycler.layoutManager = LinearLayoutManager(this)

        findViewById<android.widget.Button>(R.id.btnIncomingSimulator).setOnClickListener {
            startActivity(Intent(this, IncomingSimulatorActivity::class.java))
        }

        checkPermissionsAndLoad()
    }

    override fun onResume() {
        super.onResume()
        if (hasContactsPermission()) loadContacts()
    }

    private fun hasContactsPermission(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) ==
            PackageManager.PERMISSION_GRANTED

    private fun checkPermissionsAndLoad() {
        val perms = mutableListOf(Manifest.permission.READ_CONTACTS)
        if (Build.VERSION.SDK_INT >= 33) perms.add(Manifest.permission.POST_NOTIFICATIONS)

        val missing = perms.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missing.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missing.toTypedArray(), REQUEST_CODE_PERMISSIONS)
        } else {
            loadContacts()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS && hasContactsPermission()) {
            loadContacts()
        }
    }

    private fun loadContacts() {
        val contacts: List<Contact> = ContactsRepository.loadContacts(this)
        recycler.adapter = ContactAdapter(
            contacts,
            onCallClick = { contact -> startOutgoingCall(contact) },
            onConfigClick = { contact -> openConfig(contact) }
        )
    }

    private fun startOutgoingCall(contact: Contact) {
        val intent = Intent(this, CallActivity::class.java).apply {
            putExtra(CallActivity.EXTRA_CONTACT_ID, contact.id)
            putExtra(CallActivity.EXTRA_CONTACT_NAME, contact.name)
            putExtra(CallActivity.EXTRA_CALL_TYPE, CallActivity.TYPE_OUTGOING)
        }
        startActivity(intent)
    }

    private fun openConfig(contact: Contact) {
        val intent = Intent(this, ContactConfigActivity::class.java).apply {
            putExtra(ContactConfigActivity.EXTRA_CONTACT_ID, contact.id)
            putExtra(ContactConfigActivity.EXTRA_CONTACT_NAME, contact.name)
        }
        startActivity(intent)
    }
}
