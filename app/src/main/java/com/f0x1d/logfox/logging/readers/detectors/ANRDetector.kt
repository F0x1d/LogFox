package com.f0x1d.logfox.logging.readers.detectors

import com.f0x1d.logfox.database.AppCrash
import com.f0x1d.logfox.database.CrashType
import com.f0x1d.logfox.logging.model.LogLine
import com.f0x1d.logfox.logging.readers.detectors.base.BaseCrashDetector

class ANRDetector(collected: suspend (AppCrash) -> Unit): BaseCrashDetector(collected) {

    override val crashType = CrashType.ANR

    override fun foundFirstLine(line: LogLine) = line.tag == "ActivityManager" && line.content.startsWith("ANR in ")

    override fun stillCollecting(line: LogLine) = line.pid == collectedFirstLine?.pid && line.tid == collectedFirstLine?.tid

    override fun packageFromCollected(lines: List<LogLine>) = lines[0].content.substring(
        7,
        lines[0].content.indexOf(" (")
    )
}