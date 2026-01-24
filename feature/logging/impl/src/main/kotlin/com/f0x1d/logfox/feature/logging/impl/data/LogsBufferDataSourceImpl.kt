package com.f0x1d.logfox.feature.logging.impl.data

import com.f0x1d.logfox.core.di.DefaultDispatcher
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.LinkedList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LogsBufferDataSourceImpl @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) : LogsBufferDataSource {

    private val buffer = LinkedList<LogLine>()
    private val mutex = Mutex()

    override suspend fun add(logLine: LogLine, limit: Int) = withContext(defaultDispatcher) {
        mutex.withLock {
            buffer.add(logLine)
            while (buffer.size > limit) {
                buffer.removeFirst()
            }
        }
    }

    override suspend fun getAll(): List<LogLine> = mutex.withLock { buffer.toList() }

    override suspend fun clear() = withContext(defaultDispatcher) {
        mutex.withLock {
            buffer.clear()
        }
    }

    override fun lastId(): Long = buffer.lastOrNull()?.id ?: 0L
}
