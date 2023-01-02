package com.f0x1d.logfox.repository.logging

import android.content.Context
import com.f0x1d.logfox.database.AppCrash
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.extensions.notifications.cancelAllCrashNotifications
import com.f0x1d.logfox.extensions.notifications.cancelCrashNotificationForPackage
import com.f0x1d.logfox.extensions.notifications.sendErrorNotification
import com.f0x1d.logfox.extensions.updateList
import com.f0x1d.logfox.repository.logging.base.LoggingHelperItemsRepository
import com.f0x1d.logfox.repository.logging.readers.base.BaseReader
import com.f0x1d.logfox.repository.logging.readers.crashes.ANRDetector
import com.f0x1d.logfox.repository.logging.readers.crashes.JNICrashDetector
import com.f0x1d.logfox.repository.logging.readers.crashes.JavaCrashDetector
import com.f0x1d.logfox.utils.preferences.AppPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.update
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
            itemsFlow.updateList {
                appCrash.copy(id = database.appCrashDao().insert(appCrash)).apply {
                    if (appPreferences.showingNotificationsFor(appCrash.crashType)) {
                        context.sendErrorNotification(this, true)
                    }

                    add(0, this)
                }
            }
        } else if (appPreferences.showingNotificationsFor(appCrash.crashType)) {
            context.sendErrorNotification(appCrash, false)
        }
    }

    override val readers = listOf<BaseReader>(
        JavaCrashDetector(crashCollected),
        JNICrashDetector(crashCollected),
        ANRDetector(crashCollected)
    )

    override suspend fun setup() = itemsFlow.update {
        database.appCrashDao().getAll()
    }

    override fun deleteInternal(item: AppCrash) {
        itemsFlow.updateList {
            remove(item)
            database.appCrashDao().delete(item)
        }

        context.cancelCrashNotificationForPackage(item)
    }

    override fun clearInternal() {
        itemsFlow.update {
            database.appCrashDao().deleteAll()
            emptyList()
        }

        context.cancelAllCrashNotifications()
    }
}