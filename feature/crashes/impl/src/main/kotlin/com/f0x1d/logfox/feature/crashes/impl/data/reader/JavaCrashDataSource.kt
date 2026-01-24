package com.f0x1d.logfox.feature.crashes.impl.data.reader

import com.f0x1d.logfox.feature.crashes.impl.data.AppInfoDataSource
import com.f0x1d.logfox.feature.crashes.impl.data.CrashCollectorDataSource
import com.f0x1d.logfox.feature.crashes.impl.data.reader.base.BaseCrashDataSource
import com.f0x1d.logfox.feature.database.model.CrashType
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class JavaCrashDataSource @Inject constructor(
    appInfoDataSource: AppInfoDataSource,
    crashCollectorDataSource: CrashCollectorDataSource,
    logsSettingsRepository: LogsSettingsRepository,
) : BaseCrashDataSource(appInfoDataSource, crashCollectorDataSource, logsSettingsRepository) {

    override val crashType = CrashType.JAVA

    override fun isFirstLine(line: LogLine): Boolean =
        line.tag == TAG && line.content.startsWith(FATAL_EXCEPTION_PREFIX)

    override fun filterLines(lines: MutableList<LogLine>) {
        lines.removeAll { it.tag != TAG }
    }

    override fun extractPackageName(lines: List<LogLine>): String = runCatching {
        // Second line format: "Process: com.example.app, PID: 12345"
        val processLine = lines.getOrNull(1)?.content ?: return@runCatching UNKNOWN_PACKAGE
        val commaIndex = processLine.indexOf(',')

        if (commaIndex == -1) return@runCatching UNKNOWN_PACKAGE

        processLine.substring(
            startIndex = PROCESS_PREFIX_LENGTH,
            endIndex = commaIndex,
        )
    }.getOrDefault(UNKNOWN_PACKAGE)

    private companion object {
        const val TAG = "AndroidRuntime"
        const val FATAL_EXCEPTION_PREFIX = "FATAL EXCEPTION: "
        const val PROCESS_PREFIX_LENGTH = 9 // "Process: ".length
        const val UNKNOWN_PACKAGE = "unknown"
    }
}
