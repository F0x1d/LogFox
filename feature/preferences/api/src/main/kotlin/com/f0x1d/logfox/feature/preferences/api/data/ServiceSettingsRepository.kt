package com.f0x1d.logfox.feature.preferences.api.data

import com.f0x1d.logfox.core.preferences.api.PreferenceStateFlow

interface ServiceSettingsRepository {
    fun startOnBoot(): PreferenceStateFlow<Boolean>
    fun showLogsFromAppLaunch(): PreferenceStateFlow<Boolean>
    fun includeDeviceInfoInArchives(): PreferenceStateFlow<Boolean>
    fun includeAppInfoInExports(): PreferenceStateFlow<Boolean>
    fun stopLoggingOnBackExit(): PreferenceStateFlow<Boolean>
    fun exportLogsAsTxt(): PreferenceStateFlow<Boolean>
}
