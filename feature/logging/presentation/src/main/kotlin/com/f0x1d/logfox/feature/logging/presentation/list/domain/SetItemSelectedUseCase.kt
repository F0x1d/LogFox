package com.f0x1d.logfox.feature.logging.presentation.list.domain

import javax.inject.Inject

internal class SetItemSelectedUseCase @Inject constructor(
    private val repository: LogsSelectionRepository,
) {
    operator fun invoke(id: Long, selected: Boolean) {
        val current = repository.selectedIds.value
        repository.update(if (selected) current + id else current - id)
    }
}
