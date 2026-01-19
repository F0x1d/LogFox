package com.f0x1d.logfox.feature.preferences.impl.data.service

internal interface ServiceSettingsLocalDataSource {
    var startOnBoot: Boolean
    var showLogsFromAppLaunch: Boolean
    var includeDeviceInfoInArchives: Boolean
}
