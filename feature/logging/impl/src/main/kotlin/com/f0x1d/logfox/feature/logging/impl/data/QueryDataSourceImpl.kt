package com.f0x1d.logfox.feature.logging.impl.data

import com.f0x1d.logfox.arch.di.DefaultDispatcher
import com.f0x1d.logfox.feature.logging.api.data.QueryDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class QueryDataSourceImpl @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) : QueryDataSource {
    private val mutableQuery = MutableStateFlow<String?>(null)

    override val query: Flow<String?> get() = mutableQuery.asStateFlow()

    override suspend fun updateQuery(query: String?) = withContext(defaultDispatcher) {
        mutableQuery.update { query }
    }
}
