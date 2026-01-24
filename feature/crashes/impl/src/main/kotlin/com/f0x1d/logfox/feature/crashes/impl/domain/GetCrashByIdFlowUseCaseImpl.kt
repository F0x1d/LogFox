package com.f0x1d.logfox.feature.crashes.impl.domain

import com.f0x1d.logfox.feature.crashes.api.data.CrashesRepository
import com.f0x1d.logfox.feature.crashes.api.domain.GetCrashByIdFlowUseCase
import com.f0x1d.logfox.feature.crashes.api.model.AppCrash
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetCrashByIdFlowUseCaseImpl @Inject constructor(
    private val crashesRepository: CrashesRepository,
) : GetCrashByIdFlowUseCase {
    override fun invoke(id: Long): Flow<AppCrash?> = crashesRepository.getByIdAsFlow(id)
}
