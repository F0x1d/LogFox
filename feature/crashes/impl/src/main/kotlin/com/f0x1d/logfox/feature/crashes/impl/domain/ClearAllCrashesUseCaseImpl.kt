package com.f0x1d.logfox.feature.crashes.impl.domain

import com.f0x1d.logfox.feature.crashes.api.data.CrashesRepository
import com.f0x1d.logfox.feature.crashes.api.domain.ClearAllCrashesUseCase
import javax.inject.Inject

internal class ClearAllCrashesUseCaseImpl @Inject constructor(
    private val crashesRepository: CrashesRepository,
) : ClearAllCrashesUseCase {
    override suspend fun invoke() = crashesRepository.clear()
}
