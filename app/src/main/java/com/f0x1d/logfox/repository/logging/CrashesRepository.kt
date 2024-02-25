package com.f0x1d.logfox.repository.logging

import android.content.Context
import android.content.SharedPreferences
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.extensions.notifications.cancelAllCrashNotifications
import com.f0x1d.logfox.extensions.notifications.cancelCrashNotificationFor
import com.f0x1d.logfox.extensions.notifications.sendErrorNotification
import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.repository.logging.base.LoggingHelperItemsRepository
import com.f0x1d.logfox.repository.logging.readers.crashes.ANRDetector
import com.f0x1d.logfox.repository.logging.readers.crashes.JNICrashDetector
import com.f0x1d.logfox.repository.logging.readers.crashes.JavaCrashDetector
import com.f0x1d.logfox.repository.logging.readers.recordings.RewritingRecordingReader
import com.f0x1d.logfox.utils.preferences.AppPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CrashesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val appPreferences: AppPreferences,
    private val cacheRecordingReader: RewritingRecordingReader
): LoggingHelperItemsRepository<AppCrash>(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val logsDir = File(context.filesDir.absolutePath + "/crashes").apply {
        if (!exists()) mkdirs()
    }
    private val logDumpsDir = File(context.filesDir.absolutePath + "/dumps").apply {
        if (!exists()) mkdirs()
    }

    override val readers = listOf(
        JavaCrashDetector(this::collectCrash),
        JNICrashDetector(this::collectCrash),
        ANRDetector(this::collectCrash)
    )

    override suspend fun setup() {
        super.setup()

        appPreferences.registerListener(this)

        database.appCrashDao().clearIfNeeded()

        val crashes = database.appCrashDao().getAll()
        val shouldMigrate = crashes.any { it.logFile == null }

        if (!shouldMigrate) return

        database.appCrashDao().update(
            crashes.map {
                val logFile = File(logsDir, "${it.dateAndTime}-crash.log").apply {
                    writeText(it.log)
                }

                it.copy(
                    log = "",
                    logFile = logFile
                )
            }
        )
    }

    override suspend fun stop() {
        super.stop()
        appPreferences.unregisterListener(this)
    }

    fun deleteAllByPackageName(appCrash: AppCrash) = runOnRepoScope {
        database.appCrashDao().getAllByPackageName(appCrash.packageName).forEach {
            it.deleteAssociatedFiles()
            context.cancelCrashNotificationFor(it)
        }

        database.appCrashDao().deleteByPackageName(appCrash.packageName)
    }

    private suspend fun collectCrash(it: AppCrash, lines: List<LogLine>) {
        // Don't handle if already present in data
        database.appCrashDao().getAllByDateAndTime(
            dateAndTime = it.dateAndTime,
            packageName = it.packageName
        ).also {
            if (it.isNotEmpty()) return
        }

        val crashLog = lines.joinToString("\n") {
            it.content
        }

        val sendNotificationIfNeeded = { appCrash: AppCrash ->
            if (appPreferences.showingNotificationsFor(appCrash.crashType)) {
                context.sendErrorNotification(appCrash, crashLog)
            }
        }

        val logFile = File(logsDir, "${it.dateAndTime}-crash.log").apply {
            writeText(crashLog)
        }

        val logDumpFile = when (appPreferences.useSessionCache) {
            true -> File(logDumpsDir, "${it.dateAndTime}-dump.log").let {
                cacheRecordingReader.dumpLines()
                cacheRecordingReader.copyFileTo(it)
            }

            else -> null
        }

        val appCrash = it.copy(
            logFile = logFile,
            logDumpFile = logDumpFile
        )

        if (appPreferences.collectingFor(appCrash.crashType)) {
            val appCrashWithId = appCrash.copy(
                id = database.appCrashDao().insert(appCrash)
            )

            sendNotificationIfNeeded(appCrashWithId)
        } else sendNotificationIfNeeded(
            appCrash
        )
    }

    override suspend fun updateInternal(item: AppCrash) = database.appCrashDao().update(item)

    override suspend fun deleteInternal(item: AppCrash) {
        item.deleteAssociatedFiles()
        database.appCrashDao().delete(item)

        context.cancelCrashNotificationFor(item)
    }

    override suspend fun clearInternal() {
        database.appCrashDao().getAll().forEach {
            it.deleteAssociatedFiles()
        }
        database.appCrashDao().deleteAll()

        context.cancelAllCrashNotifications()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            "pref_session_cache_lines_count" -> cacheRecordingReader.updateRecordedLinesSize(appPreferences.sessionCacheLinesCount)
        }
    }
}