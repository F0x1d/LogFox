package com.f0x1d.logfox.feature.logging.api.domain

import android.net.Uri
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import kotlinx.coroutines.flow.Flow

interface ReadLogFileUseCase {
    operator fun invoke(uri: Uri, logsDisplayLimit: Int): Flow<List<LogLine>>
}
