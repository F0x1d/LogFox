package com.f0x1d.logfox.extensions

import android.content.Context
import android.content.Intent
import androidx.documentfile.provider.DocumentFile
import com.f0x1d.logfox.extensions.logline.LogLine
import com.f0x1d.logfox.model.LogLine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

fun Intent?.readFileContentsAsFlow(context: Context) = flow {
    val intentData = this@readFileContentsAsFlow?.data

    if (intentData == null) {
        emit(null)
    } else {
        context.contentResolver.openInputStream(intentData)?.use {
            it.bufferedReader().useLines { lines ->
                val logLines = lines.mapIndexed { index, line ->
                    // reversing ids not to mess with device logs
                    val id = -index.toLong() - 1

                    LogLine(id, line, context.packageManager) ?: LogLine(
                        id = id,
                        content = line,
                        original = line
                    )
                }.toList()

                emit(logLines)
            }
        } ?: emit(null)
    }
}.flowOn(Dispatchers.IO)

fun Intent.readFileName(context: Context): String? {
    return DocumentFile.fromSingleUri(context, data ?: return null)?.name
}