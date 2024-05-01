package com.f0x1d.logfox.repository.logging.readers.crashes.base

class DefaultChecker {

    var wasBadLine = false

    fun collecting(firstLine: com.f0x1d.logfox.model.LogLine?, line: com.f0x1d.logfox.model.LogLine) = if (firstLine?.pid == line.pid && firstLine.tid == line.tid) {
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
