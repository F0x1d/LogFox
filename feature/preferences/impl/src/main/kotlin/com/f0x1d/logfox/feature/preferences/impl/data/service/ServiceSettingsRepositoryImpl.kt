package com.f0x1d.logfox.feature.preferences.impl.data.service

import com.f0x1d.logfox.core.preferences.PreferenceStateFlow
import com.f0x1d.logfox.core.preferences.asPreferenceStateFlow
import com.f0x1d.logfox.feature.preferences.data.ServiceSettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ServiceSettingsRepositoryImpl @Inject constructor(
    private val localDataSource: ServiceSettingsLocalDataSource,
) : ServiceSettingsRepository {

    override fun startOnBoot(): PreferenceStateFlow<Boolean> = localDataSource.startOnBoot().asPreferenceStateFlow()

    override fun includeDeviceInfoInArchives(): PreferenceStateFlow<Boolean> = localDataSource.includeDeviceInfoInArchives().asPreferenceStateFlow()
}
