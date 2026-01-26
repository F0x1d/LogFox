package com.f0x1d.logfox.feature.logging.impl.data

import kotlinx.coroutines.flow.Flow

internal interface SearchDataSource {
    val query: Flow<String?>
    val caseSensitive: Flow<Boolean>

    suspend fun updateQuery(query: String?)
    suspend fun updateCaseSensitive(caseSensitive: Boolean)
}
