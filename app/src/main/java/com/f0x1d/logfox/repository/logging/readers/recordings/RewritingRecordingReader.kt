package com.f0x1d.logfox.repository.logging.readers.recordings

import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.utils.preferences.AppPreferences
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.LinkedList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RewritingRecordingReader @Inject constructor(
    private val appPreferences: AppPreferences
): RecordingWithFiltersReader() {

    // recordedLines are cleared, so manual dumpLines call may result in some lines be lost
    // This stateLines are not cleared and are just held at the size of recordedLines
    private val stateLines = LinkedList<LogLine>()
    private val stateLinesMutex = Mutex()

    init {
        recordedLinesSize = appPreferences.sessionCacheLinesCount
    }

    fun updateRecordedLinesSize(newSize: Int) {
        this.recordedLinesSize = newSize
    }

    override suspend fun readLine(line: LogLine) {
        super.readLine(line)

        stateLinesMutex.withLock {
            stateLines.add(line)
            while (stateLines.size >= recordedLinesSize)
                stateLines.removeFirst()
        }
    }

    override suspend fun writeToFile(content: String) = fileMutex.withLock {
        val myContent = stateLinesMutex.withLock {
            stateLines.joinToString(separator = "\n") {
                it.original
            }
        }

        recordingFile?.writeText(myContent)
    }
}