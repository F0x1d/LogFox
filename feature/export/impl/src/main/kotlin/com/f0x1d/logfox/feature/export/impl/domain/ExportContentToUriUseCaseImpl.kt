package com.f0x1d.logfox.feature.export.impl.domain

import android.net.Uri
import com.f0x1d.logfox.feature.export.api.data.ExportRepository
import com.f0x1d.logfox.feature.export.api.domain.ExportContentToUriUseCase
import javax.inject.Inject

internal class ExportContentToUriUseCaseImpl @Inject constructor(
    private val exportRepository: ExportRepository,
) : ExportContentToUriUseCase {

    override suspend fun invoke(uri: Uri, content: String): Result<Unit> = runCatching {
        exportRepository.writeContentToUri(uri, content)
    }
}
