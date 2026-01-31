package com.f0x1d.logfox.feature.filters.api.domain

import android.net.Uri
import com.f0x1d.logfox.feature.filters.api.model.UserFilter

interface ExportFiltersToUriUseCase {
    suspend operator fun invoke(uri: Uri, filters: List<UserFilter>): Result<Unit>
}
