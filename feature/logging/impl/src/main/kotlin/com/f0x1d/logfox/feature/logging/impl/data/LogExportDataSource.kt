package com.f0x1d.logfox.feature.logging.impl.data

import android.net.Uri

internal interface LogExportDataSource {
    suspend fun exportToUri(content: String, uri: Uri)
}
