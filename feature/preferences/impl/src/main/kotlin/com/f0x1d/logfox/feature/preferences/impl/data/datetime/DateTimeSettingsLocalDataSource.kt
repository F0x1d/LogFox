package com.f0x1d.logfox.feature.preferences.impl.data.datetime

import com.fredporciuncula.flow.preferences.Preference

internal interface DateTimeSettingsLocalDataSource {
    fun dateFormat(): Preference<String>
    fun timeFormat(): Preference<String>
}
