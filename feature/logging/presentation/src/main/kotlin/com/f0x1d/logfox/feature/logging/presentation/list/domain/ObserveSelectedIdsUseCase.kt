package com.f0x1d.logfox.feature.logging.presentation.list.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class ObserveSelectedIdsUseCase @Inject constructor(
    private val repository: LogsSelectionRepository,
) {
    operator fun invoke(): Flow<Set<Long>> = repository.selectedIds
}
