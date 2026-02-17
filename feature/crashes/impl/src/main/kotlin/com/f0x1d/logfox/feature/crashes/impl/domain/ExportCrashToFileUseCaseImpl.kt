package com.f0x1d.logfox.feature.crashes.impl.domain

import android.net.Uri
import com.f0x1d.logfox.feature.crashes.api.data.CrashExportRepository
import com.f0x1d.logfox.feature.crashes.api.domain.ExportCrashToFileUseCase
import javax.inject.Inject

internal class ExportCrashToFileUseCaseImpl @Inject constructor(
    private val crashExportRepository: CrashExportRepository,
) : ExportCrashToFileUseCase {

    override suspend fun invoke(crashId: Long, uri: Uri): Result<Unit> = runCatching {
        crashExportRepository.exportToFile(crashId, uri)
    }
}
