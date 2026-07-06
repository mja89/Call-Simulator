package com.example.callsimulator

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class IncomingCallReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_CONTACT_ID = "contact_id"
        const val EXTRA_CONTACT_NAME = "contact_name"
        const val CHANNEL_ID = "incoming_call_channel"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val contactId = intent.getStringExtra(EXTRA_CONTACT_ID) ?: return
        val contactName = intent.getStringExtra(EXTRA_CONTACT_NAME) ?: "ناشناس"

        val fullScreenIntent = Intent(context, CallActivity::class.java).apply {
            putExtra(CallActivity.EXTRA_CONTACT_ID, contactId)
            putExtra(CallActivity.EXTRA_CONTACT_NAME, contactName)
            putExtra(CallActivity.EXTRA_CALL_TYPE, CallActivity.TYPE_INCOMING)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            contactId.hashCode(),
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "تماس‌های ورودی شبیه‌سازی‌شده",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "نمایش نوتیفیکیشن تمام‌صفحه برای تماس ورودی شبیه‌سازی‌شده"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.sym_call_incoming)
            .setContentTitle("تماس ورودی")
            .setContentText(contactName)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setContentIntent(fullScreenPendingIntent)
            .setAutoCancel(true)
            .setOngoing(true)
            .build()

        notificationManager.notify(contactId.hashCode(), notification)

        // همچنین سعی می‌کنیم مستقیم اکتیویتی را باز کنیم (روی برخی دستگاه‌ها وقتی برنامه در فورگراند است)
        try {
            context.startActivity(fullScreenIntent)
        } catch (e: Exception) {
            // اگر امکان لانچ مستقیم نبود، نوتیفیکیشن تمام‌صفحه جایگزین می‌شود
        }
    }
}
