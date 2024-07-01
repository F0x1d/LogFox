package com.f0x1d.logfox.feature.crashes.core.repository.reader.base

import com.f0x1d.logfox.model.logline.LogLine

internal class DefaultChecker {

    var wasBadLine = false

    fun collecting(
        firstLine: LogLine?,
        line: LogLine,
    ) = if (firstLine?.pid == line.pid && firstLine.tid == line.tid) {
        wasBadLine = false
        true
    } else if (wasBadLine) {
        wasBadLine = false
        false
    } else {
        wasBadLine = true
        true
    }
}
