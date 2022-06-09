package com.f0x1d.logfox.logging.readers.detectors

import com.f0x1d.logfox.database.AppCrash
import com.f0x1d.logfox.database.CrashType
import com.f0x1d.logfox.logging.model.LogLine
import com.f0x1d.logfox.logging.readers.detectors.base.BaseCrashDetector

class JavaCrashDetector(collected: suspend (AppCrash) -> Unit): BaseCrashDetector(collected) {

    override val crashType = CrashType.JAVA

    override fun foundFirstLine(line: LogLine) = line.tag == "AndroidRuntime" && line.content.startsWith("FATAL EXCEPTION: ")

    override fun stillCollecting(line: LogLine) = line.pid == collectedFirstLine?.pid && line.tid == collectedFirstLine?.tid

    override fun packageFromCollected(lines: List<LogLine>) = lines[1].content.substring(
        9,
        lines[1].content.indexOf(",")
    )
}