package com.f0x1d.logfox.feature.logging.impl.data

import android.net.Uri
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import kotlinx.coroutines.flow.Flow

internal interface LogFileDataSource {

    fun readLogLines(uri: Uri, logsDisplayLimit: Int): Flow<List<LogLine>>

    fun getFileName(uri: Uri): String?
}
