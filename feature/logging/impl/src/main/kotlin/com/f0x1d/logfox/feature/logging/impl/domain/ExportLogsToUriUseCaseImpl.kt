package com.f0x1d.logfox.feature.logging.impl.domain

import android.net.Uri
import com.f0x1d.logfox.feature.export.api.data.ExportRepository
import com.f0x1d.logfox.feature.logging.api.data.LogLineFormatterRepository
import com.f0x1d.logfox.feature.logging.api.domain.ExportLogsToUriUseCase
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import javax.inject.Inject

internal class ExportLogsToUriUseCaseImpl @Inject constructor(
    private val logLineFormatterRepository: LogLineFormatterRepository,
    private val exportRepository: ExportRepository,
) : ExportLogsToUriUseCase {

    override suspend fun invoke(lines: List<LogLine>, uri: Uri) {
        val content = lines.joinToString("\n") { line ->
            logLineFormatterRepository.formatForExport(
                logLine = line,
            )
        }
        exportRepository.writeContentToUri(uri, content)
    }
}
