package com.f0x1d.logfox.feature.filters.api.domain

import android.net.Uri

interface ImportFiltersFromUriUseCase {
    suspend operator fun invoke(uri: Uri): Result<Unit>
}
