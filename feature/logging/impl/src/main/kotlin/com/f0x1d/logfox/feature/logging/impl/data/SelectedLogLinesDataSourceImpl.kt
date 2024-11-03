package com.f0x1d.logfox.feature.logging.impl.data

import com.f0x1d.logfox.arch.di.DefaultDispatcher
import com.f0x1d.logfox.feature.logging.api.data.SelectedLogLinesDataSource
import com.f0x1d.logfox.model.logline.LogLine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SelectedLogLinesDataSourceImpl @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) : SelectedLogLinesDataSource {
    private val mutableLines = MutableStateFlow(emptyList<LogLine>())

    override val selectedLines: Flow<List<LogLine>> get() = mutableLines.asStateFlow()

    override suspend fun updateSelectedLines(selectedLines: List<LogLine>) = withContext(defaultDispatcher) {
        mutableLines.update { selectedLines.toMutableList() }
    }
}
