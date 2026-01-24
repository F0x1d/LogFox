package com.f0x1d.logfox.feature.preferences.data

import com.f0x1d.logfox.core.preferences.PreferenceStateFlow

interface ServiceSettingsRepository {
    fun startOnBoot(): PreferenceStateFlow<Boolean>
    fun includeDeviceInfoInArchives(): PreferenceStateFlow<Boolean>
}
