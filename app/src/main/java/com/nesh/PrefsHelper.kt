package com.nesh

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import kotlin.reflect.KProperty

class PrefsHelper(context: Context) {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var workDirectory: Uri? by PreferenceDelegate(
        sharedPreferences,
        get = { it.getString(KEY_WORK_DIRECTORY, null)?.let { uri -> Uri.parse(uri) } },
        set = { editor, value -> editor.putString(KEY_WORK_DIRECTORY, value?.toString()) }
    )

    class PreferenceDelegate<T>(
        private val preferences: SharedPreferences,
        private val get: (SharedPreferences) -> T,
        private val set: (SharedPreferences.Editor, value: T) -> Unit
    ) {

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return get(preferences)
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            preferences.edit { set(this, value) }
        }
    }

    companion object {
        private const val KEY_WORK_DIRECTORY = "KEY_WORK_DIRECTORY"
    }
}
