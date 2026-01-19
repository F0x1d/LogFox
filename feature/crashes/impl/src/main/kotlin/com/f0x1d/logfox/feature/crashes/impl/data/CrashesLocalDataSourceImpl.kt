package com.f0x1d.logfox.feature.crashes.impl.data

import android.content.Context
import com.f0x1d.logfox.core.di.IODispatcher
import com.f0x1d.logfox.feature.crashes.api.data.CrashesRepository
import com.f0x1d.logfox.feature.crashes.api.data.DisabledAppsRepository
import com.f0x1d.logfox.feature.crashes.impl.data.reader.ANRDetector
import com.f0x1d.logfox.feature.crashes.impl.data.reader.JNICrashDetector
import com.f0x1d.logfox.feature.crashes.impl.data.reader.JavaCrashDetector
import com.f0x1d.logfox.feature.database.model.AppCrash
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.preferences.data.CrashesSettingsRepository
import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

internal class CrashesLocalDataSourceImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val notificationsLocalDataSource: CrashesNotificationsLocalDataSource,
        private val crashesRepository: CrashesRepository,
        private val disabledAppsRepository: DisabledAppsRepository,
        private val crashesSettingsRepository: CrashesSettingsRepository,
        private val logsSettingsRepository: LogsSettingsRepository,
        @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : CrashesLocalDataSource {
        private val logsDir =
            File(context.filesDir.absolutePath + "/crashes").apply {
                if (!exists()) mkdirs()
            }
        private val logDumpsDir =
            File(context.filesDir.absolutePath + "/dumps").apply {
                if (!exists()) mkdirs()
            }

        override val readers: List<suspend (LogLine) -> Unit> =
            listOf(
                JavaCrashDetector(context, this::collectCrash),
                JNICrashDetector(context, logsSettingsRepository, this::collectCrash),
                ANRDetector(context, this::collectCrash),
            )

        private suspend fun collectCrash(
            appCrash: AppCrash,
            lines: List<LogLine>,
        ) {
            if (disabledAppsRepository.isDisabledFor(appCrash.packageName)) {
                return
            }

            // Don't handle if already present in data
            crashesRepository
                .getAllByDateAndTime(
                    dateAndTime = appCrash.dateAndTime,
                    packageName = appCrash.packageName,
                ).also {
                    if (it.isNotEmpty()) return
                }

            val crashLog =
                lines.joinToString("\n") {
                    it.content
                }

            val sendNotificationIfNeeded = { crash: AppCrash ->
                if (crashesSettingsRepository.showingNotificationsFor(crash.crashType.name)) {
                    notificationsLocalDataSource.sendErrorNotification(crash, crashLog)
                }
            }

            val logFile =
                withContext(ioDispatcher) {
                    File(logsDir, "${appCrash.dateAndTime}-crash.log").apply {
                        writeText(crashLog)
                    }
                }

            val appCrashWithLog =
                appCrash.copy(
                    logFile = logFile,
                    logDumpFile = null, // TODO: return log dumps!
                )

            if (crashesSettingsRepository.collectingFor(appCrashWithLog.crashType.name)) {
                val appCrashWithId =
                    appCrashWithLog.copy(
                        id = crashesRepository.insert(appCrashWithLog),
                    )

                sendNotificationIfNeeded(appCrashWithId)
            } else {
                sendNotificationIfNeeded(appCrashWithLog)
            }
        }
    }
