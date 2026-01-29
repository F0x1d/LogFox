package com.f0x1d.logfox.feature.crashes.api.domain

import android.net.Uri

interface ExportCrashToFileUseCase {
    suspend operator fun invoke(uri: Uri, crashLog: String)
}
