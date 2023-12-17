package com.f0x1d.logfox.repository.logging.readers.crashes

import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.database.entity.CrashType
import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.repository.logging.readers.crashes.base.BaseCrashDetector

class JavaCrashDetector(collected: suspend (AppCrash, List<LogLine>) -> Unit): BaseCrashDetector(collected) {

    override val crashType = CrashType.JAVA
    override val commonTag = "AndroidRuntime"

    override fun foundFirstLine(line: LogLine) = line.tag == commonTag && line.content.startsWith("FATAL EXCEPTION: ")

    override fun packageFromCollected(lines: List<LogLine>) = lines[1].content.substring(
        9,
        lines[1].content.indexOf(",")
    )
}