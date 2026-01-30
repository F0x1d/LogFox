package com.f0x1d.logfox.feature.logging.presentation.list.domain

import javax.inject.Inject

internal class SelectAllItemsUseCase @Inject constructor(
    private val repository: LogsSelectionRepository,
) {
    operator fun invoke(allIds: Set<Long>) {
        val current = repository.selectedIds.value
        repository.update(if (current == allIds) emptySet() else allIds)
    }
}
