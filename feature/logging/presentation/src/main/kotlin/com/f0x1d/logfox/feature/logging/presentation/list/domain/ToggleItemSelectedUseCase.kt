package com.f0x1d.logfox.feature.logging.presentation.list.domain

import javax.inject.Inject

internal class ToggleItemSelectedUseCase @Inject constructor(
    private val repository: LogsSelectionRepository,
) {
    operator fun invoke(id: Long) {
        val current = repository.selectedIds.value
        repository.update(if (id in current) current - id else current + id)
    }
}
