package com.f0x1d.logfox.feature.logging.api.domain

import android.net.Uri
import com.f0x1d.logfox.feature.logging.api.model.LogLine

interface ExportLogsToUriUseCase {
    suspend operator fun invoke(lines: List<LogLine>, uri: Uri)
}
