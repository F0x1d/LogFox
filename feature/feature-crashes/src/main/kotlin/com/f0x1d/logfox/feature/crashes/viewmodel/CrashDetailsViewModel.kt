package com.f0x1d.logfox.feature.crashes.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.f0x1d.logfox.arch.di.IODispatcher
import com.f0x1d.logfox.arch.viewmodel.BaseViewModel
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.datetime.DateTimeFormatter
import com.f0x1d.logfox.feature.crashes.core.repository.CrashesRepository
import com.f0x1d.logfox.feature.crashes.di.CrashId
import com.f0x1d.logfox.io.exportToZip
import com.f0x1d.logfox.io.putZipEntry
import com.f0x1d.logfox.model.deviceData
import com.f0x1d.logfox.preferences.shared.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CrashDetailsViewModel @Inject constructor(
    @CrashId val crashId: Long,
    val dateTimeFormatter: DateTimeFormatter,
    private val crashesRepository: CrashesRepository,
    val appPreferences: AppPreferences,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    application: Application,
): BaseViewModel(application) {

    val crash = crashesRepository.getByIdAsFlow(crashId)
        .map {
            when (it) {
                null -> null

                else -> runCatching {
                    it to it.logFile?.readText()
                }.getOrNull()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )

    fun exportCrashToZip(uri: Uri) = launchCatching(ioDispatcher) {
        val (appCrash, crashLog) = crash.value ?: return@launchCatching

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

    fun deleteCrash(appCrash: AppCrash) = launchCatching {
        crashesRepository.delete(appCrash)
    }
}
