package com.f0x1d.logfox.feature.crashes.impl.data

import android.net.Uri
import com.f0x1d.logfox.core.context.deviceData
import com.f0x1d.logfox.core.io.putZipEntry
import com.f0x1d.logfox.feature.crashes.api.data.CrashExportRepository
import com.f0x1d.logfox.feature.crashes.api.data.CrashesRepository
import com.f0x1d.logfox.feature.export.api.data.ExportRepository
import com.f0x1d.logfox.feature.preferences.api.data.ServiceSettingsRepository
import javax.inject.Inject

internal class CrashExportRepositoryImpl @Inject constructor(
    private val crashesRepository: CrashesRepository,
    private val exportRepository: ExportRepository,
    private val serviceSettingsRepository: ServiceSettingsRepository,
) : CrashExportRepository {

    override suspend fun exportToFile(crashId: Long, uri: Uri) {
        val appCrash = crashesRepository.getById(crashId) ?: return

        val content = buildString {
            if (serviceSettingsRepository.includeDeviceInfoInArchives().value) {
                appendLine(deviceData)
                appendLine()
            }
            appCrash.logFile?.readText()?.let { append(it) }
        }

        exportRepository.writeContentToUri(uri, content)
    }

    override suspend fun exportToZip(crashId: Long, uri: Uri) {
        val appCrash = crashesRepository.getById(crashId) ?: return

        val deviceInfo = if (serviceSettingsRepository.includeDeviceInfoInArchives().value) {
            deviceData
        } else {
            null
        }

        exportRepository.writeZipToUri(uri) {
            if (deviceInfo != null) {
                putZipEntry(
                    name = "device.txt",
                    content = deviceInfo.encodeToByteArray(),
                )
            }

            appCrash.logFile?.let { file ->
                putZipEntry(
                    name = "crash.log",
                    file = file,
                )
            }

            appCrash.logDumpFile?.let { file ->
                putZipEntry(
                    name = "dump.log",
                    file = file,
                )
            }
        }
    }
}
