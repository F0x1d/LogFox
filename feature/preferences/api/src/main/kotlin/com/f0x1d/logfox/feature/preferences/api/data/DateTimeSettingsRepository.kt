package com.f0x1d.logfox.feature.preferences.api.data

import com.f0x1d.logfox.core.preferences.api.PreferenceStateFlow

interface DateTimeSettingsRepository {
    companion object {
        const val DATE_FORMAT_DEFAULT = "dd.MM"
        const val TIME_FORMAT_DEFAULT = "HH:mm:ss.SSS"
    }

    fun dateFormat(): PreferenceStateFlow<String>
    fun timeFormat(): PreferenceStateFlow<String>
}
