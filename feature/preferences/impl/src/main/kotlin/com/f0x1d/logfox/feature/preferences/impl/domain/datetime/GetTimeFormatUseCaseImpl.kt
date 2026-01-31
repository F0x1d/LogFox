package com.f0x1d.logfox.feature.preferences.impl.domain.datetime

import com.f0x1d.logfox.feature.preferences.api.data.DateTimeSettingsRepository
import com.f0x1d.logfox.feature.preferences.api.domain.datetime.GetTimeFormatUseCase
import javax.inject.Inject

internal class GetTimeFormatUseCaseImpl @Inject constructor(
    private val dateTimeSettingsRepository: DateTimeSettingsRepository,
) : GetTimeFormatUseCase {

    override fun invoke(): String = dateTimeSettingsRepository.timeFormat().value
}
