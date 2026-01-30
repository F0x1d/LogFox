package com.f0x1d.logfox.feature.logging.presentation.list.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class ObserveExpandedOverridesUseCase @Inject constructor(
    private val repository: LogsExpandedRepository,
) {
    operator fun invoke(): Flow<Map<Long, Boolean>> = repository.expandedOverrides
}
