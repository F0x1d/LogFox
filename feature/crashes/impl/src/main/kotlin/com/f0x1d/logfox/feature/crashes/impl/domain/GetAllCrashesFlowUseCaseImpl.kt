package com.f0x1d.logfox.feature.crashes.impl.domain

import com.f0x1d.logfox.feature.crashes.api.data.CrashesRepository
import com.f0x1d.logfox.feature.crashes.api.domain.GetAllCrashesFlowUseCase
import com.f0x1d.logfox.feature.database.model.AppCrash
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetAllCrashesFlowUseCaseImpl @Inject constructor(
    private val crashesRepository: CrashesRepository,
) : GetAllCrashesFlowUseCase {
    override fun invoke(): Flow<List<AppCrash>> = crashesRepository.getAllAsFlow()
}
