package com.f0x1d.logfox.feature.logging.impl.data

import com.f0x1d.logfox.core.di.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SearchDataSourceImpl @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) : SearchDataSource {
    private val mutableQuery = MutableStateFlow<String?>(null)
    private val mutableCaseSensitive = MutableStateFlow(false)

    override val query: Flow<String?> get() = mutableQuery.asStateFlow()
    override val caseSensitive: Flow<Boolean> get() = mutableCaseSensitive.asStateFlow()

    override suspend fun updateQuery(query: String?) = withContext(defaultDispatcher) {
        mutableQuery.update { query }
    }

    override suspend fun updateCaseSensitive(caseSensitive: Boolean) = withContext(defaultDispatcher) {
        mutableCaseSensitive.update { caseSensitive }
    }
}
