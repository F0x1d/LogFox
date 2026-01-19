package com.f0x1d.logfox.feature.preferences.impl.data.service

import com.f0x1d.logfox.feature.preferences.data.ServiceSettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ServiceSettingsRepositoryImpl
    @Inject
    constructor(
        private val localDataSource: ServiceSettingsLocalDataSource,
    ) : ServiceSettingsRepository {
        override var startOnBoot: Boolean
            get() = localDataSource.startOnBoot
            set(value) {
                localDataSource.startOnBoot = value
            }

        override var showLogsFromAppLaunch: Boolean
            get() = localDataSource.showLogsFromAppLaunch
            set(value) {
                localDataSource.showLogsFromAppLaunch = value
            }

        override var includeDeviceInfoInArchives: Boolean
            get() = localDataSource.includeDeviceInfoInArchives
            set(value) {
                localDataSource.includeDeviceInfoInArchives = value
            }
    }
