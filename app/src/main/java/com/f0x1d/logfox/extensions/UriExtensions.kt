package com.f0x1d.logfox.extensions

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.f0x1d.logfox.extensions.context.appPreferences
import com.f0x1d.logfox.extensions.logline.LogLine
import com.f0x1d.logfox.model.LogLine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.LinkedList

fun Uri?.readFileContentsAsFlow(context: Context) = flow {
    val uri = this@readFileContentsAsFlow

    if (uri == null) {
        emit(emptyList())
    } else {
        val logsDisplayLimit = context.appPreferences.logsDisplayLimit

        context.contentResolver.openInputStream(uri)?.use {
            it.bufferedReader().useLines { lines ->
                var id = -1L
                val logLines = LinkedList<LogLine>()

                for (line in lines) {
                    val logLine = LogLine(id, line, context) ?: LogLine(
                        id = id,
                        content = line,
                        original = line
                    )

                    logLines.add(logLine)
                    if (logLines.size >= logsDisplayLimit)
                        logLines.removeFirst()

                    id -= 1
                }

                emit(logLines)
            }
        } ?: emit(emptyList())
    }
}.flowOn(Dispatchers.IO).catch {
    it.printStackTrace()
    emit(emptyList())
}

fun Uri.readFileName(context: Context) = DocumentFile.fromSingleUri(context, this)?.name