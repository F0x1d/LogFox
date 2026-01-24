package com.f0x1d.logfox.feature.crashes.impl.data

import kotlinx.coroutines.flow.Flow

internal interface CrashesSearchLocalDataSource {
    val queryFlow: Flow<String>
    fun updateQuery(query: String)
}
