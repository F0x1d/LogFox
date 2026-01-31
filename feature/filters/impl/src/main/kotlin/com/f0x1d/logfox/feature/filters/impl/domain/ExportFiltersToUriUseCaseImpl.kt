package com.f0x1d.logfox.feature.filters.impl.domain

import android.net.Uri
import com.f0x1d.logfox.feature.export.api.data.ExportRepository
import com.f0x1d.logfox.feature.filters.api.domain.ExportFiltersToUriUseCase
import com.f0x1d.logfox.feature.filters.api.model.UserFilter
import com.google.gson.Gson
import javax.inject.Inject

internal class ExportFiltersToUriUseCaseImpl @Inject constructor(
    private val exportRepository: ExportRepository,
    private val gson: Gson,
) : ExportFiltersToUriUseCase {

    override suspend fun invoke(uri: Uri, filters: List<UserFilter>): Result<Unit> = runCatching {
        exportRepository.writeContentToUri(uri, gson.toJson(filters))
    }
}
