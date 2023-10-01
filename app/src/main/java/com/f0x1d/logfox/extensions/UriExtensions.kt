package com.f0x1d.logfox.extensions

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.f0x1d.logfox.extensions.logline.LogLine
import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.utils.preferences.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

fun Uri?.readFileContentsAsFlow(context: Context, appPreferences: AppPreferences) = flow<List<LogLine>> {
    val uri = this@readFileContentsAsFlow

    if (uri == null) {
        emit(emptyList())
    } else {
        val logsDisplayLimit = appPreferences.logsDisplayLimit

        context.contentResolver.openInputStream(uri)?.use {
            it.bufferedReader().useLines { lines ->
                var id = -1L
                val logLines = mutableListOf<LogLine>()

                for (line in lines) {
                    val logLine = LogLine(id, line, context.packageManager) ?: LogLine(
                        id = id,
                        content = line,
                        original = line
                    )

                    logLines.add(logLine)
                    if (logLines.size >= logsDisplayLimit)
                        break

                    id -= 1
                }

                emit(logLines)
            }
        } ?: emit(emptyList())
    }
}.flowOn(Dispatchers.IO).catch {
    emit(emptyList())
}

fun Uri.readFileName(context: Context) = DocumentFile.fromSingleUri(context, this)?.name