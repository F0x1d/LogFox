package com.f0x1d.logfox.feature.logging.impl.domain

import android.net.Uri
import com.f0x1d.logfox.feature.logging.api.domain.ReadLogFileUseCase
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.logging.impl.data.LogFileDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class ReadLogFileUseCaseImpl @Inject constructor(
    private val logFileDataSource: LogFileDataSource,
) : ReadLogFileUseCase {

    override fun invoke(uri: Uri, logsDisplayLimit: Int): Flow<List<LogLine>> =
        logFileDataSource.readLogLines(uri, logsDisplayLimit)
}
