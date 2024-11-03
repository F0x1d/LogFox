package com.f0x1d.logfox.feature.crashes.details.presentation

import android.app.Application
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.arch.di.IODispatcher
import com.f0x1d.logfox.arch.io.exportToZip
import com.f0x1d.logfox.arch.io.putZipEntry
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.datetime.DateTimeFormatter
import com.f0x1d.logfox.feature.crashes.api.data.CrashesRepository
import com.f0x1d.logfox.feature.crashes.api.data.DisabledAppsRepository
import com.f0x1d.logfox.feature.crashes.details.di.CrashId
import com.f0x1d.logfox.model.deviceData
import com.f0x1d.logfox.preferences.shared.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CrashDetailsViewModel @Inject constructor(
    @CrashId val crashId: Long,
    private val crashesRepository: CrashesRepository,
    private val disabledAppsRepository: DisabledAppsRepository,
    private val appPreferences: AppPreferences,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    dateTimeFormatter: DateTimeFormatter,
    application: Application,
): BaseViewModel<CrashDetailsState, CrashDetailsAction>(
    initialStateProvider = { CrashDetailsState() },
    application = application,
), DateTimeFormatter by dateTimeFormatter {
    val wrapCrashLogLines get() = appPreferences.wrapCrashLogLines
    val useSeparateNotificationsChannelsForCrashes get() = appPreferences.useSeparateNotificationsChannelsForCrashes

    init {
        load()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun load() {
        viewModelScope.launch {
            crashesRepository.getByIdAsFlow(crashId)
                .map {
                    when (it) {
                        null -> null

                        else -> runCatching {
                            it to it.logFile?.readText()
                        }.getOrNull()
                    }
                }
                .flowOn(ioDispatcher)
                .onEach { value ->
                    value?.let { (crash, crashLog) ->
                        reduce { copy(crash = crash, crashLog = crashLog) }
                    }
                }
                .launchIn(this)

            crashesRepository.getByIdAsFlow(crashId)
                .flatMapLatest { crash ->
                    crash?.let {
                        disabledAppsRepository.disabledForFlow(it.packageName)
                    } ?: flowOf(null)
                }
                .onEach { blacklisted ->
                    reduce { copy(blacklisted = blacklisted) }
                }
                .launchIn(this)
        }
    }

    fun exportCrashToZip(uri: Uri) = launchCatching(ioDispatcher) {
        val appCrash = currentState.crash ?: return@launchCatching
        val crashLog = currentState.crashLog

        ctx.contentResolver.openOutputStream(uri)?.use {
            it.exportToZip {
                if (appPreferences.includeDeviceInfoInArchives) putZipEntry(
                    name = "device.txt",
                    content = deviceData.encodeToByteArray(),
                )

                if (crashLog != null) putZipEntry(
                    name = "crash.log",
                    content = crashLog.encodeToByteArray(),
                )

                appCrash.logDumpFile?.let { logDumpFile ->
                    putZipEntry(
                        name = "dump.log",
                        file = logDumpFile,
                    )
                }
            }
        }
    }

    fun changeBlacklist(appCrash: AppCrash) = launchCatching {
        disabledAppsRepository.checkApp(appCrash.packageName)
    }

    fun deleteCrash(appCrash: AppCrash) = launchCatching {
        crashesRepository.delete(appCrash)
    }
}
