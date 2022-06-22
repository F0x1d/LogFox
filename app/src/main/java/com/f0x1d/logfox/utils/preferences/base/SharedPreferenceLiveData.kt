package com.f0x1d.logfox.utils.preferences.base

import android.content.SharedPreferences
import androidx.lifecycle.LiveData

abstract class SharedPreferenceLiveData<T>(private val sharedPreferences: SharedPreferences,
                                           private val key: String,
                                           private val defValue: T): LiveData<T>(), SharedPreferences.OnSharedPreferenceChangeListener {

    abstract fun fromPreferences(key: String, defValue: T): T

    override fun onActive() {
        value = fromPreferences(key, defValue)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onInactive() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == this.key) {
            value = fromPreferences(key, defValue)
        }
    }
}