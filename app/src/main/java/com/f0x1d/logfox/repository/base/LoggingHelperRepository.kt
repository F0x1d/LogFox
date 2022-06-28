package com.f0x1d.logfox.repository.base

import com.f0x1d.logfox.repository.readers.base.BaseReader

abstract class LoggingHelperRepository: BaseRepository() {

    open val readers = emptyList<BaseReader>()

    abstract suspend fun setup()
    open suspend fun stop() {}
}