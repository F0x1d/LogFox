package com.f0x1d.logfox.feature.crashes.impl.domain

import android.net.Uri
import com.f0x1d.logfox.feature.crashes.api.data.CrashExportRepository
import com.f0x1d.logfox.feature.crashes.api.domain.ExportCrashToZipUseCase
import com.f0x1d.logfox.feature.crashes.api.model.AppCrash
import javax.inject.Inject

internal class ExportCrashToZipUseCaseImpl @Inject constructor(
    private val crashExportRepository: CrashExportRepository,
) : ExportCrashToZipUseCase {

    override suspend fun invoke(uri: Uri, appCrash: AppCrash, crashLog: String?) {
        crashExportRepository.exportToZip(uri, appCrash, crashLog)
    }
}
