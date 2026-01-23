package com.f0x1d.logfox.feature.crashes.impl.data.reader

import com.f0x1d.logfox.feature.crashes.impl.data.AppInfoDataSource
import com.f0x1d.logfox.feature.crashes.impl.data.CrashCollectorDataSource
import com.f0x1d.logfox.feature.crashes.impl.data.reader.base.BaseCrashDataSource
import com.f0x1d.logfox.feature.database.model.CrashType
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository
import javax.inject.Inject

internal class JNICrashDataSource @Inject constructor(
    appInfoDataSource: AppInfoDataSource,
    crashCollectorDataSource: CrashCollectorDataSource,
    private val logsSettingsRepository: LogsSettingsRepository,
) : BaseCrashDataSource(appInfoDataSource, crashCollectorDataSource) {

    override val crashType = CrashType.JNI

    private var collectionStartTime = 0L

    override fun isFirstLine(line: LogLine): Boolean {
        val isFirst = line.isDebugTag && line.content == CRASH_HEADER
        if (isFirst) {
            collectionStartTime = System.currentTimeMillis()
        }
        return isFirst
    }

    override fun filterLines(lines: MutableList<LogLine>) {
        lines.removeAll { !it.isDebugTag }
    }

    override fun shouldContinueCollecting(line: LogLine): Boolean {
        // Stop if a new crash starts
        if (line.isDebugTag && line.content == CRASH_HEADER) return false

        // Continue if within time window (JNI crashes can have interleaved output)
        val timeWindowMs = logsSettingsRepository.logsUpdateInterval().value + TIME_BUFFER_MS
        if (collectionStartTime + timeWindowMs > System.currentTimeMillis()) return true

        // Fall back to default pid/tid checking
        return super.shouldContinueCollecting(line)
    }

    override fun extractPackageName(lines: List<LogLine>): String {
        // Look for line with format: ">>> com.example.app <<<"
        for (line in lines) {
            val content = line.content
            val startMarker = content.indexOf(PACKAGE_START_MARKER)
            val endMarker = content.indexOf(PACKAGE_END_MARKER)

            if (startMarker != -1 && endMarker != -1 && startMarker < endMarker) {
                return content.substring(
                    startIndex = startMarker + PACKAGE_START_MARKER.length,
                    endIndex = endMarker,
                )
            }
        }
        return UNKNOWN_PACKAGE
    }

    private val LogLine.isDebugTag: Boolean
        get() = tag.startsWith(DEBUG_TAG_PREFIX)

    private companion object {
        const val DEBUG_TAG_PREFIX = "DEBUG"
        const val CRASH_HEADER = "*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***"
        const val PACKAGE_START_MARKER = ">>> "
        const val PACKAGE_END_MARKER = " <<<"
        const val TIME_BUFFER_MS = 1000L
        const val UNKNOWN_PACKAGE = "unknown"
    }
}
