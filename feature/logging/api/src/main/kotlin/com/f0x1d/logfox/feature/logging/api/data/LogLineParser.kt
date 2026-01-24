package com.f0x1d.logfox.feature.logging.api.data

import com.f0x1d.logfox.feature.logging.api.model.LogLine

interface LogLineParser {

    fun parse(id: Long, line: String): LogLine?
}
