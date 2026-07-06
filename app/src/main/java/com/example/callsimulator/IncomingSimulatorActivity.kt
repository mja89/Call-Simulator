package com.example.callsimulator

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.callsimulator.model.Contact

class IncomingSimulatorActivity : AppCompatActivity() {

    private lateinit var contacts: List<Contact>
    private lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_simulator)

        contacts = ContactsRepository.loadContacts(this)
        spinner = findViewById(R.id.spinnerContact)
        spinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            contacts.map { it.name }
        )

        val radioGroup = findViewById<android.widget.RadioGroup>(R.id.radioGroupMode)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            spinner.isEnabled = checkedId == R.id.radioSelected
        }

        findViewById<android.widget.Button>(R.id.btnStart).setOnClickListener {
            startSimulation(radioGroup.checkedRadioButtonId == R.id.radioRandom)
        }
    }

    private fun startSimulation(random: Boolean) {
        val chosenContact: Contact? = if (random) {
            val idsWithVoice = VoiceStorage.allContactIdsWithVoices(this)
            val candidates = contacts.filter { idsWithVoice.contains(it.id) }
            if (candidates.isEmpty()) {
                Toast.makeText(this, "هیچ مخاطبی با وویس تنظیم‌شده وجود ندارد", Toast.LENGTH_LONG).show()
                return
            }
            candidates.random()
        } else {
            if (contacts.isEmpty()) {
                Toast.makeText(this, "مخاطبی یافت نشد", Toast.LENGTH_SHORT).show()
                return
            }
            contacts[spinner.selectedItemPosition]
        }

        val delaySeconds = findViewById<android.widget.EditText>(R.id.editDelay)
            .text.toString().toIntOrNull() ?: 5

        val intent = Intent(this, IncomingCallReceiver::class.java).apply {
            putExtra(IncomingCallReceiver.EXTRA_CONTACT_ID, chosenContact!!.id)
            putExtra(IncomingCallReceiver.EXTRA_CONTACT_NAME, chosenContact.name)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            chosenContact!!.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAt = SystemClock.elapsedRealtime() + delaySeconds * 1000L
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            // بدون مجوز alarm دقیق، از نوع غیر دقیق استفاده می‌کنیم
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pendingIntent)
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pendingIntent)
        }

        Toast.makeText(
            this,
            "تماس ورودی شبیه‌سازی‌شده از ${chosenContact.name} تا $delaySeconds ثانیه دیگر",
            Toast.LENGTH_LONG
        ).show()
        finish()
    }
}
