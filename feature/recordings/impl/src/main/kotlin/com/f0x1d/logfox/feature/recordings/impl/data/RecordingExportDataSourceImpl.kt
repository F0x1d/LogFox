package com.f0x1d.logfox.feature.recordings.impl.data

import android.content.Context
import android.net.Uri
import com.f0x1d.logfox.core.context.deviceData
import com.f0x1d.logfox.core.di.IODispatcher
import com.f0x1d.logfox.core.io.exportToZip
import com.f0x1d.logfox.core.io.putZipEntry
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

internal class RecordingExportDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : RecordingExportDataSource {

    override suspend fun exportFileToUri(file: File, uri: Uri) = withContext(ioDispatcher) {
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            file.inputStream().use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        Unit
    }

    override suspend fun exportZipToUri(
        uri: Uri,
        recordingFile: File,
        includeDeviceInfo: Boolean,
    ) = withContext(ioDispatcher) {
        context.contentResolver.openOutputStream(uri)?.use {
            it.exportToZip {
                if (includeDeviceInfo) {
                    putZipEntry(
                        "device.txt",
                        deviceData.encodeToByteArray(),
                    )
                }

                putZipEntry(
                    name = "recorded.log",
                    file = recordingFile,
                )
            }
        }
        Unit
    }
}
