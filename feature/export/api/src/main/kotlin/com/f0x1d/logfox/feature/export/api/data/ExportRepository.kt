package com.f0x1d.logfox.feature.export.api.data

import android.net.Uri
import java.io.File
import java.util.zip.ZipOutputStream

interface ExportRepository {
    suspend fun writeContentToUri(uri: Uri, content: String)
    suspend fun copyFileToUri(uri: Uri, file: File)
    suspend fun writeZipToUri(uri: Uri, block: ZipOutputStream.() -> Unit)
    suspend fun readContentFromUri(uri: Uri): String?
}
