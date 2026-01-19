package com.f0x1d.logfox.feature.database.model

import java.io.File

data class AppCrash(
    val id: Long = 0,
    val appName: String?,
    val packageName: String,
    val crashType: CrashType,
    val dateAndTime: Long,
    val logFile: File? = null,
    val logDumpFile: File? = null,
) {
    val notificationId get() = (if (id == 0L) dateAndTime else id).toInt()

    fun deleteLogFile() = logFile?.delete()
    fun deleteDumpFile() = logDumpFile?.delete()

    fun deleteAssociatedFiles() {
        deleteLogFile()
        deleteDumpFile()
    }
}

enum class CrashType(val readableName: String) {
    JAVA("Java"),
    JNI("JNI"),
    ANR("ANR"),
}

data class AppCrashesCount(val lastCrash: AppCrash, val count: Int = 1)
