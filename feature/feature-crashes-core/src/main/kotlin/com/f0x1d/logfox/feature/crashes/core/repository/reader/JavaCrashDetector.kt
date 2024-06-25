package com.f0x1d.logfox.feature.crashes.core.repository.reader

import android.content.Context
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.database.entity.CrashType
import com.f0x1d.logfox.model.logline.LogLine
import com.f0x1d.logfox.feature.crashes.core.repository.reader.base.BaseCrashDetector

internal class JavaCrashDetector(
    context: Context,
    collected: suspend (AppCrash, List<LogLine>) -> Unit,
): BaseCrashDetector(context, collected) {

    override val crashType = CrashType.JAVA
    override val commonTag = "AndroidRuntime"

    override fun foundFirstLine(line: LogLine) =
        line.tag == commonTag && line.content.startsWith("FATAL EXCEPTION: ")

    override fun packageFromCollected(lines: List<LogLine>) = lines[1].content.substring(
        startIndex = 9,
        endIndex = lines[1].content.indexOf(","),
    )
}
