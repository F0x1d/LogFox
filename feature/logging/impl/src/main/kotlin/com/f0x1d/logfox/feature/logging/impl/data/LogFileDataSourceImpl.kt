package com.f0x1d.logfox.feature.logging.impl.data

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.f0x1d.logfox.core.di.IODispatcher
import com.f0x1d.logfox.feature.logging.api.data.LogLineParser
import com.f0x1d.logfox.feature.logging.api.model.LogLevel
import com.f0x1d.logfox.feature.logging.api.model.LogLine
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.LinkedList
import javax.inject.Inject

internal class LogFileDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val logLineParser: LogLineParser,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : LogFileDataSource {

    override fun readLogLines(uri: Uri, logsDisplayLimit: Int): Flow<List<LogLine>> = flow {
        val inputStream = context.contentResolver.openInputStream(uri)

        if (inputStream == null) {
            emit(emptyList())
            return@flow
        }

        inputStream.use { stream ->
            stream.bufferedReader().useLines { lines ->
                var id = STARTING_ID
                val logLines = LinkedList<LogLine>()

                for (line in lines) {
                    val logLine = logLineParser.parse(id, line) ?: createFallbackLogLine(id, line)

                    logLines.add(logLine)
                    if (logLines.size >= logsDisplayLimit) {
                        logLines.removeFirst()
                    }

                    id -= 1
                }

                emit(logLines.toList())
            }
        }
    }.catch { throwable ->
        throwable.printStackTrace()
        emit(emptyList())
    }.flowOn(ioDispatcher)

    override fun getFileName(uri: Uri): String? =
        DocumentFile.fromSingleUri(context, uri)?.name

    private fun createFallbackLogLine(id: Long, line: String) = LogLine(
        id = id,
        dateAndTime = System.currentTimeMillis(),
        uid = "",
        pid = "",
        tid = "",
        packageName = null,
        level = LogLevel.INFO,
        tag = "",
        content = line,
        originalContent = line,
    )

    private companion object {
        const val STARTING_ID = -1L
    }
}
