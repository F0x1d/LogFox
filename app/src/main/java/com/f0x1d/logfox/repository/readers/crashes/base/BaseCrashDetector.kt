package com.f0x1d.logfox.repository.readers.crashes.base

import com.f0x1d.logfox.LogFoxApp
import com.f0x1d.logfox.database.AppCrash
import com.f0x1d.logfox.database.CrashType
import com.f0x1d.logfox.model.LogLine
import com.f0x1d.logfox.repository.readers.base.BaseReader

abstract class BaseCrashDetector(private val collected: suspend (AppCrash) -> Unit): BaseReader {

    protected abstract val crashType: CrashType
    protected open val commonTag: String? = null
    protected open val linesModifier: MutableList<LogLine>.() -> Unit = {
        commonTag?.apply {
            removeAll { it.tag != this }
        }
    }

    private var collecting = false
    protected var collectedFirstLine: LogLine? = null
    private val collectedLines = mutableListOf<LogLine>()
    private val defaultChecker = DefaultChecker()

    abstract fun foundFirstLine(line: LogLine): Boolean
    open fun stillCollecting(line: LogLine) = defaultChecker.collecting(collectedFirstLine, line)
    abstract fun packageFromCollected(lines: List<LogLine>): String

    override suspend fun readLine(line: LogLine) {
        if (!collecting) {
            checkFirstLine(line)
            return
        }

        if (stillCollecting(line)) {
            collectedLines.add(line)
        } else {
            linesModifier.invoke(collectedLines)
            collected.invoke(logsToAppCrash(packageFromCollected(collectedLines), crashType, collectedLines))

            collecting = false
            collectedFirstLine = null

            checkFirstLine(line)
        }
    }

    private fun checkFirstLine(line: LogLine) {
        if (foundFirstLine(line)) {
            collecting = true

            collectedFirstLine = line
            collectedLines.clear()
            collectedLines.add(line)
        }
    }

    private fun logsToAppCrash(crashedAppPackageName: String, crashType: CrashType, lines: List<LogLine>) = LogFoxApp.instance.run {
        try {
            val appInfo = packageManager.getPackageInfo(crashedAppPackageName, 0).applicationInfo
            AppCrash(
                packageManager.getApplicationLabel(appInfo).toString(),
                crashedAppPackageName,
                crashType,
                lines.first().dateAndTime,
                lines.joinToString("\n") { it.content }
            )
        } catch (e: Exception) {
            AppCrash(null, crashedAppPackageName, crashType, lines.first().dateAndTime, lines.joinToString("\n") { it.content })
        }
    }
}