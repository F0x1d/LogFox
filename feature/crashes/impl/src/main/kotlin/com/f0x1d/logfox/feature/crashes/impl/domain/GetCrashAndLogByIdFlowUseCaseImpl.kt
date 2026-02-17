package com.f0x1d.logfox.feature.crashes.impl.domain

import com.f0x1d.logfox.feature.crashes.api.data.CrashLogRepository
import com.f0x1d.logfox.feature.crashes.api.data.CrashesRepository
import com.f0x1d.logfox.feature.crashes.api.domain.GetCrashAndLogByIdFlowUseCase
import com.f0x1d.logfox.feature.crashes.api.model.AppCrash
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class GetCrashAndLogByIdFlowUseCaseImpl @Inject constructor(
    private val crashesRepository: CrashesRepository,
    private val crashLogRepository: CrashLogRepository,
) : GetCrashAndLogByIdFlowUseCase {
    override suspend fun invoke(id: Long): Flow<Pair<AppCrash, String?>?> =
        crashesRepository.getByIdAsFlow(id).map { appCrash ->
            appCrash?.let {
                it to runCatching {
                    crashLogRepository
                        .readCrashLog(it)
                        .joinToString(separator = "\n")
                }.getOrNull()
            }
        }
}
