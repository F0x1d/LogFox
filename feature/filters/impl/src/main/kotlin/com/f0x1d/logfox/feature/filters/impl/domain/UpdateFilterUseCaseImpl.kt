package com.f0x1d.logfox.feature.filters.impl.domain

import com.f0x1d.logfox.feature.database.model.UserFilter
import com.f0x1d.logfox.feature.filters.api.data.FiltersRepository
import com.f0x1d.logfox.feature.filters.api.domain.UpdateFilterUseCase
import com.f0x1d.logfox.feature.logging.api.model.LogLevel
import javax.inject.Inject

internal class UpdateFilterUseCaseImpl @Inject constructor(
    private val filtersRepository: FiltersRepository,
) : UpdateFilterUseCase {
    override suspend fun invoke(
        userFilter: UserFilter,
        including: Boolean,
        enabledLogLevels: List<LogLevel>,
        uid: String?,
        pid: String?,
        tid: String?,
        packageName: String?,
        tag: String?,
        content: String?,
    ) = filtersRepository.update(
        userFilter = userFilter,
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
