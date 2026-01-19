package com.f0x1d.logfox.feature.preferences.impl.data.service

import android.content.Context
import com.f0x1d.logfox.feature.preferences.impl.base.BasePreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ServiceSettingsLocalDataSourceImpl
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) : BasePreferences(context),
        ServiceSettingsLocalDataSource {
        override var startOnBoot
            get() = get(KEY_START_ON_BOOT, true)
            set(value) = put(KEY_START_ON_BOOT, value)

        override var showLogsFromAppLaunch
            get() = get(KEY_SHOW_LOGS_FROM_APP_LAUNCH, true)
            set(value) = put(KEY_SHOW_LOGS_FROM_APP_LAUNCH, value)

        override var includeDeviceInfoInArchives
            get() = get(KEY_INCLUDE_DEVICE_INFO, true)
            set(value) = put(KEY_INCLUDE_DEVICE_INFO, value)

        private companion object {
            const val KEY_START_ON_BOOT = "pref_start_on_boot"
            const val KEY_SHOW_LOGS_FROM_APP_LAUNCH = "pref_show_logs_from_app_launch"
            const val KEY_INCLUDE_DEVICE_INFO = "pref_include_device_info_in_archives"
        }
    }
