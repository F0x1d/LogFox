package com.f0x1d.logfox.feature.preferences.impl.base

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.fredporciuncula.flow.preferences.Preference

internal abstract class BasePreferences(context: Context) {
    protected val sharedPreferences: SharedPreferences by lazy { providePreferences(context) }
    protected val flowSharedPreferences: FlowSharedPreferences by lazy {
        FlowSharedPreferences(sharedPreferences)
    }

    protected open fun providePreferences(context: Context): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    protected fun booleanPreference(key: String, defaultValue: Boolean): Preference<Boolean> = flowSharedPreferences.getBoolean(key, defaultValue)

    protected fun intPreference(key: String, defaultValue: Int): Preference<Int> = flowSharedPreferences.getInt(key, defaultValue)

    protected fun longPreference(key: String, defaultValue: Long): Preference<Long> = flowSharedPreferences.getLong(key, defaultValue)

    protected fun stringPreference(key: String, defaultValue: String): Preference<String> = flowSharedPreferences.getString(key, defaultValue)

    protected inline fun <reified T : Enum<T>> enumPreference(
        key: String,
        defaultValue: T,
    ): Preference<T> = flowSharedPreferences.getEnum(key, defaultValue)
}
