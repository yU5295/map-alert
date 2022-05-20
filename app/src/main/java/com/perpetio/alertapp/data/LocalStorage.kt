package com.perpetio.alertapp.data

import android.content.Context
import com.google.gson.Gson

class LocalStorage(
    context: Context,
    private val gson: Gson
) {
    private val prefs = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE)

    fun clear() {
        prefs.edit().clear().apply()
    }

    var repeatInterval: Int?
        get() {
            val interval = prefs.getInt(REPEAT_INTERVAL, NULL_INT)
            return if (interval == NULL_INT) null else interval
        }
        set(value) {
            prefs.edit().putInt(
                REPEAT_INTERVAL, value ?: NULL_INT
            ).apply()
        }

    companion object {
        private const val STORAGE_NAME = "AlertAppStorage"
        private const val REPEAT_INTERVAL = "repeat_interval"

        private const val NULL_INT = -1
    }
}