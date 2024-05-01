package com.f0x1d.logfox.utils.preferences.base

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

abstract class BasePreferences(context: Context) {

    val sharedPreferences by lazy { providePreferences(context) }

    abstract fun providePreferences(context: Context): SharedPreferences

    inline fun <reified T> asLiveData(key: String, defValue: T?) = object : SharedPreferenceLiveData<T?>(sharedPreferences, key, defValue) {
        override fun fromPreferences(key: String, defValue: T?) = try {
            get(key, defValue)
        } catch (e: RuntimeException) {
            getNullable(key, defValue)
        }
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> put(key: String, value: T?) = sharedPreferences.edit {
        when (value) {
            null -> putString(key, null)

            is String -> putString(key, value)
            is Boolean -> putBoolean(key, value)
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is Float -> putFloat(key, value)
            is Set<*> -> putStringSet(key, value as Set<String>)
        }
    }

    inline fun <reified T> get(key: String, defaultValue: T): T = when (defaultValue) {
        is Boolean -> sharedPreferences.getBoolean(key, defaultValue) as T
        is Int -> sharedPreferences.getInt(key, defaultValue) as T
        is Long -> sharedPreferences.getLong(key, defaultValue) as T
        is Float -> sharedPreferences.getFloat(key, defaultValue) as T

        else -> error("Type of $defaultValue is not supported")
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> getNullable(key: String, defaultValue: T?): T? = when (defaultValue) {
        null -> sharedPreferences.getString(key, defaultValue) as T?

        is String -> sharedPreferences.getString(key, defaultValue) as T?
        is Set<*> -> sharedPreferences.getStringSet(key, defaultValue as Set<String>) as T?

        else -> error("Type of $defaultValue is not supported")
    }

    fun registerListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) =
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    fun unregisterListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) =
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
}
