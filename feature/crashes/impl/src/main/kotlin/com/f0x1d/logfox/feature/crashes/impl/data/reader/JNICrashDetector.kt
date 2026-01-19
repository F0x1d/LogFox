package com.f0x1d.logfox.feature.crashes.impl.data.reader

import android.content.Context
import com.f0x1d.logfox.feature.crashes.impl.data.reader.base.BaseCrashDetector
import com.f0x1d.logfox.feature.database.model.AppCrash
import com.f0x1d.logfox.feature.database.model.CrashType
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.preferences.data.LogsSettingsRepository

internal class JNICrashDetector(
    context: Context,
    private val logsSettingsRepository: LogsSettingsRepository,
    collected: suspend (AppCrash, List<LogLine>) -> Unit,
) : BaseCrashDetector(context, collected) {
    override val crashType = CrashType.JNI
    override val linesModifier: MutableList<LogLine>.() -> Unit = {
        removeAll { !it.debugTag }
    }

    private var firstLineTime = 0L

    override fun foundFirstLine(line: LogLine) =
        line.firstJNICrashLine.also {
            if (it) firstLineTime = System.currentTimeMillis()
        }

    override fun stillCollecting(line: LogLine): Boolean {
        if (line.firstJNICrashLine) return false

        return super.stillCollecting(line) ||
            // + 1000 for case logsUpdateInterval is really small
            firstLineTime + logsSettingsRepository.logsUpdateInterval().value + 1000 > System.currentTimeMillis()
    }

    override fun packageFromCollected(lines: List<LogLine>): String =
        runCatching {
            lines.forEach {
                if (it.content.contains(">>> ") && it.content.contains(" <<<")) {
                    return@runCatching it.content
                        .substring(
                            it.content.indexOf(">>> "),
                            it.content.indexOf(" <<<"),
                        ).drop(4)
                }
            }

            "unknown"
        }.getOrElse { "unknown" }

    private val LogLine.firstJNICrashLine
        get() = debugTag && content == "*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***"

    private val LogLine.debugTag
        get() = tag.startsWith("DEBUG")
}
