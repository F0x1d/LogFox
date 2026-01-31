package com.f0x1d.logfox.feature.logging.impl.data

import kotlinx.coroutines.flow.StateFlow

internal interface SearchDataSource {
    val query: StateFlow<String?>
    val caseSensitive: StateFlow<Boolean>

    suspend fun updateQuery(query: String?)
    suspend fun updateCaseSensitive(caseSensitive: Boolean)
}
