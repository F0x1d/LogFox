package com.f0x1d.logfox.repository.logging.readers.crashes

import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.database.entity.CrashType
import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.repository.logging.readers.crashes.base.BaseCrashDetector

class ANRDetector(collected: suspend (AppCrash, List<LogLine>) -> Unit): BaseCrashDetector(collected) {

    override val crashType = CrashType.ANR
    override val commonTag = "ActivityManager"

    override fun foundFirstLine(line: LogLine) = line.tag == commonTag && line.content.startsWith("ANR in ")

    override fun packageFromCollected(lines: List<LogLine>) = lines.first().run {
        content.indexOf(" (").let {
            if (it == -1)
                content.substring(7)
            else
                content.substring(7, it)
        }
    }
}