package com.f0x1d.logfox.feature.filters.impl.domain

import com.f0x1d.logfox.feature.filters.api.data.FiltersRepository
import com.f0x1d.logfox.feature.filters.api.domain.CreateFilterUseCase
import com.f0x1d.logfox.feature.logging.api.model.LogLevel
import javax.inject.Inject

internal class CreateFilterUseCaseImpl @Inject constructor(
    private val filtersRepository: FiltersRepository,
) : CreateFilterUseCase {
    override suspend fun invoke(
        including: Boolean,
        enabledLogLevels: List<LogLevel>,
        uid: String?,
        pid: String?,
        tid: String?,
        packageName: String?,
        tag: String?,
        content: String?,
    ) = filtersRepository.create(
        including = including,
        enabledLogLevels = enabledLogLevels,
        uid = uid,
        pid = pid,
        tid = tid,
        packageName = packageName,
        tag = tag,
        content = content,
    )
}
