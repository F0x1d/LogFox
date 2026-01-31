package com.f0x1d.logfox.feature.export.api.domain

import android.net.Uri

interface ExportContentToUriUseCase {
    suspend operator fun invoke(uri: Uri, content: String): Result<Unit>
}
