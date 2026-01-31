package com.f0x1d.logfox.feature.preferences.impl.domain.datetime

import com.f0x1d.logfox.feature.preferences.data.DateTimeSettingsRepository
import com.f0x1d.logfox.feature.preferences.domain.datetime.GetDateFormatUseCase
import javax.inject.Inject

internal class GetDateFormatUseCaseImpl @Inject constructor(
    private val dateTimeSettingsRepository: DateTimeSettingsRepository,
) : GetDateFormatUseCase {

    override fun invoke(): String = dateTimeSettingsRepository.dateFormat().value
}
