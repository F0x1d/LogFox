package com.f0x1d.logfox.feature.crashes.impl.data.reader.base

import com.f0x1d.logfox.feature.crashes.api.model.AppCrash
import com.f0x1d.logfox.feature.crashes.api.model.CrashType
import com.f0x1d.logfox.feature.crashes.impl.data.AppInfoDataSource
import com.f0x1d.logfox.feature.crashes.impl.data.CrashCollectorDataSource
import com.f0x1d.logfox.feature.crashes.impl.data.CrashDataSource
import com.f0x1d.logfox.feature.logging.api.model.LogLine

internal abstract class BaseCrashDataSource(
    private val appInfoDataSource: AppInfoDataSource,
    private val crashCollectorDataSource: CrashCollectorDataSource,
) : CrashDataSource {

    protected abstract val crashType: CrashType
    protected open val timeBufferMs: Long = TIME_BUFFER_MS

    private var collecting = false
    private var collectionStartTime = 0L
    private var firstLine: LogLine? = null
    private val collectedLines = mutableListOf<LogLine>()

    abstract fun isFirstLine(line: LogLine): Boolean

    abstract fun extractPackageName(lines: List<LogLine>): String

    open fun filterLines(lines: MutableList<LogLine>) = Unit

    open fun shouldContinueCollecting(line: LogLine): Boolean {
        // Continue if within time window (crashes can have interleaved output)
        if (collectionStartTime + timeBufferMs > System.currentTimeMillis()) return true

        // Fall back to PID checking
        val first = firstLine ?: return false
        return first.pid == line.pid
    }

    override suspend fun process(line: LogLine) {
        if (!collecting) {
            tryStartCollecting(line)
            return
        }

        if (shouldContinueCollecting(line)) {
            collectedLines.add(line)
        } else {
            emitCrashIfValid()
            tryStartCollecting(line)
        }
    }

    private fun tryStartCollecting(line: LogLine) {
        if (isFirstLine(line)) {
            collecting = true
            collectionStartTime = System.currentTimeMillis()
            firstLine = line
            collectedLines.clear()
            collectedLines.add(line)
        }
    }

    private suspend fun emitCrashIfValid() {
        filterLines(collectedLines)

        if (collectedLines.isNotEmpty()) {
            val crash = createAppCrash(
                packageName = extractPackageName(collectedLines),
                lines = collectedLines,
            )
            crashCollectorDataSource.collectCrash(crash, collectedLines.toList())
        }

        collecting = false
        firstLine = null
    }

    private fun createAppCrash(
        packageName: String,
        lines: List<LogLine>,
    ) = AppCrash(
        appName = appInfoDataSource.getAppName(packageName),
        packageName = packageName,
        crashType = crashType,
        dateAndTime = lines.firstOrNull()?.dateAndTime ?: System.currentTimeMillis(),
    )

    private companion object {
        const val TIME_BUFFER_MS = 1000L
    }
}
