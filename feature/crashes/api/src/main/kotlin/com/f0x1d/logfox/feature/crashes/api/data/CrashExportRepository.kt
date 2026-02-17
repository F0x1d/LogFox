package com.f0x1d.logfox.feature.crashes.api.data

import android.net.Uri

interface CrashExportRepository {
    suspend fun exportToFile(crashId: Long, uri: Uri)
    suspend fun exportToZip(crashId: Long, uri: Uri)
}
