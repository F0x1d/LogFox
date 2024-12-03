package com.f0x1d.logfox.feature.logging.api.data

import kotlinx.coroutines.flow.Flow

interface QueryDataSource {
    val query: Flow<String?>

    suspend fun updateQuery(query: String?)
}
