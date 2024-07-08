package com.f0x1d.logfox.feature.crashes.core.controller

import android.content.Context
import com.f0x1d.logfox.arch.di.IODispatcher
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.feature.crashes.core.repository.reader.ANRDetector
import com.f0x1d.logfox.feature.crashes.core.repository.reader.JNICrashDetector
import com.f0x1d.logfox.feature.crashes.core.repository.reader.JavaCrashDetector
import com.f0x1d.logfox.model.logline.LogLine
import com.f0x1d.logfox.preferences.shared.AppPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

interface CrashesController {
    val readers: List<suspend (LogLine) -> Unit>
}

internal class CrashesControllerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationsController: CrashesNotificationsController,
    private val database: AppDatabase,
    private val appPreferences: AppPreferences,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : CrashesController {

    private val logsDir = File(context.filesDir.absolutePath + "/crashes").apply {
        if (!exists()) mkdirs()
    }
    private val logDumpsDir = File(context.filesDir.absolutePath + "/dumps").apply {
        if (!exists()) mkdirs()
    }

    override val readers: List<suspend (LogLine) -> Unit> = listOf(
        JavaCrashDetector(context, this::collectCrash),
        JNICrashDetector(context, this::collectCrash),
        ANRDetector(context, this::collectCrash),
    )

    private suspend fun collectCrash(it: AppCrash, lines: List<LogLine>) = withContext(ioDispatcher) {
        // Don't handle if already present in data
        database.appCrashes().getAllByDateAndTime(
            dateAndTime = it.dateAndTime,
            packageName = it.packageName
        ).also {
            if (it.isNotEmpty()) return@withContext
        }

        val crashLog = lines.joinToString("\n") {
            it.content
        }

        val sendNotificationIfNeeded = { appCrash: AppCrash ->
            if (appPreferences.showingNotificationsFor(appCrash.crashType)) {
                notificationsController.sendErrorNotification(appCrash, crashLog)
            }
        }

        val logFile = File(logsDir, "${it.dateAndTime}-crash.log").apply {
            writeText(crashLog)
        }

        val appCrash = it.copy(
            logFile = logFile,
            logDumpFile = null, // TODO: return log dumps!
        )

        if (appPreferences.collectingFor(appCrash.crashType)) {
            val appCrashWithId = appCrash.copy(
                id = database.appCrashes().insert(appCrash)
            )

            sendNotificationIfNeeded(appCrashWithId)
        } else {
            sendNotificationIfNeeded(appCrash)
        }
    }
}
