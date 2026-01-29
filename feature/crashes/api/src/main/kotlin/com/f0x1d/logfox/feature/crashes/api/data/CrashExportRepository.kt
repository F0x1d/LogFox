package com.f0x1d.logfox.feature.crashes.api.data

import android.net.Uri
import com.f0x1d.logfox.feature.crashes.api.model.AppCrash

interface CrashExportRepository {
    suspend fun exportToFile(uri: Uri, crashLog: String)
    suspend fun exportToZip(uri: Uri, appCrash: AppCrash, crashLog: String?)
}
