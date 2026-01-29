package com.f0x1d.logfox.feature.crashes.impl.data

import android.app.Application
import android.net.Uri
import com.f0x1d.logfox.core.di.IODispatcher
import com.f0x1d.logfox.core.io.exportToZip
import com.f0x1d.logfox.core.io.putZipEntry
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

internal class CrashExportLocalDataSourceImpl @Inject constructor(
    private val application: Application,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : CrashExportLocalDataSource {

    override suspend fun writeToFile(uri: Uri, crashLog: String) {
        withContext(ioDispatcher) {
            application.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(crashLog.encodeToByteArray())
            }
        }
    }

    override suspend fun writeToZip(
        uri: Uri,
        deviceInfo: String?,
        crashLog: String?,
        logDumpFile: File?,
    ) {
        withContext(ioDispatcher) {
            application.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.exportToZip {
                    if (deviceInfo != null) {
                        putZipEntry(
                            name = "device.txt",
                            content = deviceInfo.encodeToByteArray(),
                        )
                    }

                    if (crashLog != null) {
                        putZipEntry(
                            name = "crash.log",
                            content = crashLog.encodeToByteArray(),
                        )
                    }

                    logDumpFile?.let { file ->
                        putZipEntry(
                            name = "dump.log",
                            file = file,
                        )
                    }
                }
            }
        }
    }
}
