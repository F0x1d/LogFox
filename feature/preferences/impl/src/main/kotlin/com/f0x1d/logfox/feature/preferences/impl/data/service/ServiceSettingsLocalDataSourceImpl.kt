package com.f0x1d.logfox.feature.preferences.impl.data.service

import android.content.Context
import com.f0x1d.logfox.feature.preferences.impl.base.BasePreferences
import com.fredporciuncula.flow.preferences.Preference
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ServiceSettingsLocalDataSourceImpl @Inject constructor(
    @ApplicationContext context: Context,
) : BasePreferences(context),
    ServiceSettingsLocalDataSource {

    override fun startOnBoot(): Preference<Boolean> = booleanPreference(
        key = KEY_START_ON_BOOT,
        defaultValue = true,
    )

    override fun showLogsFromAppLaunch(): Preference<Boolean> = booleanPreference(
        key = KEY_SHOW_LOGS_FROM_APP_LAUNCH,
        defaultValue = true,
    )

    override fun includeDeviceInfoInArchives(): Preference<Boolean> = booleanPreference(
        key = KEY_INCLUDE_DEVICE_INFO,
        defaultValue = true,
    )

    private companion object {
        const val KEY_START_ON_BOOT = "pref_start_on_boot"
        const val KEY_SHOW_LOGS_FROM_APP_LAUNCH = "pref_show_logs_from_app_launch"
        const val KEY_INCLUDE_DEVICE_INFO = "pref_include_device_info_in_archives"
    }
}
