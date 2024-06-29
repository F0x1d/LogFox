package com.f0x1d.logfox.feature.recordings.core.controller.reader

import com.f0x1d.logfox.datetime.DateTimeFormatter
import com.f0x1d.logfox.model.logline.LogLine
import com.f0x1d.logfox.preferences.shared.AppPreferences
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.LinkedList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RewritingRecordingReader @Inject constructor(
    appPreferences: AppPreferences,
    dateTimeFormatter: DateTimeFormatter,
): RecordingWithFiltersReader(appPreferences, dateTimeFormatter) {

    // recordedLines are cleared, so manual dumpLines call may result in some lines be lost
    // This stateLines are not cleared and are just held at the size of recordedLines
    private val stateLines = LinkedList<LogLine>()
    private val stateLinesMutex = Mutex()

    init {
        // TODO: return it
        // recordedLinesSize = appPreferences.sessionCacheLinesCount
    }

    fun updateRecordedLinesSize(newSize: Int) {
        this.recordedLinesSize = newSize
    }

    override suspend fun invoke(line: LogLine) {
        super.invoke(line)

        stateLinesMutex.withLock {
            stateLines.add(line)
            while (stateLines.size >= recordedLinesSize)
                stateLines.removeFirst()
        }
    }

    override suspend fun writeToFile(content: String) = fileMutex.withLock {
        val myContent = stateLinesMutex.withLock {
            /*stateLines.joinToString(separator = "\n") {
                it.original
            }*/
            " TODO: return it :) "
        }

        recordingFile?.writeText(myContent)
    }
}
