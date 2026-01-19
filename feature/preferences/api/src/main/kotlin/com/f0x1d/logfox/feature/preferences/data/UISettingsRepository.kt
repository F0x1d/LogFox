package com.f0x1d.logfox.feature.preferences.data

import com.f0x1d.logfox.core.preferences.PreferenceStateFlow

interface UISettingsRepository {
    fun nightTheme(): PreferenceStateFlow<Int>
    fun monetEnabled(): PreferenceStateFlow<Boolean>
}
