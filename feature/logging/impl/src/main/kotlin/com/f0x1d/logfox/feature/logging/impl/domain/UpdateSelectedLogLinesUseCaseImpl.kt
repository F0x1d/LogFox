package com.f0x1d.logfox.feature.logging.impl.domain

import com.f0x1d.logfox.feature.logging.api.model.LogLine
import com.f0x1d.logfox.feature.logging.api.domain.UpdateSelectedLogLinesUseCase
import com.f0x1d.logfox.feature.logging.impl.data.SelectedLogLinesDataSource
import javax.inject.Inject

internal class UpdateSelectedLogLinesUseCaseImpl @Inject constructor(
    private val selectedLogLinesDataSource: SelectedLogLinesDataSource,
) : UpdateSelectedLogLinesUseCase {
    override suspend fun invoke(selectedLines: List<LogLine>) {
        selectedLogLinesDataSource.updateSelectedLines(selectedLines)
    }
}
