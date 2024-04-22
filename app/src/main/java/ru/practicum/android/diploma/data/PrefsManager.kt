package ru.practicum.android.diploma.data

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson

class PrefsManager(private val sharedPrefs: SharedPreferences) {

    private val gson = Gson()

    fun save(key: String, value: Any) {
        sharedPrefs.edit().putString(key, gson.toJson(value)).apply()
    }

    fun <T : Any> get(key: String, clazz: Class<T>): T? {
        val res = gson.fromJson(sharedPrefs.getString(key, ""), clazz)
        Log.d("AAA", "Prefs $res")
        return res
    }

    fun delete(key: String) {
        sharedPrefs.edit().remove(key).apply()
    }
}
