package com.f0x1d.logfox.repository.logging

import android.content.Context
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.extensions.notifications.cancelAllCrashNotifications
import com.f0x1d.logfox.extensions.notifications.cancelCrashNotificationFor
import com.f0x1d.logfox.extensions.notifications.sendErrorNotification
import com.f0x1d.logfox.repository.logging.base.LoggingHelperItemsRepository
import com.f0x1d.logfox.repository.logging.readers.crashes.ANRDetector
import com.f0x1d.logfox.repository.logging.readers.crashes.JNICrashDetector
import com.f0x1d.logfox.repository.logging.readers.crashes.JavaCrashDetector
import com.f0x1d.logfox.utils.preferences.AppPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CrashesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val appPreferences: AppPreferences
): LoggingHelperItemsRepository<AppCrash>() {

    private val crashCollected: suspend (AppCrash) -> Unit = { appCrash ->
        if (appPreferences.collectingFor(appCrash.crashType)) {
            appCrash.copy(id = database.appCrashDao().insert(appCrash)).also { appCrash ->
                if (appPreferences.showingNotificationsFor(appCrash.crashType)) {
                    context.sendErrorNotification(appCrash)
                }
            }
        } else if (appPreferences.showingNotificationsFor(appCrash.crashType)) {
            context.sendErrorNotification(appCrash)
        }
    }

    override val readers = listOf(
        JavaCrashDetector(crashCollected),
        JNICrashDetector(crashCollected),
        ANRDetector(crashCollected)
    )

    fun deleteAllByPackageName(appCrash: AppCrash) = runOnAppScope {
        database.appCrashDao().getAllByPackageName(appCrash.packageName).forEach {
            context.cancelCrashNotificationFor(it)
        }

        database.appCrashDao().deleteByPackageName(appCrash.packageName)
    }

    override suspend fun updateInternal(item: AppCrash) = database.appCrashDao().update(item)

    override suspend fun deleteInternal(item: AppCrash) {
        database.appCrashDao().delete(item)

        context.cancelCrashNotificationFor(item)
    }

    override suspend fun clearInternal() {
        database.appCrashDao().deleteAll()

        context.cancelAllCrashNotifications()
    }
}