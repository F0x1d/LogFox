package com.f0x1d.logfox.feature.logging.impl.data

import kotlinx.coroutines.flow.Flow

internal interface QueryDataSource {
    val query: Flow<String?>

    suspend fun updateQuery(query: String?)
}
