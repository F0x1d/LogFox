package com.f0x1d.logfox.feature.logging.api.domain

import com.f0x1d.logfox.feature.logging.api.model.LogLine

interface GetLogLinesByIdsUseCase {
    operator fun invoke(ids: Set<Long>): List<LogLine>
}
