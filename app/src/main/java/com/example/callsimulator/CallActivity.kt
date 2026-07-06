package com.example.callsimulator

import android.app.NotificationManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CallActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CONTACT_ID = "contact_id"
        const val EXTRA_CONTACT_NAME = "contact_name"
        const val EXTRA_CALL_TYPE = "call_type"
        const val TYPE_OUTGOING = "outgoing"
        const val TYPE_INCOMING = "incoming"
    }

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var contactId: String
    private lateinit var contactName: String
    private lateinit var callType: String

    private lateinit var txtStatus: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var secondsElapsed = 0
    private var timerRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showOverLockScreen()
        setContentView(R.layout.activity_call)

        contactId = intent.getStringExtra(EXTRA_CONTACT_ID) ?: ""
        contactName = intent.getStringExtra(EXTRA_CONTACT_NAME) ?: "ناشناس"
        callType = intent.getStringExtra(EXTRA_CALL_TYPE) ?: TYPE_OUTGOING

        findViewById<TextView>(R.id.txtCallerName).text = contactName
        txtStatus = findViewById(R.id.txtCallStatus)

        val layoutIncomingButtons = findViewById<android.widget.LinearLayout>(R.id.layoutIncomingButtons)
        val btnEndCall = findViewById<Button>(R.id.btnEndCall)
        val btnAccept = findViewById<Button>(R.id.btnAccept)
        val btnDecline = findViewById<Button>(R.id.btnDecline)

        btnEndCall.setOnClickListener { endCall() }
        btnDecline.setOnClickListener { endCall() }
        btnAccept.setOnClickListener {
            layoutIncomingButtons.visibility = android.view.View.GONE
            btnEndCall.visibility = android.view.View.VISIBLE
            connectCall()
        }

        when (callType) {
            TYPE_INCOMING -> {
                txtStatus.text = "در حال تماس..."
                layoutIncomingButtons.visibility = android.view.View.VISIBLE
                btnEndCall.visibility = android.view.View.GONE
            }
            else -> {
                txtStatus.text = "در حال برقراری تماس..."
                layoutIncomingButtons.visibility = android.view.View.GONE
                btnEndCall.visibility = android.view.View.VISIBLE
                handler.postDelayed({ connectCall() }, 1500)
            }
        }
    }

    private fun showOverLockScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }
    }

    private fun connectCall() {
        txtStatus.text = "00:00"
        startVoicePlayback()
        startTimer()
        // پاک کردن نوتیفیکیشن تماس ورودی در صورت وجود
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(contactId.hashCode())
    }

    private fun startVoicePlayback() {
        val voiceUri = VoiceStorage.pickRandomVoice(this, contactId)
        if (voiceUri == null) {
            Toast.makeText(this, "برای این مخاطب وویسی تنظیم نشده است", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(this@CallActivity, Uri.parse(voiceUri))
                isLooping = true
                setOnPreparedListener { start() }
                prepareAsync()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "خطا در پخش فایل صوتی", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTimer() {
        timerRunnable = object : Runnable {
            override fun run() {
                secondsElapsed++
                val m = secondsElapsed / 60
                val s = secondsElapsed % 60
                txtStatus.text = String.format("%02d:%02d", m, s)
                handler.postDelayed(this, 1000)
            }
        }
        handler.postDelayed(timerRunnable!!, 1000)
    }

    private fun endCall() {
        timerRunnable?.let { handler.removeCallbacks(it) }
        mediaPlayer?.let {
            try { it.stop() } catch (e: Exception) {}
            it.release()
        }
        mediaPlayer = null
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(contactId.hashCode())
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        timerRunnable?.let { handler.removeCallbacks(it) }
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onBackPressed() {
        endCall()
    }
}
