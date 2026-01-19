package com.f0x1d.logfox.feature.crashes.impl.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

internal class CrashesSearchLocalDataSourceImpl @Inject constructor() : CrashesSearchLocalDataSource {

    private val _queryFlow = MutableStateFlow("")

    override val queryFlow: Flow<String> = _queryFlow.asStateFlow()

    override fun updateQuery(query: String) {
        _queryFlow.value = query
    }
}
