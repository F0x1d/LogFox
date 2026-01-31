package com.f0x1d.logfox.feature.crashes.impl.data

import android.net.Uri
import com.f0x1d.logfox.core.context.deviceData
import com.f0x1d.logfox.feature.crashes.api.data.CrashExportRepository
import com.f0x1d.logfox.feature.crashes.api.model.AppCrash
import com.f0x1d.logfox.feature.preferences.api.data.ServiceSettingsRepository
import javax.inject.Inject

internal class CrashExportRepositoryImpl @Inject constructor(
    private val crashExportLocalDataSource: CrashExportLocalDataSource,
    private val serviceSettingsRepository: ServiceSettingsRepository,
) : CrashExportRepository {

    override suspend fun exportToFile(uri: Uri, crashLog: String) {
        crashExportLocalDataSource.writeToFile(uri, crashLog)
    }

    override suspend fun exportToZip(uri: Uri, appCrash: AppCrash, crashLog: String?) {
        val deviceInfo = if (serviceSettingsRepository.includeDeviceInfoInArchives().value) {
            deviceData
        } else {
            null
        }

        crashExportLocalDataSource.writeToZip(
            uri = uri,
            deviceInfo = deviceInfo,
            crashLog = crashLog,
            logDumpFile = appCrash.logDumpFile,
        )
    }
}
