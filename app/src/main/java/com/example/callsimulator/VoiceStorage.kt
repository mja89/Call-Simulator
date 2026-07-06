package com.example.callsimulator

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * ذخیره نگاشت contactId -> لیست URI فایل‌های صوتی (وویس‌های پیش‌فرض آن مخاطب)
 * با SharedPreferences به صورت JSON ذخیره می‌شود.
 */
object VoiceStorage {

    private const val PREFS = "voice_prefs"
    private const val KEY_MAP = "contact_voice_map"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun loadRoot(context: Context): JSONObject {
        val raw = prefs(context).getString(KEY_MAP, null) ?: return JSONObject()
        return try { JSONObject(raw) } catch (e: Exception) { JSONObject() }
    }

    private fun saveRoot(context: Context, root: JSONObject) {
        prefs(context).edit().putString(KEY_MAP, root.toString()).apply()
    }

    fun getVoicesFor(context: Context, contactId: String): List<String> {
        val root = loadRoot(context)
        val arr = root.optJSONArray(contactId) ?: return emptyList()
        return (0 until arr.length()).map { arr.getString(it) }
    }

    fun setVoicesFor(context: Context, contactId: String, uris: List<String>) {
        val root = loadRoot(context)
        val arr = JSONArray()
        uris.forEach { arr.put(it) }
        root.put(contactId, arr)
        saveRoot(context, root)
    }

    fun hasVoices(context: Context, contactId: String): Boolean =
        getVoicesFor(context, contactId).isNotEmpty()

    /** یک وویس را به‌صورت تصادفی از میان وویس‌های تنظیم‌شده‌ی مخاطب برمی‌گرداند */
    fun pickRandomVoice(context: Context, contactId: String): String? {
        val list = getVoicesFor(context, contactId)
        if (list.isEmpty()) return null
        return list[(list.indices).random()]
    }

    fun allContactIdsWithVoices(context: Context): List<String> {
        val root = loadRoot(context)
        return root.keys().asSequence().filter { root.optJSONArray(it)?.length() ?: 0 > 0 }.toList()
    }
}
