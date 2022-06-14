package com.f0x1d.logfox.repository

import android.content.Context
import com.f0x1d.logfox.LogFoxApp
import com.f0x1d.logfox.database.AppCrash
import com.f0x1d.logfox.database.AppDatabase
import com.f0x1d.logfox.extensions.sendErrorNotification
import com.f0x1d.logfox.repository.base.LoggingHelperRepository
import com.f0x1d.logfox.repository.readers.base.BaseReader
import com.f0x1d.logfox.repository.readers.crashes.ANRDetector
import com.f0x1d.logfox.repository.readers.crashes.JNICrashDetector
import com.f0x1d.logfox.repository.readers.crashes.JavaCrashDetector
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CrashesRepository @Inject constructor(@ApplicationContext private val context: Context,
                                            private val database: AppDatabase): LoggingHelperRepository() {

    val crashesFlow = MutableStateFlow(listOf<AppCrash>())

    private val crashCollected: suspend (AppCrash) -> Unit = { appCrash ->
        crashesFlow.update {
            appCrash.copy(id = database.appCrashDao().insert(appCrash)).run {
                context.sendErrorNotification(this)

                it.toMutableList().apply {
                    add(0, this@run)
                }
            }
        }
    }

    override val readers = listOf<BaseReader>(
        JavaCrashDetector(crashCollected),
        JNICrashDetector(crashCollected),
        ANRDetector(crashCollected)
    )

    override suspend fun setup() {
        crashesFlow.update {
            database.appCrashDao().getAll()
        }
    }

    fun clearCrashes() {
        LogFoxApp.applicationScope.launch(Dispatchers.Default) {
            crashesFlow.update {
                database.appCrashDao().deleteAll()
                emptyList()
            }
        }
    }
}