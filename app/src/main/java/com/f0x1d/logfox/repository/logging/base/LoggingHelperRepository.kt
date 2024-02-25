package com.f0x1d.logfox.repository.logging.base

import com.f0x1d.logfox.repository.base.BaseRepository
import com.f0x1d.logfox.repository.logging.readers.base.LogsReader

abstract class LoggingHelperRepository: BaseRepository() {

    open val readers = emptyList<LogsReader>()

    open suspend fun setup() = Unit
    open suspend fun stop() = Unit
}