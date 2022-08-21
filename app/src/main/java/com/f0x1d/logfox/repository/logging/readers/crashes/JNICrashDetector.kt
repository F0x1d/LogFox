package com.f0x1d.logfox.repository.logging.readers.crashes

import com.f0x1d.logfox.database.AppCrash
import com.f0x1d.logfox.database.CrashType
import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.repository.logging.readers.crashes.base.BaseCrashDetector

class JNICrashDetector(collected: suspend (AppCrash) -> Unit): BaseCrashDetector(collected) {

    override val crashType = CrashType.JNI
    override val linesModifier: MutableList<LogLine>.() -> Unit = {
        removeAll { !it.debugTag }
    }

    private var wasBacktrace = false

    override fun foundFirstLine(line: LogLine) = line.debugTag && line.content == "*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***"

    override fun stillCollecting(line: LogLine): Boolean {
        if (line.debugTag && line.content == "backtrace:")
            wasBacktrace = true

        return if (super.stillCollecting(line))
            true
        else !wasBacktrace
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

    private val LogLine.debugTag
        get() = tag.startsWith("DEBUG")
}