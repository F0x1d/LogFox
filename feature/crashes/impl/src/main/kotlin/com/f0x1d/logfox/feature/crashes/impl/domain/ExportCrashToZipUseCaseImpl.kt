package com.f0x1d.logfox.feature.crashes.impl.domain

import android.net.Uri
import com.f0x1d.logfox.feature.crashes.api.data.CrashExportRepository
import com.f0x1d.logfox.feature.crashes.api.domain.ExportCrashToZipUseCase
import javax.inject.Inject

internal class ExportCrashToZipUseCaseImpl @Inject constructor(
    private val crashExportRepository: CrashExportRepository,
) : ExportCrashToZipUseCase {

    override suspend fun invoke(crashId: Long, uri: Uri): Result<Unit> = runCatching {
        crashExportRepository.exportToZip(crashId, uri)
    }
}
