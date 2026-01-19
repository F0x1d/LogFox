package com.f0x1d.logfox.feature.crashes.impl.domain

import com.f0x1d.logfox.feature.crashes.api.data.CrashesRepository
import com.f0x1d.logfox.feature.crashes.api.domain.DeleteCrashUseCase
import com.f0x1d.logfox.feature.database.model.AppCrash
import javax.inject.Inject

internal class DeleteCrashUseCaseImpl @Inject constructor(
    private val crashesRepository: CrashesRepository,
) : DeleteCrashUseCase {
    override suspend fun invoke(appCrash: AppCrash) = crashesRepository.delete(appCrash)
}
