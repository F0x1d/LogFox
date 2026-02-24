package com.f0x1d.logfox.feature.crashes.impl.data

import android.net.Uri
import com.f0x1d.logfox.core.context.deviceData
import com.f0x1d.logfox.core.io.putZipEntry
import com.f0x1d.logfox.feature.crashes.api.data.CrashExportRepository
import com.f0x1d.logfox.feature.crashes.api.data.CrashesRepository
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import com.f0x1d.logfox.feature.export.api.data.ExportRepository
import com.f0x1d.logfox.feature.logging.api.data.LogLineFormatterRepository
import com.f0x1d.logfox.feature.logging.api.data.LogLineParser
import com.f0x1d.logfox.feature.preferences.api.data.ServiceSettingsRepository
import java.io.File
import javax.inject.Inject

internal class CrashExportRepositoryImpl @Inject constructor(
    private val crashesRepository: CrashesRepository,
    private val exportRepository: ExportRepository,
    private val serviceSettingsRepository: ServiceSettingsRepository,
    private val appInfoDataSource: AppInfoDataSource,
    private val dateTimeFormatter: DateTimeFormatter,
    private val logLineParser: LogLineParser,
    private val logLineFormatterRepository: LogLineFormatterRepository,
) : CrashExportRepository {

    override suspend fun exportToFile(crashId: Long, uri: Uri) {
        val appCrash = crashesRepository.getById(crashId) ?: return

        val content = buildString {
            if (serviceSettingsRepository.includeDeviceInfoInArchives().value) {
                appendLine(deviceData)
                appendLine()
            }
            if (serviceSettingsRepository.includeAppInfoInExports().value) {
                appendLine(appInfoDataSource.getAppInfo(appCrash.packageName).format())
                appendLine()
            }
            appCrash.logFile?.let { append(formatLogFileContent(it)) }
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

        val appInfo = if (serviceSettingsRepository.includeAppInfoInExports().value) {
            appInfoDataSource.getAppInfo(appCrash.packageName).format()
        } else {
            null
        }

        val logExtension = if (serviceSettingsRepository.exportLogsAsTxt().value) "txt" else "log"
        val suffix = dateTimeFormatter.formatForExport(appCrash.dateAndTime)

        exportRepository.writeZipToUri(uri) {
            if (deviceInfo != null) {
                putZipEntry(
                    name = "device_${suffix}.txt",
                    content = deviceInfo.encodeToByteArray(),
                )
            }

            if (appInfo != null) {
                putZipEntry(
                    name = "app_${suffix}.txt",
                    content = appInfo.encodeToByteArray(),
                )
            }

            appCrash.logFile?.let { file ->
                putZipEntry(
                    name = "crash_${suffix}.$logExtension",
                    content = formatLogFileContent(file).encodeToByteArray(),
                )
            }

            appCrash.logDumpFile?.let { file ->
                putZipEntry(
                    name = "dump_${suffix}.$logExtension",
                    content = formatLogFileContent(file).encodeToByteArray(),
                )
            }
        }
    }

    private fun formatLogFileContent(file: File): String {
        val lines = file.readLines()
        return lines.mapIndexed { index, line ->
            logLineParser.parse(index.toLong(), line)?.let { logLine ->
                logLineFormatterRepository.formatForExport(logLine)
            } ?: line
        }.joinToString("\n")
    }
}
