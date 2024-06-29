package com.f0x1d.logfox.preferences.shared.base

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.fredporciuncula.flow.preferences.FlowSharedPreferences

abstract class BasePreferences(context: Context) {

    protected val sharedPreferences by lazy { providePreferences(context) }
    protected val flowSharedPreferences by lazy { FlowSharedPreferences(sharedPreferences) }

    abstract fun providePreferences(context: Context): SharedPreferences

    @Suppress("UNCHECKED_CAST")
    protected inline fun <reified T> put(key: String, value: T?) = sharedPreferences.edit {
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

    protected inline fun <reified T> get(key: String, defaultValue: T): T = when (defaultValue) {
        is Boolean -> sharedPreferences.getBoolean(key, defaultValue) as T
        is Int -> sharedPreferences.getInt(key, defaultValue) as T
        is Long -> sharedPreferences.getLong(key, defaultValue) as T
        is Float -> sharedPreferences.getFloat(key, defaultValue) as T

        else -> error("Type of $defaultValue is not supported")
    }

    @Suppress("UNCHECKED_CAST")
    protected inline fun <reified T> getNullable(key: String, defaultValue: T?): T? = when (defaultValue) {
        null -> sharedPreferences.getString(key, defaultValue) as T?

        is String -> sharedPreferences.getString(key, defaultValue) as T?
        is Set<*> -> sharedPreferences.getStringSet(key, defaultValue as Set<String>) as T?

        else -> error("Type of $defaultValue is not supported")
    }
}
