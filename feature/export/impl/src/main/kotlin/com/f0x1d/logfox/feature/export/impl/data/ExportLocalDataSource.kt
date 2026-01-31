package com.f0x1d.logfox.feature.export.impl.data

import android.net.Uri
import java.io.File
import java.util.zip.ZipOutputStream

internal interface ExportLocalDataSource {
    suspend fun writeContentToUri(uri: Uri, content: String)
    suspend fun writeContentAndFileToUri(uri: Uri, content: String, file: File)
    suspend fun copyFileToUri(uri: Uri, file: File)
    suspend fun writeZipToUri(uri: Uri, block: ZipOutputStream.() -> Unit)
    suspend fun readContentFromUri(uri: Uri): String?
}
