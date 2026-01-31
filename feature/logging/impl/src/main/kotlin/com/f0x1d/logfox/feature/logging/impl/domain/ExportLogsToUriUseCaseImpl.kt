package com.f0x1d.logfox.feature.logging.impl.domain

import android.net.Uri
import com.f0x1d.logfox.feature.datetime.api.DateTimeFormatter
import com.f0x1d.logfox.feature.logging.api.data.LogLineFormatterRepository
import com.f0x1d.logfox.feature.logging.api.domain.ExportLogsToUriUseCase
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.logging.impl.data.LogExportDataSource
import javax.inject.Inject

internal class ExportLogsToUriUseCaseImpl @Inject constructor(
    private val logLineFormatterRepository: LogLineFormatterRepository,
    private val dateTimeFormatter: DateTimeFormatter,
    private val logExportDataSource: LogExportDataSource,
) : ExportLogsToUriUseCase {

    override suspend fun invoke(lines: List<LogLine>, uri: Uri) {
        val content = lines.joinToString("\n") { line ->
            logLineFormatterRepository.format(
                logLine = line,
                formatDate = dateTimeFormatter::formatDate,
                formatTime = dateTimeFormatter::formatTime,
            )
        }
        logExportDataSource.exportToUri(content, uri)
    }
}
