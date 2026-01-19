package com.f0x1d.logfox.feature.preferences.data

interface ServiceSettingsRepository {
    var startOnBoot: Boolean
    var showLogsFromAppLaunch: Boolean
    var includeDeviceInfoInArchives: Boolean
}
