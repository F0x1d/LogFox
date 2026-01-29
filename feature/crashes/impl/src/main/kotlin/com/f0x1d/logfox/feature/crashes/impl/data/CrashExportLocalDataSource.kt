package com.f0x1d.logfox.feature.crashes.impl.data

import android.net.Uri
import java.io.File

internal interface CrashExportLocalDataSource {
    suspend fun writeToFile(uri: Uri, crashLog: String)
    suspend fun writeToZip(uri: Uri, deviceInfo: String?, crashLog: String?, logDumpFile: File?)
}
