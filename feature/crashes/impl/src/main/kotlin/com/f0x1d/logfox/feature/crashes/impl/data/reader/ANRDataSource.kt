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
internal class ANRDataSource @Inject constructor(
    appInfoDataSource: AppInfoDataSource,
    crashCollectorDataSource: CrashCollectorDataSource,
    logsSettingsRepository: LogsSettingsRepository,
) : BaseCrashDataSource(appInfoDataSource, crashCollectorDataSource, logsSettingsRepository) {

    override val crashType = CrashType.ANR

    override fun isFirstLine(line: LogLine): Boolean =
        line.tag == TAG && line.content.startsWith(ANR_PREFIX)

    override fun filterLines(lines: MutableList<LogLine>) {
        lines.removeAll { it.tag != TAG }
    }

    override fun extractPackageName(lines: List<LogLine>): String = runCatching {
        // First line format: "ANR in com.example.app" or "ANR in com.example.app (reason)"
        val content = lines.firstOrNull()?.content ?: return@runCatching UNKNOWN_PACKAGE
        val parenthesisIndex = content.indexOf(" (")

        if (parenthesisIndex == -1) {
            content.substring(ANR_PREFIX_LENGTH)
        } else {
            content.substring(ANR_PREFIX_LENGTH, parenthesisIndex)
        }
    }.getOrDefault(UNKNOWN_PACKAGE)

    private companion object {
        const val TAG = "ActivityManager"
        const val ANR_PREFIX = "ANR in "
        const val ANR_PREFIX_LENGTH = 7 // "ANR in ".length
        const val UNKNOWN_PACKAGE = "unknown"
    }
}
