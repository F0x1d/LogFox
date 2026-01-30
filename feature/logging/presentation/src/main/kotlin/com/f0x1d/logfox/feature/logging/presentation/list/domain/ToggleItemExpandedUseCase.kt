package com.f0x1d.logfox.feature.logging.presentation.list.domain

import javax.inject.Inject

internal class ToggleItemExpandedUseCase @Inject constructor(
    private val repository: LogsExpandedRepository,
) {
    operator fun invoke(id: Long, defaultExpanded: Boolean) {
        val current = repository.expandedOverrides.value
        val wasExpanded = current.getOrElse(id) { defaultExpanded }
        repository.update(current + (id to !wasExpanded))
    }
}
