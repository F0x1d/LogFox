package com.f0x1d.logfox.repository.logging.readers.crashes

import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.database.entity.CrashType
import com.f0x1d.logfox.repository.logging.readers.crashes.base.BaseCrashDetector

class JNICrashDetector(collected: suspend (AppCrash, List<com.f0x1d.logfox.model.LogLine>) -> Unit): BaseCrashDetector(collected) {

    override val crashType = CrashType.JNI
    override val linesModifier: MutableList<com.f0x1d.logfox.model.LogLine>.() -> Unit = {
        removeAll { !it.debugTag }
    }

    private var firstLineTime = 0L

    override fun foundFirstLine(line: com.f0x1d.logfox.model.LogLine) = line.firstJNICrashLine.also {
        if (it) firstLineTime = System.currentTimeMillis()
    }

    override fun stillCollecting(line: com.f0x1d.logfox.model.LogLine): Boolean {
        if (line.firstJNICrashLine) return false

        return super.stillCollecting(line) || firstLineTime + 1000 > System.currentTimeMillis()
    }

    override fun packageFromCollected(lines: List<com.f0x1d.logfox.model.LogLine>): String {
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

    private val com.f0x1d.logfox.model.LogLine.firstJNICrashLine
        get() = debugTag && content == "*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***"

    private val com.f0x1d.logfox.model.LogLine.debugTag
        get() = tag.startsWith("DEBUG")
}
