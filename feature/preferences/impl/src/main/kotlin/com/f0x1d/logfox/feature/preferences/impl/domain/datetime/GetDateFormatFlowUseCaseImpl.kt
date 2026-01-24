package com.f0x1d.logfox.feature.preferences.impl.domain.datetime

import com.f0x1d.logfox.feature.preferences.data.DateTimeSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.datetime.GetDateFormatFlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetDateFormatFlowUseCaseImpl @Inject constructor(
    private val dateTimeSettingsRepository: DateTimeSettingsRepository,
) : GetDateFormatFlowUseCase {

    override fun invoke(): Flow<String> = dateTimeSettingsRepository.dateFormat()
}
