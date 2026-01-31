package com.f0x1d.logfox.feature.export.api.domain

import android.net.Uri

interface ImportContentFromUriUseCase {
    suspend operator fun invoke(uri: Uri): Result<String?>
}
