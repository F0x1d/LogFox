package com.f0x1d.logfox.feature.export.impl.data

import android.net.Uri
import com.f0x1d.logfox.feature.export.api.data.ExportRepository
import java.io.File
import java.util.zip.ZipOutputStream
import javax.inject.Inject

internal class ExportRepositoryImpl @Inject constructor(
    private val localDataSource: ExportLocalDataSource,
) : ExportRepository {

    override suspend fun writeContentToUri(uri: Uri, content: String) {
        localDataSource.writeContentToUri(uri, content)
    }

    override suspend fun writeContentAndFileToUri(uri: Uri, content: String, file: File) {
        localDataSource.writeContentAndFileToUri(uri, content, file)
    }

    override suspend fun copyFileToUri(uri: Uri, file: File) {
        localDataSource.copyFileToUri(uri, file)
    }

    override suspend fun writeZipToUri(uri: Uri, block: ZipOutputStream.() -> Unit) {
        localDataSource.writeZipToUri(uri, block)
    }

    override suspend fun readContentFromUri(uri: Uri): String? {
        return localDataSource.readContentFromUri(uri)
    }
}
