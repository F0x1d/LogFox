package com.f0x1d.logfox.feature.export.impl.domain

import android.net.Uri
import com.f0x1d.logfox.feature.export.api.data.ExportRepository
import com.f0x1d.logfox.feature.export.api.domain.ImportContentFromUriUseCase
import javax.inject.Inject

internal class ImportContentFromUriUseCaseImpl @Inject constructor(
    private val exportRepository: ExportRepository,
) : ImportContentFromUriUseCase {

    override suspend fun invoke(uri: Uri): Result<String?> = runCatching {
        exportRepository.readContentFromUri(uri)
    }
}
