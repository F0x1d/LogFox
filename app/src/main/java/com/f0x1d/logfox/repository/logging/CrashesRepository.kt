package com.f0x1d.logfox.repository.logging

import android.content.Context
import android.content.SharedPreferences
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.extensions.logline.filterAndSearch
import com.f0x1d.logfox.extensions.notifications.cancelAllCrashNotifications
import com.f0x1d.logfox.extensions.notifications.cancelCrashNotificationFor
import com.f0x1d.logfox.extensions.notifications.sendErrorNotification
import com.f0x1d.logfox.repository.logging.base.LoggingHelperItemsRepository
import com.f0x1d.logfox.repository.logging.readers.crashes.ANRDetector
import com.f0x1d.logfox.repository.logging.readers.crashes.DumpCollector
import com.f0x1d.logfox.repository.logging.readers.crashes.JNICrashDetector
import com.f0x1d.logfox.repository.logging.readers.crashes.JavaCrashDetector
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
    private val dumpCollector: DumpCollector
): LoggingHelperItemsRepository<AppCrash>(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val logDumpsDir = File(context.filesDir.absolutePath + "/dumps").apply {
        if (!exists()) mkdirs()
    }

    override val readers = listOf(
        dumpCollector,
        JavaCrashDetector(this::collectCrash),
        JNICrashDetector(this::collectCrash),
        ANRDetector(this::collectCrash)
    )

    override suspend fun setup() {
        dumpCollector.capacity = appPreferences.logsDumpLinesCount
        appPreferences.registerListener(this)
    }

    override suspend fun stop() {
        appPreferences.unregisterListener(this)
    }

    fun deleteAllByPackageName(appCrash: AppCrash) = runOnAppScope {
        database.appCrashDao().getAllByPackageName(appCrash.packageName).forEach {
            it.deleteDumpFile()
            context.cancelCrashNotificationFor(it)
        }

        database.appCrashDao().deleteByPackageName(appCrash.packageName)
    }

    private suspend fun collectCrash(it: AppCrash) {
        database.appCrashDao().getAllByDateAndTime(it.dateAndTime).filter { crash ->
            crash.packageName == it.packageName
        }.also {
            if (it.isNotEmpty()) return
        }

        val sendNotificationIfNeeded = { appCrash: AppCrash ->
            if (appPreferences.showingNotificationsFor(appCrash.crashType)) {
                context.sendErrorNotification(appCrash)
            }
        }

        val logDump = dumpCollector
            .logsDump
            .filterAndSearch(database.userFilterDao().getAll())
            .joinToString("\n") { logLine -> logLine.original }

        val logDumpFile = when (logDump.isNotEmpty()) {
            true -> File(logDumpsDir, "${it.notificationId}-dump.txt").apply {
                writeText(logDump)
            }

            else -> null
        }

        val appCrash = it.copy(logDumpFile = logDumpFile?.absolutePath)

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
        item.deleteDumpFile()
        database.appCrashDao().delete(item)

        context.cancelCrashNotificationFor(item)
    }

    override suspend fun clearInternal() {
        database.appCrashDao().getAll().forEach {
            it.deleteDumpFile()
        }
        database.appCrashDao().deleteAll()

        context.cancelAllCrashNotifications()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            "pref_logs_dump_lines_count" -> dumpCollector.capacity = appPreferences.logsDumpLinesCount
        }
    }
}