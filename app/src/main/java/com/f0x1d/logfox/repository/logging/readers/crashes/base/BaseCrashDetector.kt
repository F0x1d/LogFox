package com.f0x1d.logfox.repository.logging.readers.crashes.base

import com.f0x1d.logfox.LogFoxApp
import com.f0x1d.logfox.database.entity.AppCrash
import com.f0x1d.logfox.database.entity.CrashType
import com.f0x1d.logfox.repository.logging.readers.base.LogsReader

abstract class BaseCrashDetector(
    private val collected: suspend (AppCrash, List<com.f0x1d.logfox.model.LogLine>) -> Unit
): LogsReader {

    protected abstract val crashType: CrashType
    protected open val commonTag: String? = null
    protected open val linesModifier: MutableList<com.f0x1d.logfox.model.LogLine>.() -> Unit = {
        commonTag?.also { tag ->
            removeAll { line -> line.tag != tag }
        }
    }

    private var collecting = false
    private var collectedFirstLine: com.f0x1d.logfox.model.LogLine? = null
    private val collectedLines = mutableListOf<com.f0x1d.logfox.model.LogLine>()
    private val defaultChecker = DefaultChecker()

    abstract fun foundFirstLine(line: com.f0x1d.logfox.model.LogLine): Boolean
    open fun stillCollecting(line: com.f0x1d.logfox.model.LogLine) = defaultChecker.collecting(collectedFirstLine, line)
    abstract fun packageFromCollected(lines: List<com.f0x1d.logfox.model.LogLine>): String

    override suspend fun readLine(line: com.f0x1d.logfox.model.LogLine) {
        if (!collecting) {
            checkFirstLine(line)
            return
        }

        if (stillCollecting(line)) {
            collectedLines.add(line)
        } else {
            linesModifier(collectedLines)
            collected(
                makeAppCrash(
                    crashedAppPackageName = packageFromCollected(collectedLines),
                    crashType = crashType,
                    lines = collectedLines
                ),
                collectedLines
            )

            collecting = false
            collectedFirstLine = null

            checkFirstLine(line)
        }
    }

    private fun checkFirstLine(line: com.f0x1d.logfox.model.LogLine) {
        if (foundFirstLine(line)) {
            collecting = true

            collectedFirstLine = line
            collectedLines.clear()
            collectedLines.add(line)
        }
    }

    private fun makeAppCrash(crashedAppPackageName: String, crashType: CrashType, lines: List<com.f0x1d.logfox.model.LogLine>) = LogFoxApp.instance.run {
        val appName = try {
            packageManager.getPackageInfo(crashedAppPackageName, 0).applicationInfo.let {
                packageManager.getApplicationLabel(it).toString()
            }
        } catch (e: Exception) {
            null
        }

        AppCrash(
            appName = appName,
            packageName = crashedAppPackageName,
            crashType = crashType,
            dateAndTime = lines.first().dateAndTime
        )
    }
}
