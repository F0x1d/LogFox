package com.f0x1d.logfox.feature.crashes.impl.data.reader

import android.content.Context
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.database.entity.CrashType
import com.f0x1d.logfox.feature.crashes.impl.data.reader.base.BaseCrashDetector
import com.f0x1d.logfox.model.logline.LogLine

internal class ANRDetector(
    context: Context,
    collected: suspend (AppCrash, List<LogLine>) -> Unit
): BaseCrashDetector(context, collected) {

    override val crashType = CrashType.ANR
    override val commonTag = "ActivityManager"

    override fun foundFirstLine(line: LogLine) =
        line.tag == commonTag && line.content.startsWith("ANR in ")

    override fun packageFromCollected(lines: List<LogLine>) = runCatching {
        lines.first().run {
            content.indexOf(" (").let {
                if (it == -1)
                    content.substring(7)
                else
                    content.substring(7, it)
            }
        }
    }.getOrElse { "unknown" }
}
