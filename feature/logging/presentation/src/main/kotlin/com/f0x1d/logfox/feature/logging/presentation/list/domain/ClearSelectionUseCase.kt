package com.f0x1d.logfox.feature.logging.presentation.list.domain

import javax.inject.Inject

internal class ClearSelectionUseCase @Inject constructor(
    private val repository: LogsSelectionRepository,
) {
    operator fun invoke() {
        repository.update(emptySet())
    }
}
