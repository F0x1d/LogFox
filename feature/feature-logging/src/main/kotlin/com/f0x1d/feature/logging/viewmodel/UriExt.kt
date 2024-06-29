package com.f0x1d.feature.logging.viewmodel

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.f0x1d.logfox.model.logline.LogLine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.LinkedList

internal fun Uri?.readFileContentsAsFlow(
    context: Context,
    logsDisplayLimit: Int,
) = flow {
    val inputStream = this@readFileContentsAsFlow
        ?.let(context.contentResolver::openInputStream)

    if (inputStream == null) {
        emit(emptyList())
    } else {
        inputStream.use {
            it.bufferedReader().useLines { lines ->
                var id = -1L
                val logLines = LinkedList<LogLine>()

                for (line in lines) {
                    val logLine = LogLine(id, line, context) ?: LogLine(
                        id = id,
                        content = line,
                        originalContent = line,
                    )

                    logLines.add(logLine)
                    if (logLines.size >= logsDisplayLimit)
                        logLines.removeFirst()

                    id -= 1
                }

                emit(logLines)
            }
        }
    }
}.catch {
    it.printStackTrace()
    emit(emptyList())
}.flowOn(Dispatchers.IO)

internal fun Uri.readFileName(context: Context) = DocumentFile.fromSingleUri(context, this)?.name
