package com.f0x1d.logfox.feature.crashes.impl.domain

import com.f0x1d.logfox.feature.crashes.api.data.CrashesRepository
import com.f0x1d.logfox.feature.crashes.api.domain.DeleteCrashUseCase
import javax.inject.Inject

internal class DeleteCrashUseCaseImpl @Inject constructor(
    private val crashesRepository: CrashesRepository,
) : DeleteCrashUseCase {
    override suspend fun invoke(crashId: Long) {
        val crash = crashesRepository.getById(crashId) ?: return
        crashesRepository.delete(crash)
    }
}
