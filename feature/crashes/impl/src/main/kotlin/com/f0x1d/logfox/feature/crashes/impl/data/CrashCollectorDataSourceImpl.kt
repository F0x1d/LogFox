package com.f0x1d.logfox.feature.crashes.impl.data

import android.content.Context
import com.f0x1d.logfox.core.di.IODispatcher
import com.f0x1d.logfox.feature.crashes.api.data.CrashesRepository
import com.f0x1d.logfox.feature.crashes.api.data.DisabledAppsRepository
import com.f0x1d.logfox.feature.crashes.api.model.AppCrash
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.preferences.api.data.CrashesSettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

internal class CrashCollectorDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationsLocalDataSource: CrashesNotificationsLocalDataSource,
    private val crashesRepository: CrashesRepository,
    private val disabledAppsRepository: DisabledAppsRepository,
    private val crashesSettingsRepository: CrashesSettingsRepository,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : CrashCollectorDataSource {

    private val logsDir = File(context.filesDir, "crashes").apply {
        if (!exists()) mkdirs()
    }

    override suspend fun collectCrash(appCrash: AppCrash, logLines: List<LogLine>) {
        if (disabledAppsRepository.isDisabledFor(appCrash.packageName)) return

        // Avoid duplicates
        val existingCrashes = crashesRepository.getAllByDateAndTime(
            dateAndTime = appCrash.dateAndTime,
            packageName = appCrash.packageName,
        )
        if (existingCrashes.isNotEmpty()) return

        val crashLog = logLines.joinToString(separator = "\n") { it.content }

        val logFile = withContext(ioDispatcher) {
            File(logsDir, "${appCrash.dateAndTime}-crash.log").apply {
                writeText(crashLog)
            }
        }

        val crashWithLog = appCrash.copy(
            logFile = logFile,
            logDumpFile = null, // TODO: return log dumps!
        )

        val finalCrash = if (crashesSettingsRepository.collectingFor(crashWithLog.crashType.name)) {
            crashWithLog.copy(id = crashesRepository.insert(crashWithLog))
        } else {
            crashWithLog
        }

        if (crashesSettingsRepository.showingNotificationsFor(finalCrash.crashType.name)) {
            notificationsLocalDataSource.sendErrorNotification(finalCrash, crashLog)
        }
    }
}
