package com.f0x1d.logfox.utils.preferences

import android.content.Context
import androidx.preference.PreferenceManager
import com.f0x1d.logfox.utils.preferences.base.BasePreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppPreferences @Inject constructor(@ApplicationContext context: Context): BasePreferences(context) {

    var startOnBoot
        get() = get("pref_start_on_boot", true)
        set(value) { put("pref_start_on_boot", value) }

    override fun providePreferences(context: Context) = PreferenceManager.getDefaultSharedPreferences(context)
}