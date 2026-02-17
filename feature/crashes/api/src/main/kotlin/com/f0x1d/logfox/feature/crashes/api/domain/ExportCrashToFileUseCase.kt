package com.f0x1d.logfox.feature.crashes.api.domain

import android.net.Uri

interface ExportCrashToFileUseCase {
    suspend operator fun invoke(crashId: Long, uri: Uri): Result<Unit>
}
