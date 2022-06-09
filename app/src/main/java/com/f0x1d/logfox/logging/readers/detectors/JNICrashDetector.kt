package com.f0x1d.logfox.logging.readers.detectors

import com.f0x1d.logfox.database.AppCrash
import com.f0x1d.logfox.database.CrashType
import com.f0x1d.logfox.logging.model.LogLine
import com.f0x1d.logfox.logging.readers.detectors.base.BaseCrashDetector

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

        return if (line.pid == collectedFirstLine?.pid && line.tid == collectedFirstLine?.tid)
            true
        else !wasBacktrace
    }

    override fun packageFromCollected(lines: List<LogLine>) = lines[5].content.substring(
        lines[5].content.indexOf(">>> "),
        lines[5].content.indexOf(" <<<")
    ).drop(4).also { wasBacktrace = false }

    private val LogLine.debugTag
        get() = tag.startsWith("DEBUG")
}