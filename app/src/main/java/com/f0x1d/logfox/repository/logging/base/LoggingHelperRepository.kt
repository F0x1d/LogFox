package com.f0x1d.logfox.repository.logging.base

import com.f0x1d.logfox.repository.base.BaseRepository
import com.f0x1d.logfox.repository.logging.readers.base.BaseReader

abstract class LoggingHelperRepository: BaseRepository() {

    open val readers = emptyList<BaseReader>()

    abstract suspend fun setup()
    open suspend fun stop() {}
}