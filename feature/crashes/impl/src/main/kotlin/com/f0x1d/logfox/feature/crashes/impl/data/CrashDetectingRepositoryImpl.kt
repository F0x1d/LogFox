package com.f0x1d.logfox.feature.crashes.impl.data

import com.f0x1d.logfox.feature.crashes.api.data.CrashDetectingRepository
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import javax.inject.Inject

internal class CrashDetectingRepositoryImpl @Inject constructor(
    private val crashDataSources: Set<@JvmSuppressWildcards CrashDataSource>,
) : CrashDetectingRepository {

    override suspend fun processLogLine(line: LogLine) {
        crashDataSources.forEach { dataSource ->
            dataSource.process(line)
        }
    }
}
