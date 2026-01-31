package com.f0x1d.logfox.feature.filters.impl.domain

import android.net.Uri
import com.f0x1d.logfox.feature.export.api.data.ExportRepository
import com.f0x1d.logfox.feature.filters.api.domain.CreateAllFiltersUseCase
import com.f0x1d.logfox.feature.filters.api.domain.ImportFiltersFromUriUseCase
import com.f0x1d.logfox.feature.filters.api.model.UserFilter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

internal class ImportFiltersFromUriUseCaseImpl @Inject constructor(
    private val exportRepository: ExportRepository,
    private val createAllFiltersUseCase: CreateAllFiltersUseCase,
    private val gson: Gson,
) : ImportFiltersFromUriUseCase {

    override suspend fun invoke(uri: Uri): Result<Unit> = runCatching {
        val content = exportRepository.readContentFromUri(uri) ?: return@runCatching
        val filters = gson.fromJson<List<UserFilter>>(
            content,
            object : TypeToken<List<UserFilter>>() {}.type,
        )
        createAllFiltersUseCase(filters)
    }
}
