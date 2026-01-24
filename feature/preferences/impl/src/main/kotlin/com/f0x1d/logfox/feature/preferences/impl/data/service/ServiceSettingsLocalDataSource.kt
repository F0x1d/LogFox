package com.f0x1d.logfox.feature.preferences.impl.data.service

import com.fredporciuncula.flow.preferences.Preference

internal interface ServiceSettingsLocalDataSource {
    fun startOnBoot(): Preference<Boolean>
    fun includeDeviceInfoInArchives(): Preference<Boolean>
}
