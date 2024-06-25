package com.f0x1d.logfox.feature.crashes.core.repository

import android.content.Context
import com.f0x1d.logfox.arch.di.IODispatcher
import com.f0x1d.logfox.arch.repository.DatabaseProxyRepository
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.feature.crashes.core.controller.CrashesNotificationsController
import com.f0x1d.logfox.feature.crashes.core.repository.reader.ANRDetector
import com.f0x1d.logfox.feature.crashes.core.repository.reader.JNICrashDetector
import com.f0x1d.logfox.feature.crashes.core.repository.reader.JavaCrashDetector
import com.f0x1d.logfox.model.logline.LogLine
import com.f0x1d.logfox.preferences.shared.AppPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

interface CrashesRepository : DatabaseProxyRepository<AppCrash> {
    val readers: List<suspend (LogLine) -> Unit>

    suspend fun deleteAllByPackageName(appCrash: AppCrash)
}

internal class CrashesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationsController: CrashesNotificationsController,
    private val database: AppDatabase,
    private val appPreferences: AppPreferences,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : CrashesRepository {

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
        database.appCrashDao().getAllByDateAndTime(
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
                id = database.appCrashDao().insert(appCrash)
            )

            sendNotificationIfNeeded(appCrashWithId)
        } else {
            sendNotificationIfNeeded(appCrash)
        }
    }

    override fun getAllAsFlow(): Flow<List<AppCrash>> =
        database.appCrashDao().getAllAsFlow().flowOn(ioDispatcher)

    override fun getByIdAsFlow(id: Long): Flow<AppCrash?> =
        database.appCrashDao().getByIdAsFlow(id).flowOn(ioDispatcher)

    override suspend fun getAll(): List<AppCrash> = withContext(ioDispatcher) {
        database.appCrashDao().getAll()
    }

    override suspend fun getById(id: Long): AppCrash? = withContext(ioDispatcher) {
        database.appCrashDao().getById(id)
    }

    override suspend fun deleteAllByPackageName(appCrash: AppCrash) = withContext(ioDispatcher) {
        database.appCrashDao().getAllByPackageName(appCrash.packageName).forEach {
            it.deleteAssociatedFiles()
            notificationsController.cancelCrashNotificationFor(it)
        }

        database.appCrashDao().deleteByPackageName(appCrash.packageName)
    }

    override suspend fun update(item: AppCrash) = withContext(ioDispatcher) {
        database.appCrashDao().update(item)
    }

    override suspend fun delete(item: AppCrash) = withContext(ioDispatcher) {
        item.deleteAssociatedFiles()
        database.appCrashDao().delete(item)

        notificationsController.cancelCrashNotificationFor(item)
    }

    override suspend fun clear() = withContext(ioDispatcher) {
        getAll().forEach {
            it.deleteAssociatedFiles()
        }
        database.appCrashDao().deleteAll()

        notificationsController.cancelAllCrashNotifications()
    }
}
