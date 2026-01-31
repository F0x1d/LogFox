package com.f0x1d.logfox.feature.preferences.impl.domain.datetime

import com.f0x1d.logfox.feature.preferences.data.DateTimeSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.datetime.GetTimeFormatUseCase
import javax.inject.Inject

internal class GetTimeFormatUseCaseImpl @Inject constructor(
    private val dateTimeSettingsRepository: DateTimeSettingsRepository,
) : GetTimeFormatUseCase {

    override fun invoke(): String = dateTimeSettingsRepository.timeFormat().value
}
