package com.f0x1d.logfox.feature.crashes.core.repository.reader

import android.content.Context
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.database.entity.CrashType
import com.f0x1d.logfox.feature.crashes.core.repository.reader.base.BaseCrashDetector
import com.f0x1d.logfox.model.logline.LogLine
import com.f0x1d.logfox.preferences.shared.appPreferences

internal class JNICrashDetector(
    context: Context,
    collected: suspend (AppCrash, List<LogLine>) -> Unit
): BaseCrashDetector(context, collected) {

    override val crashType = CrashType.JNI
    override val linesModifier: MutableList<LogLine>.() -> Unit = {
        removeAll { !it.debugTag }
    }

    private var firstLineTime = 0L

    private val appPreferences by lazy { context.appPreferences }

    override fun foundFirstLine(line: LogLine) = line.firstJNICrashLine.also {
        if (it) firstLineTime = System.currentTimeMillis()
    }

    override fun stillCollecting(line: LogLine): Boolean {
        if (line.firstJNICrashLine) return false

        return super.stillCollecting(line) ||
                // + 1000 for case logsUpdateInterval is really small
                firstLineTime + appPreferences.logsUpdateInterval + 1000 > System.currentTimeMillis()
    }

    override fun packageFromCollected(lines: List<LogLine>): String {
        lines.forEach {
            if (it.content.contains(">>> ") && it.content.contains(" <<<")) {
                return it.content.substring(
                    it.content.indexOf(">>> "),
                    it.content.indexOf(" <<<")
                ).drop(4)
            }
        }

        return "???"
    }

    private val LogLine.firstJNICrashLine
        get() = debugTag && content == "*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***"

    private val LogLine.debugTag
        get() = tag.startsWith("DEBUG")
}
