package com.f0x1d.logfox.feature.export.impl.data

import android.content.Context
import android.net.Uri
import com.f0x1d.logfox.core.di.IODispatcher
import com.f0x1d.logfox.core.io.exportToZip
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.util.zip.ZipOutputStream
import javax.inject.Inject

internal class ExportLocalDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : ExportLocalDataSource {

    override suspend fun writeContentToUri(uri: Uri, content: String): Unit = withContext(ioDispatcher) {
        context.contentResolver.openOutputStream(uri)?.use {
            it.write(content.encodeToByteArray())
        }
    }

    override suspend fun writeContentAndFileToUri(uri: Uri, content: String, file: File): Unit = withContext(ioDispatcher) {
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(content.encodeToByteArray())
            file.inputStream().use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }

    override suspend fun copyFileToUri(uri: Uri, file: File): Unit = withContext(ioDispatcher) {
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            file.inputStream().use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }

    override suspend fun writeZipToUri(
        uri: Uri,
        block: ZipOutputStream.() -> Unit,
    ): Unit = withContext(ioDispatcher) {
        context.contentResolver.openOutputStream(uri)?.use {
            it.exportToZip(block)
        }
    }

    override suspend fun readContentFromUri(uri: Uri): String? = withContext(ioDispatcher) {
        context.contentResolver.openInputStream(uri)?.use {
            it.readBytes().decodeToString()
        }
    }
}
